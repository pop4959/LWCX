/*
 * Copyright 2011 Tyler Blair. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ''AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and contributors and should not be interpreted as representing official policies,
 * either expressed or implied, of anybody else.
 */

package com.griefcraft.lwc;

import com.griefcraft.cache.DefaultsCache;
import com.griefcraft.bukkit.EntityBlock;
import com.griefcraft.cache.BlockCache;
import com.griefcraft.cache.ProtectionCache;
import com.griefcraft.integration.ICurrency;
import com.griefcraft.integration.IPermissions;
import com.griefcraft.integration.currency.NoCurrency;
import com.griefcraft.integration.currency.VaultCurrency;
import com.griefcraft.integration.permissions.SuperPermsPermissions;
import com.griefcraft.integration.permissions.VaultPermissions;
import com.griefcraft.io.BackupManager;
import com.griefcraft.listeners.LWCMCPCSupport;
import com.griefcraft.migration.ConfigPost300;
import com.griefcraft.migration.MySQLPost200;
import com.griefcraft.model.*;
import com.griefcraft.modules.admin.*;
import com.griefcraft.modules.confirm.ConfirmModule;
import com.griefcraft.modules.create.CreateModule;
import com.griefcraft.modules.credits.CreditsModule;
import com.griefcraft.modules.debug.DebugModule;
import com.griefcraft.modules.destroy.DestroyModule;
import com.griefcraft.modules.doors.DoorsModule;
import com.griefcraft.modules.economy.EconomyModule;
import com.griefcraft.modules.fix.FixModule;
import com.griefcraft.modules.flag.BaseFlagModule;
import com.griefcraft.modules.flag.MagnetModule;
import com.griefcraft.modules.free.FreeModule;
import com.griefcraft.modules.history.HistoryModule;
import com.griefcraft.modules.info.InfoModule;
import com.griefcraft.modules.limits.LimitsModule;
import com.griefcraft.modules.limits.LimitsV2;
import com.griefcraft.modules.modes.*;
import com.griefcraft.modules.modify.ModifyModule;
import com.griefcraft.modules.modifydefault.DefaultModule;
import com.griefcraft.modules.owners.OwnersModule;
import com.griefcraft.modules.pluginsupport.Factions;
import com.griefcraft.modules.pluginsupport.Towny;
import com.griefcraft.modules.pluginsupport.WorldGuard;
import com.griefcraft.modules.redstone.RedstoneModule;
import com.griefcraft.modules.setup.BaseSetupModule;
import com.griefcraft.modules.setup.DatabaseSetupModule;
import com.griefcraft.modules.setup.LimitsSetup;
import com.griefcraft.modules.unlock.UnlockModule;
import com.griefcraft.scripting.Module;
import com.griefcraft.scripting.ModuleLoader;
import com.griefcraft.scripting.event.LWCAccessEvent;
import com.griefcraft.scripting.event.LWCReloadEvent;
import com.griefcraft.scripting.event.LWCSendLocaleEvent;
import com.griefcraft.sql.Database;
import com.griefcraft.sql.PhysDB;
import com.griefcraft.util.*;
import com.griefcraft.util.config.Configuration;
import com.griefcraft.util.matchers.DoubleChestMatcher;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.type.Chest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class LWC {

    /**
     * If LWC is currently enabled
     */
    public static boolean ENABLED = false;

    /**
     * The current instance of LWC
     */
    private static LWC instance;

    /**
     * Core LWC configuration
     */
    private Configuration configuration;

    /**
     * The module loader
     */
    private final ModuleLoader moduleLoader;

    /**
     * The manager of backups
     */
    private final BackupManager backupManager;

    /**
     * The protection cache
     */
    private final ProtectionCache protectionCache;

    /**
     * The defaults cache
     */
    private final DefaultsCache defaultsCache;

    /**
     * Physical database instance
     */
    private PhysDB physicalDatabase;

    /**
     * Plugin instance
     */
    private LWCPlugin plugin;

    /**
     * Updates to the database that can be ran on a seperate thread
     */
    private DatabaseThread databaseThread;

    /**
     * The permissions handler
     */
    private IPermissions permissions;

    /**
     * The currency handler
     */
    private ICurrency currency;

    /**
     * Protection configuration cache
     */
    private final Map<String, String> protectionConfigurationCache = new HashMap<>();

    /**
     * Whether alternative-hopper-protection is enabled
     */
    private boolean alternativeHoppers;

    public LWC(LWCPlugin plugin) {
        this.plugin = plugin;
        LWC.instance = this;
        configuration = Configuration.load("core.yml");
        alternativeHoppers = configuration.getBoolean("optional.alternativeHopperProtection", false);
        protectionCache = new ProtectionCache(this);
        defaultsCache = new DefaultsCache(this);
        backupManager = new BackupManager();
        moduleLoader = new ModuleLoader(this);
    }

    /**
     * Get the currently loaded LWC instance
     *
     * @return
     */
    public static LWC getInstance() {
        return instance;
    }

    /**
     * Get a string representation of a block type
     *
     * @param id
     * @return
     */

    /**
     * Get a string representation of a block material
     *
     * @param material
     * @return
     */
    public static String materialToString(Material material) {
        if (material != null) {
            String materialName = normalizeMaterialName(material);

            // attempt to match the locale
            String locale = LWC.getInstance().getPlugin().getMessageParser().parseMessage(materialName.toLowerCase());

            // if it starts with UNKNOWN_LOCALE, use the default material name
            if (locale == null) {
                locale = materialName;
            }

            locale = locale.replace('_', ' ');
            return StringUtil.capitalizeFirstLetter(locale);
        }

        return "";
    }

    /**
     * <p>Normalize a name to a more readable and usable form.</p>
     *
     * E.g sign_post/wall_sign = Sign, furnace/burning_furnace = Furnace,
     * iron_door_block = iron_door
     *
     * @param material
     * @return
     */
    public static String normalizeMaterialName(Material material) {
        String name = StringUtils.replace(material.toString().toLowerCase(), "block", "");

        // some name normalizations
        if (name.contains("sign")) {
            name = "Sign";
        }

        if (name.contains("furnace")) {
            name = "furnace";
        }

        if (name.endsWith("_")) {
            name = name.substring(0, name.length() - 1);
        }

        return name.toLowerCase();
    }

    /**
     * Restore the direction the block is facing for when 1.8 broke it
     *
     * @param block
     */
    public void adjustChestDirection(Block block, BlockFace face) { // TODO: this probably doesn't work currently
        if (block.getType() != Material.CHEST) {
            return;
        }

        // Is there a double chest?
        Block doubleChest = findAdjacentDoubleChest(block);

        // Calculate the data byte to set
        @SuppressWarnings("unused")
        byte data = 0;

        switch (face) {
            case NORTH:
                data = 4;
                break;

            case SOUTH:
                data = 5;
                break;

            case EAST:
                data = 2;
                break;

            case WEST:
                data = 3;
                break;
            default:
                break;
        }

        // set the data for both sides of the chest
    }

    /**
     * Look for a double chest adjacent to a chest
     *
     * @param block
     * @return
     */
    public Block findAdjacentDoubleChest(Block block) {
        if (!DoubleChestMatcher.PROTECTABLES_CHESTS.contains(block.getType())) {
            throw new UnsupportedOperationException(
                    "findAdjacentDoubleChest() cannot be called on a: " + block.getType());
        }

        BlockState baseBlockState = block.getState();
        Chest baseBlockData = null;
        try {
            baseBlockData = (Chest) baseBlockState.getBlockData();
        } catch (ClassCastException e) {
            return null;
        }

        // get the block face for the neighboring chest if there is one
        BlockFace neighboringBlockFace = DoubleChestMatcher.getNeighboringChestBlockFace(baseBlockData);
        if (neighboringBlockFace == null) {
            return null;
        }

        // if the neighboring block is a chest as well, we have a match
        Block neighboringBlock = baseBlockState.getBlock().getRelative(neighboringBlockFace);
        if (baseBlockState.getType() == neighboringBlock.getType()) {
            return block;
        }

        return null;
    }

    /**
     * Check if a player has the ability to access a protection
     *
     * @param player
     * @param block
     * @return
     */
    public boolean canAccessProtection(Player player, Block block) {
        Protection protection = findProtection(block.getLocation());

        return protection != null && canAccessProtection(player, protection);
    }

    /**
     * Check if a player has the ability to access a protection
     *
     * @param player
     * @param x
     * @param y
     * @param z
     * @return
     */
    public boolean canAccessProtection(Player player, int x, int y, int z) {
        return canAccessProtection(player, physicalDatabase.loadProtection(player.getWorld().getName(), x, y, z));
    }

    /**
     * Check if a player has the ability to administrate a protection
     *
     * @param player
     * @param block
     * @return
     */
    public boolean canAdminProtection(Player player, Block block) {
        Protection protection = findProtection(block.getLocation());

        return protection != null && canAdminProtection(player, protection);
    }

    /**
     * Check if a player has the ability to administrate a protection
     *
     * @param player
     * @param protection
     * @return
     */
    public boolean canAdminProtection(Player player, Protection protection) {
        if (protection == null || player == null) {
            return true;
        }

        if (isAdmin(player)) {
            return true;
        }

        // Their access level
        Permission.Access access = Permission.Access.NONE;

        switch (protection.getType()) {
            case PUBLIC:
                if (protection.isOwner(player)) {
                    return true;
                }

                break;

            case PASSWORD:
                if (protection.isOwner(player) && wrapPlayer(player).getAccessibleProtections().contains(protection)) {
                    return true;
                }

                break;

            case PRIVATE:
            case DONATION:
            case DISPLAY:
                if (protection.isOwner(player)) {
                    return true;
                }

                if (protection.getAccess(player.getUniqueId().toString(),
                        Permission.Type.PLAYER) == Permission.Access.ADMIN) {
                    return true;
                }

                if (protection.getAccess(player.getName(), Permission.Type.PLAYER) == Permission.Access.ADMIN) {
                    return true;
                }

                for (String groupName : permissions.getGroups(player)) {
                    if (protection.getAccess(groupName, Permission.Type.GROUP) == Permission.Access.ADMIN) {
                        return true;
                    }
                }

                break;
            default:
                break;
        }

        // call the canAccessProtection hook
        LWCAccessEvent event = new LWCAccessEvent(player, protection, access);
        moduleLoader.dispatchEvent(event);

        return event.getAccess() == Permission.Access.ADMIN;
    }

    /**
     * Deposit items into an inventory chest Works with double chests.
     *
     * @param block
     * @param itemStack
     * @return remaining items (if any)
     */
    public Map<Integer, ItemStack> depositItems(Block block, ItemStack itemStack) {
        BlockState blockState;

        if ((blockState = block.getState()) != null && (blockState instanceof InventoryHolder)) {
            Block doubleChestBlock = null;
            InventoryHolder holder = (InventoryHolder) blockState;

            if (DoubleChestMatcher.PROTECTABLES_CHESTS.contains(block.getType())) {
                doubleChestBlock = findAdjacentDoubleChest(block);
            } else if (block.getType() == Material.FURNACE) {
                Inventory inventory = holder.getInventory();

                if (inventory.getItem(0) != null && inventory.getItem(1) != null) {
                    if (inventory.getItem(0).getType() == itemStack.getType()
                            && inventory.getItem(0)
                            .getMaxStackSize() >= (inventory.getItem(0).getAmount() + itemStack.getAmount())) {
                        // ItemStack fits on Slot 0
                    } else if (inventory.getItem(1).getType() == itemStack.getType()
                            && inventory.getItem(1)
                            .getMaxStackSize() >= (inventory.getItem(1).getAmount() + itemStack.getAmount())) {
                        // ItemStack fits on Slot 1
                    } else {
                        return null;
                    }
                }
            }

            if (itemStack.getAmount() <= 0) {
                return new HashMap<Integer, ItemStack>();
            }

            Map<Integer, ItemStack> remaining = holder.getInventory().addItem(itemStack);

            // we have remainders, deal with it
            if (remaining.size() > 0) {
                int key = remaining.keySet().iterator().next();
                ItemStack remainingItemStack = remaining.get(key);

                // is it a double chest ?????
                if (doubleChestBlock != null) {
                    InventoryHolder holder2 = (InventoryHolder) doubleChestBlock.getState();
                    remaining = holder2.getInventory().addItem(remainingItemStack);
                }

                // recheck remaining in the event of double chest being used
                if (remaining.size() > 0) {
                    return remaining;
                }
            }
        }

        return new HashMap<Integer, ItemStack>();
    }

    /**
     * Find a block that is adjacent to another block given a Material
     *
     * @param block
     * @param material
     * @param ignore
     * @return
     */
    public Block findAdjacentBlock(Block block, Material material, Block... ignore) {
        BlockFace[] faces = new BlockFace[]{BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST};
        List<Block> ignoreList = Arrays.asList(ignore);

        for (BlockFace face : faces) {
            Block adjacentBlock = block.getRelative(face);

            if (adjacentBlock.getType() == material && !ignoreList.contains(adjacentBlock)) {
                return adjacentBlock;
            }
        }

        return null;
    }

    /**
     * Find a block that is adjacent to another block on any of the block's 6 sides
     * given a Material
     *
     * @param block
     * @param material
     * @param ignore
     * @return
     */
    public Block findAdjacentBlockOnAllSides(Block block, Material material, Block... ignore) {
        BlockFace[] faces = new BlockFace[]{BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST,
                BlockFace.UP, BlockFace.DOWN};
        List<Block> ignoreList = Arrays.asList(ignore);

        for (BlockFace face : faces) {
            Block adjacentBlock = block.getRelative(face);

            if (adjacentBlock.getType() == material && !ignoreList.contains(adjacentBlock)) {
                return adjacentBlock;
            }
        }

        return null;
    }

    /**
     * Find a protection that is adjacent to another block on any of the block's 6
     * sides
     *
     * @param block
     * @param ignore
     * @return
     */
    public List<Protection> findAdjacentProtectionsOnAllSides(Block block, Block... ignore) {
        BlockFace[] faces = new BlockFace[]{BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST,
                BlockFace.UP, BlockFace.DOWN};
        List<Block> ignoreList = Arrays.asList(ignore);
        List<Protection> found = new ArrayList<Protection>();

        for (BlockFace face : faces) {
            Protection protection;
            Block adjacentBlock = block.getRelative(face);

            if (!ignoreList.contains(adjacentBlock.getLocation().getBlock())
                    && (protection = findProtection(adjacentBlock.getLocation())) != null) {
                found.add(protection);
            }
        }

        return found;
    }

    /**
     * Free some memory (LWC was disabled)
     */
    public void destruct() {
        // destroy the modules
        if (moduleLoader != null) {
            moduleLoader.shutdown();
        }

        if (databaseThread != null) {
            log("Flushing protection updates (" + databaseThread.size() + ")");
            databaseThread.stop();
            databaseThread = null;
        }

        if (physicalDatabase != null) {
            physicalDatabase.dispose();
        }

        physicalDatabase = null;

        // Clean ourselves up
        instance = null;
    }

    /**
     * Log a string
     *
     * @param str
     */
    public void log(String str) {
        plugin.getLogger().info(str);
    }

    /**
     * Encrypt a string using SHA1
     *
     * @param text
     * @return
     */
    public String encrypt(String text) {
        return StringUtil.encrypt(text);
    }

    /**
     * Enforce access to a protected block
     *
     * @param player
     * @param protection
     * @param block
     * @param hasAccess
     * @param notice     Should this method print a notice to the player? (some events may call this several times)
     * @return true if the player was granted access
     */
    public boolean enforceAccess(Player player, Protection protection, Block block, boolean hasAccess, boolean notice) {
        MessageParser parser = plugin.getMessageParser();

        if (block == null || protection == null) {
            return true;
        }

        // support for old protection dbs that do not contain the block id
        BlockCache blockCache = BlockCache.getInstance();
        if (protection.getBlockId() <= 0 && blockCache.getBlockId(block) != protection.getBlockId()) {
            protection.setBlockId(blockCache.getBlockId(block));
            protection.save();
        }

        // multi-world, update old protections
        if (protection.getWorld() == null || !block.getWorld().getName().equals(protection.getWorld())) {
            protection.setWorld(block.getWorld().getName());
            protection.save();
        }

        // update timestamp
        if (hasAccess) {
            long timestamp = System.currentTimeMillis() / 1000L;

            // check that they aren't an admin and if they are, they need to be
            // the owner of the protection or have access through /cmodify
            if (protection.isOwner(player)
                    || protection.getAccess(player.getName(), Permission.Type.PLAYER) != Permission.Access.NONE) {
                protection.setLastAccessed(timestamp);
                protection.save();
            }
        }

        if (notice) {
            boolean permShowNotices = hasPermission(player, "lwc.shownotices");
            if ((permShowNotices && configuration.getBoolean("core.showNotices", true))
                    && !Boolean.parseBoolean(resolveProtectionConfiguration(block, "quiet"))) {
                boolean isOwner = protection.isOwner(player);
                boolean showMyNotices = configuration.getBoolean("core.showMyNotices", true);

                if (!isOwner || (isOwner && (showMyNotices || permShowNotices))) {
                    String owner;

                    // replace your username with "you" if you own the protection
                    if (protection.isRealOwner(player)) {
                        owner = parser.parseMessage("you");
                    } else {
                        owner = UUIDRegistry.formatPlayerName(protection.getOwner(), false);
                    }

                    String blockName = materialToString(block);
                    String protectionTypeToString = parser.parseMessage(protection.typeToString().toLowerCase());

                    if (protectionTypeToString == null) {
                        protectionTypeToString = "Unknown";
                    }

                    if (parser.parseMessage("protection." + blockName.toLowerCase() + ".notice.protected") != null) {
                        sendLocaleToActionBar(player, "protection." + blockName.toLowerCase() + ".notice.protected", "type",
                                protectionTypeToString, "block", blockName, "owner", owner);
                    } else {
                        sendLocaleToActionBar(player, "protection.general.notice.protected", "type", protectionTypeToString, "block",
                                blockName, "owner", owner);
                    }
                }
            }

            if (!hasAccess) {
                Protection.Type type = protection.getType();

                if (type == Protection.Type.PASSWORD) {
                    sendLocaleToActionBar(player, "protection.general.locked.password", "block", materialToString(block), "owner",
                            protection.getOwner());
                } else if (type == Protection.Type.PRIVATE || type == Protection.Type.DONATION || type == Protection.Type.DISPLAY) {
                    sendLocaleToActionBar(player, "protection.general.locked.private", "block", materialToString(block),
                            "name", UUIDRegistry.isValidUUID(protection.getOwner()) ? UUIDRegistry.getName(UUID.fromString(protection.getOwner())) : protection.getOwner(),
                            "owner", protection.getOwner());
                }
            }
        }

        return hasAccess;
    }

    public boolean enforceAccess(Player player, Protection protection, Block block, boolean hasAccess) {
        return enforceAccess(player, protection, block, hasAccess, true);
    }

    /**
     * Check if a player has the ability to access a protection
     *
     * @param player
     * @param protection
     * @return
     */
    @SuppressWarnings("deprecation")
    public boolean canAccessProtection(Player player, Protection protection) {
        if (protection == null || player == null) {
            return true;
        }

        if (isAdmin(player)) {
            return true;
        }

        if (isMod(player)) {
            Player protectionOwner = protection.getBukkitOwner();

            if (protectionOwner == null) {
                return true;
            }

            if (!isAdmin(protectionOwner)) {
                return true;
            }
        }

        // Their access level
        Permission.Access access = Permission.Access.NONE;

        switch (protection.getType()) {
            case PUBLIC:
            case DONATION:
            case DISPLAY:
                return true;

            case PASSWORD:
                if (wrapPlayer(player).getAccessibleProtections().contains(protection)) {
                    return true;
                }

                break;

            case PRIVATE:
                if (protection.isOwner(player)) {
                    return true;
                }

                if (protection.getAccess(player.getUniqueId().toString(), Permission.Type.PLAYER)
                        .ordinal() >= Permission.Access.PLAYER.ordinal()) {
                    return true;
                }

                if (protection.getAccess(player.getName(), Permission.Type.PLAYER).ordinal() >= Permission.Access.PLAYER
                        .ordinal()) {
                    return true;
                }

                // Check for item keys
                for (Permission permission : protection.getPermissions()) {
                    if (permission.getType() != Permission.Type.ITEM) {
                        continue;
                    }

                    // Get the item they need to have
                    int item = Integer.parseInt(permission.getName());

                    // Are they wielding it?
                    BlockCache blockCache = BlockCache.getInstance();
                    if (blockCache.getBlockId(player.getItemInHand().getType()) == item) {
                        return true;
                    }
                }

                for (String groupName : permissions.getGroups(player)) {
                    if (protection.getAccess(groupName, Permission.Type.GROUP).ordinal() >= Permission.Access.PLAYER
                            .ordinal()) {
                        return true;
                    }
                }

                break;
            default:
                break;
        }

        // call the canAccessProtection hook
        LWCAccessEvent event = new LWCAccessEvent(player, protection, access);
        moduleLoader.dispatchEvent(event);

        return event.getAccess() == Permission.Access.PLAYER || event.getAccess() == Permission.Access.ADMIN;
    }

    /**
     * Check if a player can do mod functions on LWC
     *
     * @param player the player to check
     * @return true if the player is an LWC mod
     */
    public boolean isMod(Player player) {
        return hasPermission(player, "lwc.mod");
    }

    /**
     * Check if a player can do admin functions on LWC
     *
     * @param player the player to check
     * @return true if the player is an LWC admin
     */
    public boolean isAdmin(Player player) {
        if (player.isOp()) {
            if (configuration.getBoolean("core.opIsLWCAdmin", true)) {
                return true;
            }
        }

        return hasPermission(player, "lwc.admin");
    }

    /**
     * Check if a player has a permissions node
     *
     * @param player
     * @param node
     * @return
     */
    public boolean hasPermission(Player player, String node) {
        try {
            return player.hasPermission(node);
        } catch (NoSuchMethodError e) {
            // their server does not support Superperms..
            return !node.contains("admin") && !node.contains("mod");
        }
    }

    /**
     * Create an LWCPlayer object for a player
     *
     * @param sender
     * @return
     */
    public LWCPlayer wrapPlayer(CommandSender sender) {
        if (sender instanceof LWCPlayer) {
            return (LWCPlayer) sender;
        }

        if (!(sender instanceof Player)) {
            return null;
        }

        return LWCPlayer.getPlayer((Player) sender);
    }

    /**
     * Find a player in the given ranges
     *
     * @param minX
     * @param maxX
     * @param minY
     * @param maxY
     * @param minZ
     * @param maxZ
     * @return
     */
    public Player findPlayer(int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            Location location = player.getLocation();
            int plrX = location.getBlockX();
            int plrY = location.getBlockY();
            int plrZ = location.getBlockZ();

            // simple check of the ranges
            if (plrX >= minX && plrX <= maxX && plrY >= minY && plrY <= maxY && plrZ >= minZ && plrZ <= maxZ) {
                return player;
            }
        }

        return null;
    }

    /**
     * <p>Check the data of locale</p>
     * Returns null if invalid
     *
     * @param sender CommandSender
     * @param key    key of locale
     * @param args   Character to be rewritten
     *               Example: %block% = Chest
     * @return message or null
     */
    public String[] getLocaleMessage(CommandSender sender, String key, Object... args) {
        String[] message; // The message to send to the player
        MessageParser parser = plugin.getMessageParser();
        String parsed = parser.parseMessage(key, args);

        if (parsed == null) {
            return null; // Nothing to send
        }

        // message = parsed.split("\\n");
        message = StringUtils.split(parsed, '\n');

        if (message == null) {
            sender.sendMessage(Colors.Dark_Red + "LWC: " + Colors.White + "Undefined locale: \"" + Colors.Dark_Gray + key
                    + Colors.White + "\"");
            return null;
        }

        if (message.length > 0 && message[0].equalsIgnoreCase("null")) {
            return null;
        }

        return message;
    }

    /**
     * Send a locale to a player or console
     *
     * @param sender
     * @param key
     * @param args
     */
    public void sendLocale(CommandSender sender, String key, Object... args) {
        // The message to send to the player
        String[] message = getLocaleMessage(sender, key, args);

        String[] prefix = getLocaleMessage(sender, "prefix");

        if (message == null) {
            return;
        }

        // broadcast an event if they are a player
        if (sender instanceof Player && !key.equals("prefix")) {
            LWCSendLocaleEvent evt = new LWCSendLocaleEvent((Player) sender, key);
            moduleLoader.dispatchEvent(evt);

            // did they cancel it?
            if (evt.isCancelled()) {
                return;
            }
        }

        // Send the message!
        // sender.sendMessage(prefix + message);
        // prefix[0]: Only use the first
        for (String line : message) {
            if (ArrayUtils.isEmpty(prefix)) {
                sender.sendMessage(line);
            } else {
                sender.sendMessage(prefix[0] + line);
            }
        }
    }

    /**
     * Attempt to send a locale to a player's action bar (otherwise, send it normally)
     *
     * @param sender
     * @param key
     * @param args
     */
    public void sendLocaleToActionBar(CommandSender sender, String key, Object... args) {
        // The message to send to the player
        String[] message = getLocaleMessage(sender, key, args);

        String[] prefix = getLocaleMessage(sender, "prefix");

        if (message == null) {
            return;
        }

        // broadcast an event if they are a player
        if (sender instanceof Player && !key.equals("prefix")) {
            LWCSendLocaleEvent evt = new LWCSendLocaleEvent((Player) sender, key);
            moduleLoader.dispatchEvent(evt);

            // did they cancel it?
            if (evt.isCancelled()) {
                return;
            }
        }

        // Send the message!
        // sender.sendMessage(message);
        for (String line : message) {
            if (configuration.getBoolean("optional.useActionBar", false) && sender instanceof Player) {
                // Attempt to use the Spigot-API action bar if enabled, but use the normal chat message as a fallback.
                try {
                    // prefix[0]: Only use the first
                    if (ArrayUtils.isEmpty(prefix)) {
                        ((Player) sender).spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(line));
                    } else {
                        ((Player) sender).spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(prefix[0] + line));
                    }
                } catch (NoSuchMethodError e) {
                    // prefix[0]: Only use the first
                    if (ArrayUtils.isEmpty(prefix)) {
                        sender.sendMessage(line);
                    } else {
                        sender.sendMessage(prefix[0] + line);
                    }
                }
            } else {
                // prefix[0]: Only use the first
                if (ArrayUtils.isEmpty(prefix)) {
                    sender.sendMessage(line);
                } else {
                    sender.sendMessage(prefix[0] + line);
                }
            }
        }
    }

    /**
     * Get a string representation of a block's material
     *
     * @param block
     * @return
     */
    public static String materialToString(Block block) {
        if (block instanceof EntityBlock) {
            return StringUtils.capitalize(EntityBlock.getEntity().getType().name().replace("_", " "));
        }
        return materialToString(block.getType());
    }

    /**
     * Fast remove all protections for a player. ~100k protections / second.
     *
     * @param sender
     * @param player
     * @param shouldRemoveBlocks
     * @return
     */
    public int fastRemoveProtectionsByPlayer(CommandSender sender, String player, boolean shouldRemoveBlocks) {
        UUID uuid = UUIDRegistry.getUUID(player);
        int ret = fastRemoveProtections(sender,
                "Lower(owner) = Lower('" + (uuid != null ? uuid.toString() : player) + "')", shouldRemoveBlocks);

        // invalid any history objects associated with the player
        physicalDatabase.invalidateHistory(player);

        return ret;
    }

    @SuppressWarnings("deprecation")
    public static UUID convert(String uuid) {
        if (Bukkit.getOfflinePlayer(uuid).isOnline()) {
            return Bukkit.getPlayer(uuid).getUniqueId();
        }

        return Bukkit.getOfflinePlayer(uuid).getUniqueId();
    }

    /**
     * Remove protections very quickly with raw SQL calls
     *
     * @param sender
     * @param where
     * @param shouldRemoveBlocks
     * @return
     */
    public int fastRemoveProtections(CommandSender sender, String where, boolean shouldRemoveBlocks) {
        List<Integer> exemptedBlocks = configuration.getIntList("optional.exemptBlocks", new ArrayList<Integer>());
        List<Integer> toRemove = new LinkedList<>();
        List<Block> removeBlocks = null;
        int totalProtections = physicalDatabase.getProtectionCount();
        int completed = 0;
        int count = 0;

        // flush all changes to the database before working on the live database
        databaseThread.flush();

        if (shouldRemoveBlocks) {
            removeBlocks = new LinkedList<Block>();
        }

        if (where != null && !where.trim().isEmpty()) {
            where = " WHERE " + where.trim();
        }

        sender.sendMessage("Loading protections via STREAM mode");

        try {
            Statement resultStatement = physicalDatabase.getConnection().createStatement(ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_READ_ONLY);

            if (physicalDatabase.getType() == Database.Type.MySQL) {
                resultStatement.setFetchSize(Integer.MIN_VALUE);
            }

            String prefix = physicalDatabase.getPrefix();
            ResultSet result = resultStatement.executeQuery(
                    "SELECT id, owner, type, x, y, z, data, blockId, world, password, date, last_accessed FROM "
                            + prefix + "protections" + where);

            while (result.next()) {
                Protection protection = physicalDatabase.resolveProtection(result);
                World world = protection.getBukkitWorld();

                // check if the protection is exempt from being removed
                if (protection.hasFlag(Flag.Type.EXEMPTION) || exemptedBlocks.contains(protection.getBlockId())) {
                    continue;
                }

                count++;

                if (count % 100000 == 0 || count == totalProtections || count == 1) {
                    sender.sendMessage(Colors.Dark_Red + count + " / " + totalProtections);
                }

                if (world == null) {
                    continue;
                }

                // remove the protection
                toRemove.add(protection.getId());

                // remove the block ?
                if (shouldRemoveBlocks) {
                    removeBlocks.add(protection.getBlock());
                }

                // Remove it from the cache if it's in there
                Protection cached = protectionCache.getProtection(protection.getCacheKey());
                if (cached != null) {
                    cached.removeCache();
                }

                completed++;
            }

            // Close the streaming statement
            result.close();
            resultStatement.close();

            // flush all of the queries
            fullRemoveProtections(sender, toRemove);

            if (shouldRemoveBlocks) {
                removeBlocks(sender, removeBlocks);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return completed;
    }

    /**
     * Push removal changes to the database
     *
     * @param sender
     * @param toRemove
     */
    private void fullRemoveProtections(CommandSender sender, List<Integer> toRemove) throws SQLException {
        StringBuilder deleteProtectionsQuery = new StringBuilder();
        StringBuilder deleteHistoryQuery = new StringBuilder();
        int total = toRemove.size();
        int count = 0;

        // iterate over the items to remove
        Iterator<Integer> iter = toRemove.iterator();

        // the database prefix
        String prefix = getPhysicalDatabase().getPrefix();

        // create the statement to use
        Statement statement = getPhysicalDatabase().getConnection().createStatement();

        while (iter.hasNext()) {
            int protectionId = iter.next();

            if (count % 10000 == 0) {
                deleteProtectionsQuery.append("DELETE FROM ").append(prefix).append("protections WHERE id IN (")
                        .append(protectionId);
                deleteHistoryQuery.append("UPDATE ").append(prefix)
                        .append("history SET status = " + History.Status.INACTIVE.ordinal() + " WHERE protectionId IN(")
                        .append(protectionId);
            } else {
                deleteProtectionsQuery.append(",").append(protectionId);
                deleteHistoryQuery.append(",").append(protectionId);
            }

            if (count % 10000 == 9999 || count == (total - 1)) {
                deleteProtectionsQuery.append(")");
                deleteHistoryQuery.append(")");
                statement.executeUpdate(deleteProtectionsQuery.toString());
                statement.executeUpdate(deleteHistoryQuery.toString());
                deleteProtectionsQuery.setLength(0);
                deleteHistoryQuery.setLength(0);

                sender.sendMessage(Colors.Dark_Green + "REMOVED " + (count + 1) + " / " + total);
            }

            count++;
            physicalDatabase.decrementProtectionCount();
        }

        statement.close();
    }

    /**
     * Remove a list of blocks from the world
     *
     * @param sender
     * @param blocks
     */
    private void removeBlocks(CommandSender sender, List<Block> blocks) {
        int count = 0;

        for (Block block : blocks) {
            if (block == null || !isProtectable(block)) {
                continue;
            }

            // possibility of a double chest
            if (DoubleChestMatcher.PROTECTABLES_CHESTS.contains(block.getType())) {
                Block doubleChest = findAdjacentDoubleChest(block);

                if (doubleChest != null) {
                    removeInventory(doubleChest);
                    doubleChest.setType(Material.AIR);
                }
            }

            // remove the inventory from the block if it has one
            removeInventory(block);

            // and now remove the block
            block.setType(Material.AIR);

            count++;
        }

        sender.sendMessage("Removed " + count + " blocks from the world");
    }

    /**
     * Remove the inventory from a block
     *
     * @param block
     */
    private void removeInventory(Block block) {
        if (block == null) {
            return;
        }

        if (!(block.getState() instanceof InventoryHolder)) {
            return;
        }

        InventoryHolder holder = (InventoryHolder) block.getState();
        holder.getInventory().clear();
    }

    /**
     * Compares two blocks if they are equal
     *
     * @param block
     * @param block2
     * @return
     */
    public boolean blockEquals(Block block, Block block2) {
        return block.getType() == block2.getType() && block.getX() == block2.getX() && block.getY() == block2.getY()
                && block.getZ() == block2.getZ();
    }

    /**
     * Find a protection linked to the location
     *
     * @param location
     * @return
     */
    public Protection findProtection(Location location) {
        String cacheKey = protectionCache.cacheKey(location);

        if (protectionCache.isKnownNull(cacheKey)) {
            return null;
        }

        Protection protection = protectionCache.getProtection(cacheKey);

        return protection != null ? protection : findProtection(location.getBlock());
    }

    /**
     * Find a protection linked to the block
     *
     * @param block
     * @return
     */
    public Protection findProtection(Block block) {
        return findProtection(block.getState());
    }

    public Protection findProtection(BlockState block) {
        // If the block type is AIR, then we have a problem .. but attempt to
        // load a protection anyway
        // Note: this call stems from a very old bug in Bukkit that likely does
        // not exist anymore at all
        // but is kept just incase. At one point getBlock() in Bukkit would
        // sometimes say a block
        // is an air block even though the client and server sees it differently
        // (ie a chest).
        // This was of course very problematic!
        if (block != null) {
            if (block.getType() == Material.AIR || block instanceof EntityBlock) {
                // We won't be able to match any other blocks anyway, so the least
                // we can do is attempt to load a protection
                return physicalDatabase.loadProtection(block.getWorld().getName(), block.getX(), block.getY(),
                        block.getZ());
            }

            Protection found = null;
            try {
                // Create a protection finder
                ProtectionFinder finder = new ProtectionFinder(this);

                // Search for a protection
                boolean result = finder.matchBlocks(block);

                // We're done, load the possibly loaded protection
                if (result) {
                    found = finder.loadProtection();
                }

                if (found == null) {
                    protectionCache.addKnownNull(protectionCache.cacheKey(block.getLocation()));
                }
            } catch (Exception e) {
            }
            return found;
        }

        log("Block is null");
        return null;
    }

    /**
     * Find a protection linked to the block at [x, y, z]
     *
     * @param block
     * @param block2
     * @return
     */

    public boolean blockEquals(BlockState block, BlockState block2) {
        return block.getType() == block2.getType() && block.getX() == block2.getX() && block.getY() == block2.getY()
                && block.getZ() == block2.getZ();
    }

    public Protection findProtection(World world, int x, int y, int z) {
        if (world == null) {
            return null;
        }

        return findProtection(new Location(world, x, y, z));
    }

    /**
     * Matches all possible blocks that can be considered a 'protection' e.g
     * clicking a chest will match double chests, clicking a door or block below a
     * door matches the whole door
     *
     * @param state
     * @return the List of possible blocks
     */
    public boolean isProtectable(BlockState state) {
        Material material = state.getType();

        if (material == null) {
            return false;
        }

        return Boolean.parseBoolean(resolveProtectionConfiguration(state, "enabled"));
    }

    public boolean isProtectable(EntityType state) {

        return Boolean.parseBoolean(resolveProtectionConfiguration(state, "enabled"));
    }

    @SuppressWarnings("deprecation")
    public String resolveProtectionConfiguration(BlockState state, String node) {
        Material material = state.getType();
        String cacheKey = material.toString() + "-" + node;
        if (protectionConfigurationCache.containsKey(cacheKey)) {
            return protectionConfigurationCache.get(cacheKey);
        }

        List<String> names = new ArrayList<String>();

        String materialName = normalizeMaterialName(material);

        // add the names without the block data
        names.add(materialName);
        names.add(material.toString());
        names.add(material.toString().toLowerCase());

        if (materialName.contains("_")) { // Prefix wildcarding for shulker boxes & gates
            int i = materialName.indexOf("_") + 1;
            while (i > 0) {
                names.add("*_" + materialName.substring(i));
                names.add("*_" + materialName.substring(i).toLowerCase());
                i = materialName.indexOf("_", i) + 1;
            }
        }

        // Add the wildcards last so it can be overriden
        names.add("*");

        String value = configuration.getString("protections." + node);

        for (String name : names) {
            String temp = configuration.getString("protections.blocks." + name + "." + node);

            if (temp != null && !temp.isEmpty()) {
                value = temp;
                break;
            }
        }

        protectionConfigurationCache.put(cacheKey, value);
        return value;
    }

    public String resolveProtectionConfiguration(EntityType state, String node) {
        String cacheKey = state + "-" + node;
        if (protectionConfigurationCache.containsKey(cacheKey)) {
            return protectionConfigurationCache.get(cacheKey);
        }

        String value = configuration.getString("protections." + node);

        String temp = configuration.getString("protections.blocks." + state.name().toUpperCase() + "." + node);

        if (temp != null && !temp.isEmpty()) {
            value = temp;
        }

        protectionConfigurationCache.put(cacheKey, value);
        return value;
    }

    /**
     * Check if a player has either access to lwc.admin or the specified node
     *
     * @param sender
     * @param node
     * @return
     */
    public boolean hasAdminPermission(CommandSender sender, String node) {
        return isAdmin(sender) || hasPermission(sender, node, "lwc.admin");
    }

    /**
     * Check if a player is an LWC admin -- Console defaults to *YES*
     *
     * @param sender
     * @return
     */
    public boolean isAdmin(CommandSender sender) {
        return !(sender instanceof Player) || isAdmin((Player) sender);
    }

    /**
     * Check a player for a node, using a fallback as a default (e.g lwc.protect)
     *
     * @param sender
     * @param node
     * @param fallback
     * @return
     */
    public boolean hasPermission(CommandSender sender, String node, String... fallback) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;
        boolean hasNode = hasPermission(player, node);

        if (!hasNode) {
            for (String temp : fallback) {
                if (hasPermission(player, temp)) {
                    return true;
                }
            }
        }

        return hasNode;
    }

    /**
     * Check if a player has either access to lwc.protect or the specified node
     *
     * @param sender
     * @param node
     * @return
     */
    public boolean hasPlayerPermission(CommandSender sender, String node) {
        return hasPermission(sender, node, "lwc.protect");
    }

    /**
     * Check if a mode is enabled
     *
     * @param mode
     * @return
     */
    public boolean isModeEnabled(String mode) {
        return configuration.getBoolean("modes." + mode + ".enabled", true);
    }

    /**
     * Check if a mode is whitelisted for a player
     *
     * @param mode
     * @return
     */
    public boolean isModeWhitelisted(Player player, String mode) {
        return hasPermission(player, "lwc.mode." + mode, "lwc.allmodes");
    }

    /**
     * Check a block to see if it is protectable
     *
     * @param block
     * @return
     */
    public boolean isProtectable(Block block) {
        Material material = block.getType();
        if (block instanceof EntityBlock) {
            return Boolean.parseBoolean(resolveProtectionConfiguration(EntityBlock.getEntity().getType(), "enabled"));
        }

        if (material == null) {
            return false;
        }

        return Boolean.parseBoolean(resolveProtectionConfiguration(block, "enabled"));
    }

    /**
     * Get the appropriate config value for the block (protections.block.node)
     *
     * @param block
     * @param node
     * @return
     */
    public String resolveProtectionConfiguration(Block block, String node) {
        Material material = block.getType();
        if (material == null) {
            return null;
        }
        String cacheKey = material.toString() + "-" + node;
        if (protectionConfigurationCache.containsKey(cacheKey)) {
            return protectionConfigurationCache.get(cacheKey);
        }

        List<String> names = new ArrayList<>();

        String materialName = normalizeMaterialName(material);

        // add the names without the block data
        names.add(materialName);
        names.add(material.toString());
        names.add(material.toString().toLowerCase());

        if (materialName.contains("_")) { // Prefix wildcarding for shulker boxes & gates
            int i = materialName.indexOf("_") + 1;
            while (i > 0) {
                names.add("*_" + materialName.substring(i));
                names.add("*_" + materialName.substring(i).toLowerCase());
                i = materialName.indexOf("_", i) + 1;
            }
        }

        // Add the wildcards last so it can be overriden
        names.add("*");

        String value = configuration.getString("protections." + node);

        for (String name : names) {
            String temp = configuration.getString("protections.blocks." + name + "." + node);

            if (temp != null && !temp.isEmpty()) {
                value = temp;
                break;
            }
        }

        protectionConfigurationCache.put(cacheKey, value);
        return value;
    }

    /**
     * Get the appropriate config value for the block (protections.block.node)
     *
     * @param material
     * @param node
     * @return
     */
    public String resolveProtectionConfiguration(Material material, String node) {
        if (material == null) {
            return null;
        }
        String cacheKey = "00-" + material.toString() + "-" + node;
        if (protectionConfigurationCache.containsKey(cacheKey)) {
            return protectionConfigurationCache.get(cacheKey);
        }

        List<String> names = new ArrayList<>();

        String materialName = normalizeMaterialName(material);

        // add the name & the block id
        names.add(materialName);

        // add both upper and lower material name
        names.add(material.toString());
        names.add(material.toString().toLowerCase());

        if (materialName.contains("_")) { // Prefix wildcarding for shulker boxes & gates
            int i = materialName.indexOf("_") + 1;
            while (i > 0) {
                names.add("*_" + materialName.substring(i));
                names.add("*_" + materialName.substring(i).toLowerCase());
                i = materialName.indexOf("_", i) + 1;
            }
        }

        // Add the wildcards last so it can be overriden
        names.add("*");

        String value = configuration.getString("protections." + node);

        for (String name : names) {
            String temp = configuration.getString("protections.blocks." + name + "." + node);

            if (temp != null && !temp.isEmpty()) {
                value = temp;
                break;
            }
        }

        protectionConfigurationCache.put(cacheKey, value);
        return value;
    }

    /**
     * Load sqlite (done only when LWC is loaded so memory isn't used unnecessarily)
     */
    public void load() {
        configuration = Configuration.load("core.yml");
        registerCoreModules();

        // check for upgrade before everything else
        new ConfigPost300().run();
        plugin.loadDatabase();

        Statistics.init();

        physicalDatabase = new PhysDB();
        databaseThread = new DatabaseThread(this);

        // Permissions init
        permissions = new SuperPermsPermissions();

        if (resolvePlugin("Vault") != null) {
            permissions = new VaultPermissions();
        }

        // Currency init
        currency = new NoCurrency();

        if (resolvePlugin("Vault") != null) {
            currency = new VaultCurrency();
        }

        plugin.getUpdater().init();

        log("Connecting to " + Database.DefaultType);
        try {
            if (!physicalDatabase.connect()) {
                Bukkit.getPluginManager().disablePlugin(plugin);
                return;
            }
            physicalDatabase.load();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // check any major conversions
        new MySQLPost200().run();

        // precache protections
        physicalDatabase.precache();

        // Initialize the block cache
        BlockCache.getInstance().loadBlocks();

        // We are now done loading!
        moduleLoader.loadAll();

        // Should we try metrics?
    }

    /**
     * Register the core modules for LWC
     */
    private void registerCoreModules() {
        // MCPC
        registerModule(new LWCMCPCSupport(this));

        // core
        registerModule(new LimitsV2());
        registerModule(new LimitsModule());
        registerModule(new CreateModule());
        registerModule(new ModifyModule());
        registerModule(new DefaultModule());
        registerModule(new DestroyModule());
        registerModule(new FreeModule());
        registerModule(new InfoModule());
        registerModule(new UnlockModule());
        registerModule(new OwnersModule());
        registerModule(new DoorsModule());
        registerModule(new DebugModule());
        registerModule(new CreditsModule());
        registerModule(new FixModule());
        registerModule(new HistoryModule());
        registerModule(new ConfirmModule());

        // admin commands
        registerModule(new BaseAdminModule());
        registerModule(new AdminCache());
        registerModule(new AdminCleanup());
        registerModule(new AdminClear());
        registerModule(new AdminFind());
        registerModule(new AdminFlush());
        registerModule(new AdminForceOwner());
        registerModule(new AdminOwnerAll());
        registerModule(new AdminLocale());
        registerModule(new AdminPurge());
        registerModule(new AdminReload());
        registerModule(new AdminRemove());
        registerModule(new AdminReport());
        registerModule(new AdminVersion());
        registerModule(new AdminQuery());
        registerModule(new AdminPurgeBanned());
        registerModule(new AdminExpire());
        registerModule(new AdminDump());
        registerModule(new AdminRebuild());
        registerModule(new AdminBackup());
        registerModule(new AdminView());

        // /lwc setup
        registerModule(new BaseSetupModule());
        registerModule(new DatabaseSetupModule());
        registerModule(new LimitsSetup());

        // flags
        registerModule(new BaseFlagModule());
        registerModule(new RedstoneModule());
        registerModule(new MagnetModule());

        // modes
        registerModule(new BaseModeModule());
        registerModule(new PersistModule());
        registerModule(new DropTransferModule());
        registerModule(new NoSpamModule());
        registerModule(new NoLockModule());

        // non-core modules but are included with LWC anyway
        if (resolvePlugin("WorldGuard") != null) {
            registerModule(new WorldGuard());
        }

        if (resolvePlugin("Towny") != null) {
            registerModule(new Towny());
        }

        if (resolvePlugin("Vault") != null) {
            registerModule(new EconomyModule());
        }

        if (resolvePlugin("Factions") != null) {
            try {
                registerModule(new Factions());
            } catch (NoClassDefFoundError e) {
                this.log("Failed to hook into Factions!");
                this.log("Please make sure you are using an updated version of FactionsUUID.");
                this.log("https://www.spigotmc.org/resources/factionsuuid.1035/");
                e.printStackTrace();
            }
        }
    }

    /**
     * Register a module
     *
     * @param module
     */
    private void registerModule(Module module) {
        moduleLoader.registerModule(plugin, module);
    }

    /**
     * Get a plugin by the name. Does not have to be enabled, and will remain
     * disabled if it is disabled.
     *
     * @param name
     * @return
     */
    private Plugin resolvePlugin(String name) {
        Plugin temp = plugin.getServer().getPluginManager().getPlugin(name);

        if (temp == null) {
            return null;
        }

        return temp;
    }

    /**
     * Merge inventories into one
     *
     * @param blocks
     * @return
     */
    public ItemStack[] mergeInventories(List<Block> blocks) {
        ItemStack[] stacks = new ItemStack[54];
        int index = 0;

        try {
            for (Block block : blocks) {
                if (!(block.getState() instanceof InventoryHolder)) {
                    continue;
                }

                InventoryHolder holder = (InventoryHolder) block.getState();
                Inventory inventory = holder.getInventory();

                // Add all the items from this inventory
                for (ItemStack stack : inventory.getContents()) {
                    stacks[index] = stack;
                    index++;
                }
            }
        } catch (Exception e) {
            return mergeInventories(blocks);
        }

        return stacks;
    }

    /**
     * Process rights inputted for a protection and add or remove them to the given
     * protection
     *
     * @param sender
     * @param protection
     * @param arguments
     */
    public void processRightsModifications(CommandSender sender, Protection protection, String... arguments) {
        // Does it match a protection type?
        try {
            Protection.Type protectionType = Protection.Type.matchType(arguments[0]);

            if (protectionType != null) {

                if (!sender.hasPermission("lwc.create." + arguments[0]) && !sender.hasPermission("lwc.create") && !sender.hasPermission("lwc.protect")) {
                    return;
                }

                protection.setType(protectionType);
                protection.save();

                // If it's being passworded, we need to set the password
                if (protectionType == Protection.Type.PASSWORD) {
                    String password = StringUtil.join(arguments, 1);
                    protection.setPassword(encrypt(password));
                }

                sendLocale(sender, "protection.typechanged", "type",
                        plugin.getMessageParser().parseMessage(protectionType.toString().toLowerCase()));
                return;
            }
        } catch (IllegalArgumentException e) {
            // It's normal for this to be thrown if nothing was matched
        }

        for (String value : arguments) {
            boolean remove = false;
            boolean isAdmin = false;
            boolean ownerChange = false;
            Permission.Type type = Permission.Type.PLAYER;

            // Gracefully ignore id
            if (value.startsWith("id:")) {
                continue;
            }

            if (value.startsWith("-")) {
                remove = true;
                value = value.substring(1);
            }

            if (value.startsWith("@")) {
                isAdmin = true;
                value = value.substring(1);
            }

            if (value.toLowerCase().startsWith("p:")) {
                type = Permission.Type.PLAYER;
                value = value.substring(2);
            }

            if (value.toLowerCase().startsWith("g:")) {
                type = Permission.Type.GROUP;
                value = value.substring(2);
            }

            if (value.toLowerCase().startsWith("t:")) {
                type = Permission.Type.TOWN;
                value = value.substring(2);
            }

            if (value.toLowerCase().startsWith("town:")) {
                type = Permission.Type.TOWN;
                value = value.substring(5);
            }

            if (value.toLowerCase().startsWith("item:")) {
                type = Permission.Type.ITEM;
                value = value.substring(5);
            }

            if (value.toLowerCase().startsWith("r:")) {
                type = Permission.Type.REGION;
                value = value.substring(2);
            }

            if (value.toLowerCase().startsWith("region:")) {
                type = Permission.Type.REGION;
                value = value.substring(7);
            }

            if (value.toLowerCase().startsWith("owner:")) {
                type = Permission.Type.PLAYER;
                ownerChange = true;
                value = value.substring(6);
            }

            if (value.toLowerCase().startsWith("f:")) {
                type = Permission.Type.FACTION;
                value = value.substring(2);
            }

            if (value.toLowerCase().startsWith("faction:")) {
                type = Permission.Type.FACTION;
                value = value.substring(8);
            }

            if (value.trim().isEmpty()) {
                continue;
            }

            String localeChild = type.toString().toLowerCase();

            // Store the original value (keep player name / original input)
            final String originalValue = value;

            // If it's a player, convert it to UUID
            if (type == Permission.Type.PLAYER) {
                UUID uuid = UUIDRegistry.getUUID(value);

                if (uuid != null) {
                    value = uuid.toString();
                }
            }

            if (ownerChange) {
                protection.setOwner(value);
                protection.save();
                sendLocale(sender, "protection.interact.forceowner.finalize", "player", UUIDRegistry.formatPlayerName(originalValue, false));
                continue;
            }

            if (!remove) {
                Permission permission = new Permission(value, type);
                permission.setAccess(isAdmin ? Permission.Access.ADMIN : Permission.Access.PLAYER);

                // add it to the protection and queue it to be saved
                protection.addPermission(permission);
                protection.save();

                if (type == Permission.Type.PLAYER) {
                    sendLocale(sender, "protection.interact.rights.register." + localeChild, "name",
                            UUIDRegistry.formatPlayerName(value, false), "isadmin",
                            isAdmin ? "[" + Colors.Dark_Red + "ADMIN" + Colors.Gold + "]" : "");
                } else {
                    sendLocale(sender, "protection.interact.rights.register." + localeChild, "name", value, "isadmin",
                            isAdmin ? "[" + Colors.Dark_Red + "ADMIN" + Colors.Gold + "]" : "");
                }
            } else {
                protection.removePermissions(value, type);
                protection.save();

                if (type == Permission.Type.PLAYER) {
                    sendLocale(sender, "protection.interact.rights.remove." + localeChild, "name",
                            UUIDRegistry.formatPlayerName(value, false), "isadmin",
                            isAdmin ? "[" + Colors.Dark_Red + "ADMIN" + Colors.Gold + "]" : "");
                } else {
                    sendLocale(sender, "protection.interact.rights.remove." + localeChild, "name", value, "isadmin",
                            isAdmin ? "[" + Colors.Dark_Red + "ADMIN" + Colors.Gold + "]" : "");
                }
            }
        }
    }

    public void processDefaultModifications(CommandSender sender, String data) {
        String senderId = null;
        UUID uuid = UUIDRegistry.getUUID(sender.getName());
        if (uuid != null) {
            senderId = uuid.toString();
        } else {
            sendLocale(sender, "lwc.defaults.error");
            return;
        }
        Default defaults = new Default();
        defaults.setOwner(senderId);
        defaults.setData(data);
        defaults.save();
        if("-".equals(data)) {
            defaultsCache.clear(sender);
            sendLocale(sender, "lwc.defaults.cleared");
        } else {
            defaultsCache.set(sender, data);
            sendLocale(sender, "lwc.defaults.saved");
        }
    }

    /**
     * Reload internal data structures
     */
    public void reload() {
        plugin.loadLocales();
        plugin.loadEvents();
        protectionConfigurationCache.clear();
        Configuration.reload();
        alternativeHoppers = configuration.getBoolean("optional.alternativeHopperProtection", false);
        moduleLoader.dispatchEvent(new LWCReloadEvent());
    }

    /**
     * Reload the database
     */
    public void reloadDatabase() {
        try {
            databaseThread.flush();
            databaseThread.stop();
            physicalDatabase = new PhysDB();
            physicalDatabase.connect();
            physicalDatabase.load();
            databaseThread = new DatabaseThread(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove all modes if the player is not in persistent mode
     *
     * @param sender
     */
    public void removeModes(CommandSender sender) {
        if (sender instanceof Player) {
            Player bPlayer = (Player) sender;

            if (notInPersistentMode(bPlayer.getName())) {
                wrapPlayer(bPlayer).removeAllActions();
            }
        } else if (sender instanceof LWCPlayer) {
            removeModes(((LWCPlayer) sender).getBukkitPlayer());
        }
    }

    /**
     * Return if the player is in persistent mode
     *
     * @param player the player to check
     * @return true if the player is NOT in persistent mode
     */
    public boolean notInPersistentMode(String player) {
        return !wrapPlayer(Bukkit.getServer().getPlayer(player)).hasMode("persist");
    }

    /**
     * Send the full help to a player
     *
     * @param sender the player to send to
     */
    public void sendFullHelp(CommandSender sender) {
        sendLocale(sender, "help.basic");

        if (isAdmin(sender)) {
            sender.sendMessage("");
            sender.sendMessage(Colors.Dark_Red + "/lwc admin - Administration");
        }
    }

    /**
     * Send the simple usage of a command
     *
     * @param player
     * @param command
     */
    public void sendSimpleUsage(CommandSender player, String command) {
        sendLocale(player, "help.simpleusage", "command", command);
    }

    /**
     * @return the configuration object
     */
    public Configuration getConfiguration() {
        return configuration;
    }

    /**
     * @return the Currency handler
     */
    public ICurrency getCurrency() {
        return currency;
    }

    /**
     * @return the backup manager
     */
    public BackupManager getBackupManager() {
        return backupManager;
    }

    /**
     * @return the module loader
     */
    public ModuleLoader getModuleLoader() {
        return moduleLoader;
    }

    /**
     * @return the Permissions handler
     */
    public IPermissions getPermissions() {
        return permissions;
    }

    /**
     * @return physical database object
     */
    public PhysDB getPhysicalDatabase() {
        return physicalDatabase;
    }

    /**
     * @return the plugin class
     */
    public LWCPlugin getPlugin() {
        return plugin;
    }

    /**
     * @return the protection cache
     */
    public ProtectionCache getProtectionCache() {
        return protectionCache;
    }

    /**
     * @return the update thread
     */
    public DatabaseThread getDatabaseThread() {
        return databaseThread;
    }

    /**
     * @return the plugin version
     */
    public double getVersion() {
        return Double.parseDouble(plugin.getDescription().getVersion());
    }

    public DefaultsCache getDefaultsCache() {
        return defaultsCache;
    }

    /**
     * @return true if history logging is enabled
     */
    public boolean isHistoryEnabled() {
        return !configuration.getBoolean("core.disableHistory", false);
    }

    /**
     * @return true if alternative hopper protection is enabled
     */
    public boolean useAlternativeHopperProtection() {
        return alternativeHoppers;
    }

    public boolean enforceAccess(Player player, Protection protection, Entity entity, boolean hasAccess) {
        MessageParser parser = plugin.getMessageParser();
        if (entity == null || protection == null) {
            return true;
        }

        // support for old protection dbs that do not contain the block id
        BlockCache blockCache = BlockCache.getInstance();
        if (protection.getBlockId() <= 0
                && blockCache.getBlockId(EntityBlock.ENTITY_BLOCK_NAME) != protection.getBlockId()) {
            protection.setBlockId(blockCache.getBlockId(EntityBlock.ENTITY_BLOCK_NAME));
            protection.save();
        }

        // multi-world, update old protections
        if (protection.getWorld() == null || !entity.getWorld().getName().equals(protection.getWorld())) {
            protection.setWorld(entity.getWorld().getName());
            protection.save();
        }

        // update timestamp
        if (hasAccess) {
            long timestamp = System.currentTimeMillis() / 1000L;

            // check that they aren't an admin and if they are, they need to be
            // the owner of the protection or have access through /cmodify
            if (protection.isOwner(player)
                    || protection.getAccess(player.getName(), Permission.Type.PLAYER) != Permission.Access.NONE) {
                protection.setLastAccessed(timestamp);
                protection.save();
            }
        }

        boolean permShowNotices = hasPermission(player, "lwc.shownotices");
        if ((permShowNotices && configuration.getBoolean("core.showNotices", true))
                && !Boolean.parseBoolean(resolveProtectionConfiguration(entity.getType(), "quiet"))) {
            boolean isOwner = protection.isOwner(player);
            boolean showMyNotices = configuration.getBoolean("core.showMyNotices", true);

            if (!isOwner || (isOwner && (showMyNotices || permShowNotices))) {
                String owner;

                // replace your username with "you" if you own the protection
                if (protection.isRealOwner(player)) {
                    owner = parser.parseMessage("you");
                } else {
                    owner = UUIDRegistry.formatPlayerName(protection.getOwner(), false);
                }

                String blockName = entity.getType().name();
                String protectionTypeToString = parser.parseMessage(protection.typeToString().toLowerCase());

                if (protectionTypeToString == null) {
                    protectionTypeToString = "Unknown";
                }

                if (parser.parseMessage("protection." + blockName.toLowerCase() + ".notice.protected") != null) {
                    sendLocaleToActionBar(player, "protection." + blockName.toLowerCase() + ".notice.protected", "type",
                            protectionTypeToString, "block", blockName, "owner", owner);
                } else {
                    sendLocaleToActionBar(player, "protection.general.notice.protected", "type", protectionTypeToString, "block",
                            blockName, "owner", owner);
                }
            }
        }

        if (!hasAccess) {
            String blockName = entity.getType().name();
            Protection.Type type = protection.getType();

            if (type == Protection.Type.PASSWORD) {
                sendLocaleToActionBar(player, "protection.general.locked.password", "block", blockName, "owner",
                        protection.getOwner());
            } else if (type == Protection.Type.PRIVATE || type == Protection.Type.DONATION) {
                sendLocaleToActionBar(player, "protection.general.locked.private", "block", blockName,
                        "name", UUIDRegistry.isValidUUID(protection.getOwner()) ? UUIDRegistry.getName(UUID.fromString(protection.getOwner())) : protection.getOwner(),
                        "owner", protection.getOwner());
            }
        }

        return hasAccess;
    }

}
