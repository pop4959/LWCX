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

package com.griefcraft.listeners;

import com.griefcraft.cache.BlockCache;
import com.griefcraft.cache.ProtectionCache;
import com.griefcraft.lwc.LWC;
import com.griefcraft.lwc.LWCPlugin;
import com.griefcraft.model.Protection;
import com.griefcraft.scripting.event.LWCProtectionDestroyEvent;
import com.griefcraft.scripting.event.LWCProtectionRegisterEvent;
import com.griefcraft.scripting.event.LWCProtectionRegistrationPostEvent;
import com.griefcraft.scripting.event.LWCRedstoneEvent;
import com.griefcraft.util.Colors;
import com.griefcraft.util.MaterialUtil;
import com.griefcraft.util.matchers.DoubleChestMatcher;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.type.Chest;
import org.bukkit.block.data.type.Piston;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.world.StructureGrowEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LWCBlockListener implements Listener {

    /**
     * The plugin instance
     */
    private LWCPlugin plugin;

    /**
     * A set of blacklisted blocks
     */
    private final Set<Material> blacklistedBlocks = new HashSet<>();

    public LWCBlockListener(LWCPlugin plugin) {
        this.plugin = plugin;
        loadAndProcessConfig();
    }

    public static final BlockFace[] POSSIBLE_FACES = {BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST,
            BlockFace.UP, BlockFace.DOWN};

    @EventHandler
    public void onBlockRedstoneChange(BlockRedstoneEvent event) {
        if (!LWC.ENABLED) {
            return;
        }

        LWC lwc = plugin.getLWC();
        Block block = event.getBlock();

        if (block == null) {
            return;
        }

        Protection protection = lwc.findProtection(block.getLocation());

        if (protection == null) {
            return;
        }

        LWCRedstoneEvent evt = new LWCRedstoneEvent(event, protection);
        lwc.getModuleLoader().dispatchEvent(evt);

        if (evt.isCancelled()) {
            event.setNewCurrent(event.getOldCurrent());
        }
    }

    @EventHandler
    public void onStructureGrow(StructureGrowEvent event) {
        if (!LWC.ENABLED) {
            return;
        }

        LWC lwc = LWC.getInstance();
        // the blocks that were changed / replaced
        List<BlockState> blocks = event.getBlocks();

        for (BlockState block : blocks) {
            if (!lwc.isProtectable(block.getBlock())) {
                continue;
            }
            // we don't have the block id of the block before it
            // so we have to do some raw lookups (these are usually cache hits
            // however, at least!)
            Protection protection = lwc.getPhysicalDatabase().loadProtection(block.getWorld().getName(), block.getX(),
                    block.getY(), block.getZ());
            if (protection != null) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (!LWC.ENABLED) {
            return;
        }

        LWC lwc = LWC.getInstance();

        Block block = event.getBlock();
        if (!lwc.isProtectable(block)) {
            return;
        }

        Protection protection = lwc.findProtection(block);
        if (protection != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        if (!LWC.ENABLED || event.isCancelled()) {
            return;
        }

        LWC lwc = plugin.getLWC();
        Block block = event.getBlock();
        Player player = event.getPlayer();

        if (block == null) {
            return;
        }

        Protection protection = lwc.findProtection(block.getLocation());

        if (protection == null) {
            return;
        }

        boolean canAccess = lwc.canAccessProtection(player, protection);

        if (!canAccess) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!LWC.ENABLED || event.isCancelled()) {
            return;
        }

        LWC lwc = plugin.getLWC();
        Player player = event.getPlayer();
        Block block = event.getBlock();

        boolean ignoreBlockDestruction = Boolean
                .parseBoolean(lwc.resolveProtectionConfiguration(block, "ignoreBlockDestruction"));

        if (ignoreBlockDestruction) {
            return;
        }

        ProtectionCache cache = lwc.getProtectionCache();
        String cacheKey = cache.cacheKey(block.getLocation());

        // In the event they place a block, remove any known nulls there
        if (cache.isKnownNull(cacheKey)) {
            cache.remove(cacheKey);
        }

        Protection protection = lwc.findProtection(block.getLocation());

        if (protection == null) {
            return;
        }

        boolean canAccess = lwc.canAccessProtection(player, protection);
        boolean canAdmin = lwc.canAdminProtection(player, protection);

        // when destroying a chest, it's possible they are also destroying a
        // double chest
        // in the event they're trying to destroy a double chest, we should just
        // move
        // the protection to the chest that is not destroyed, if it is not that
        // one already.
        if (protection.isOwner(player) && DoubleChestMatcher.PROTECTABLES_CHESTS.contains(block.getType())) {
            Block doubleChest = lwc.findAdjacentDoubleChest(block);

            if (doubleChest != null) {
                // if they destroyed the protected block we want to move it aye?
                if (lwc.blockEquals(protection.getBlock(), block)) {
                    // correct the block
                    BlockCache blockCache = BlockCache.getInstance();
                    protection.setBlockId(blockCache.getBlockId(doubleChest));
                    protection.setX(doubleChest.getX());
                    protection.setY(doubleChest.getY());
                    protection.setZ(doubleChest.getZ());
                    protection.saveNow();
                }

                // Repair the cache
                protection.radiusRemoveCache();

                if (protection.getProtectionFinder() != null) {
                    protection.getProtectionFinder().removeBlock(block.getState());
                }

                lwc.getProtectionCache().addProtection(protection);

                return;
            }
        }

        try {
            LWCProtectionDestroyEvent evt = new LWCProtectionDestroyEvent(player, protection,
                    LWCProtectionDestroyEvent.Method.BLOCK_DESTRUCTION, canAccess, canAdmin);
            lwc.getModuleLoader().dispatchEvent(evt);

            if (evt.isCancelled() || !canAccess) {
                event.setCancelled(true);
            }
        } catch (Exception e) {
            event.setCancelled(true);
            lwc.sendLocale(player, "protection.internalerror", "id", "BLOCK_BREAK");
            e.printStackTrace();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockMultiPlace(BlockMultiPlaceEvent event) {
        LWC lwc = plugin.getLWC();
        Block block = event.getBlock();

        if (block.getType().name().contains("_BED")) {
            for (BlockState state : event.getReplacedBlockStates()) {
                Protection protection = lwc.findProtection(state);

                if (protection != null) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockFromTo(BlockFromToEvent event) {
        Block block = event.getBlock();
        LWC lwc = this.plugin.getLWC();
        if (block.getType() == Material.WATER) {
            if (lwc.findProtection(event.getToBlock().getLocation()) != null) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        if ((!LWC.ENABLED) || (event.isCancelled())) {
            return;
        }
        LWC lwc = this.plugin.getLWC();
        Block pistonBlock = event.getBlock();
        Piston piston = null;
        try {
            piston = (Piston) pistonBlock.getBlockData();
        } catch (ClassCastException e) {
            return;
        }
        BlockFace facing = piston.getFacing();
        Block pulledBlock = pistonBlock.getRelative(facing).getRelative(facing);
        Protection protection = lwc.findProtection(pulledBlock);
        if (protection != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        if (!LWC.ENABLED || event.isCancelled()) {
            return;
        }
        LWC lwc = plugin.getLWC();
        Block pistonBlock = event.getBlock();
        Piston piston = null;
        try {
            piston = (Piston) pistonBlock.getBlockData();
        } catch (ClassCastException e) {
            return;
        }
        BlockFace facing = piston.getFacing();
        Block pushedBlock = pistonBlock.getRelative(facing);
        Protection protection = lwc.findProtection(pushedBlock);
        if (protection != null) {
            event.setCancelled(true);
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!LWC.ENABLED) {
            return;
        }

        LWC lwc = plugin.getLWC();
        Player player = event.getPlayer();
        Block block = event.getBlockPlaced();

        ProtectionCache cache = lwc.getProtectionCache();
        String cacheKey = cache.cacheKey(block.getLocation());

        // In the event they place a block, remove any known nulls there
        if (cache.isKnownNull(cacheKey)) {
            cache.remove(cacheKey);
        }

        // check if the block is blacklisted
        if (blacklistedBlocks.contains(block.getType())) {
            // it's blacklisted, check for a protected chest
            for (Protection protection : lwc.findAdjacentProtectionsOnAllSides(block)) {
                if (protection != null) {
                    if (!lwc.canAccessProtection(player, protection)
                            || (protection.getType() == Protection.Type.DONATION
                            && !lwc.canAdminProtection(player, protection))) {
                        // they can't access the protection ..
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }

    /**
     * Used for auto registering placed protections
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockPlaceMonitor(BlockPlaceEvent event) {
        if (!LWC.ENABLED) {
            return;
        }

        LWC lwc = plugin.getLWC();
        Player player = event.getPlayer();
        Block block = event.getBlockPlaced();

        // Update the cache if a protection is matched here
        Protection current = lwc.findProtection(block.getLocation());
        if (current != null) {
            if (!current.isBlockInWorld()) {
                // Removing corrupted protection
                current.remove();
            } else {
                if (current.getProtectionFinder() != null) {
                    current.getProtectionFinder().fullMatchBlocks();
                    lwc.getProtectionCache().addProtection(current);
                }
                return;
            }
        }

        // The placable block must be protectable
        if (!lwc.isProtectable(block)) {
            return;
        }

        String autoRegisterType = lwc.resolveProtectionConfiguration(block, "autoRegister");

        // is it auto protectable?
        if ((!lwc.hasPermission(player, "lwc.autoprotect") && !autoRegisterType.equalsIgnoreCase("private")
                && !autoRegisterType.equalsIgnoreCase("public")) || autoRegisterType.equalsIgnoreCase("false")) {
            return;
        }

        if (!lwc.hasPermission(player, "lwc.create." + autoRegisterType, "lwc.create", "lwc.protect")) {
            return;
        }

        // set the auto register type if they have the perm node
        if (lwc.hasPermission(player, "lwc.autoprotect")) {
            autoRegisterType = "private";
        }

        // Parse the type
        Protection.Type type;

        try {
            type = Protection.Type.valueOf(autoRegisterType.toUpperCase());
        } catch (IllegalArgumentException e) {
            // No auto protect type found
            return;
        }

        // Is it okay?
        if (type == null) {
            player.sendMessage(Colors.Red + "LWC_INVALID_CONFIG_autoRegister");
            return;
        }

        // If it's a chest, make sure they aren't placing it beside an already registered chest
        BlockState blockState = block.getState();
        Chest chestData = null;
        try {
            chestData = (Chest) blockState.getBlockData();
        } catch (ClassCastException e) {
        }
        if (chestData != null) {
            BlockFace neighboringChestBlockFace = DoubleChestMatcher.getNeighboringChestBlockFace(chestData);
            if (neighboringChestBlockFace != null) {
                // They're placing it beside a chest, check if it's already protected
                Block neighboringBlock = block.getRelative(neighboringChestBlockFace);
                lwc.getProtectionCache().addProtection(current);
                if (neighboringBlock.getType() == blockState.getBlock().getType()) {
                    if (lwc.findProtection(neighboringBlock.getLocation()) != null) {
                        return;
                    }
                }
            }
        }

        try {
            LWCProtectionRegisterEvent evt = new LWCProtectionRegisterEvent(player, block);
            lwc.getModuleLoader().dispatchEvent(evt);

            // something cancelled registration
            if (evt.isCancelled()) {
                return;
            }

            // All good!
            BlockCache blockCache = BlockCache.getInstance();
            int blockId = blockCache.getBlockId(block);
            if (blockId < 0) {
                return;
            }
            Protection protection = lwc.getPhysicalDatabase().registerProtection(blockId, type,
                    block.getWorld().getName(), player.getUniqueId().toString(), "", block.getX(), block.getY(),
                    block.getZ());

            if (!Boolean.parseBoolean(lwc.resolveProtectionConfiguration(block, "quiet"))) {
                lwc.sendLocaleToActionBar(player, "protection.onplace.create.finalize", "type",
                        lwc.getPlugin().getMessageParser().parseMessage(autoRegisterType.toLowerCase()), "block",
                        LWC.materialToString(block));
            }

            if (protection != null) {
                lwc.getModuleLoader().dispatchEvent(new LWCProtectionRegistrationPostEvent(protection));
            }
            protection.saveNow();
        } catch (Exception e) {
            lwc.sendLocale(player, "protection.internalerror", "id", "PLAYER_INTERACT");
            e.printStackTrace();
        }
    }

    /**
     * Load and process the configuration
     */
    public void loadAndProcessConfig() {
        List<String> blocks = LWC.getInstance().getConfiguration().getStringList("optional.blacklistedBlocks",
                new ArrayList<String>());
        for (String block : blocks) {
            int legacyId;
            try {
                legacyId = Integer.parseInt(block.trim().split(":")[0]);
            } catch (Exception e) {
                legacyId = -1;
            }
            Material material = MaterialUtil.getMaterialById(legacyId);
            if (material != null) {
                blacklistedBlocks.add(material);
            } else {
                material = Material.matchMaterial(block.trim().toUpperCase());
                if (material != null) {
                    blacklistedBlocks.add(material);
                }
            }
        }
    }

    /**
     * Get the hashcode of two integers
     *
     * @param int1
     * @param int2
     * @return
     */
    private int hashCode(int int1, int int2) {
        int hash = int1 * 17;
        hash *= 37 + int2;
        return hash;
    }

}
