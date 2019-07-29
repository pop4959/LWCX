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

package com.griefcraft.modules.limits;

import com.griefcraft.cache.BlockCache;
import com.griefcraft.lwc.LWC;
import com.griefcraft.model.Protection;
import com.griefcraft.scripting.JavaModule;
import com.griefcraft.scripting.event.LWCCommandEvent;
import com.griefcraft.scripting.event.LWCProtectionRegisterEvent;
import com.griefcraft.scripting.event.LWCReloadEvent;
import com.griefcraft.util.Colors;
import com.griefcraft.util.VersionUtil;
import com.griefcraft.util.config.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class LimitsV2 extends JavaModule {

    /**
     * The limit represented by unlimited
     */
    public final static int UNLIMITED = Integer.MAX_VALUE;

    /**
     * If the limits module is enabled
     */
    private boolean enabled = true;

    /**
     * The limits configuration
     */
    private final Configuration configuration = Configuration.load("limitsv2.yml");

    /**
     * A map of the default limits
     */
    private final List<Limit> defaultLimits = new LinkedList<>();

    /**
     * A map of all of the player limits
     */
    private final Map<String, List<Limit>> playerLimits = new HashMap<>();

    /**
     * A map of all of the group limits - downcasted to lowercase to simplify
     * comparisons
     */
    private final Map<String, List<Limit>> groupLimits = new HashMap<>();

    /**
     * A map mapping string representations of materials to their Material
     * counterpart
     */
    private final Map<String, Material> materialCache = new HashMap<>();

    /**
     * Set of materials for the various sign blocks used in the sign limit
     */
    private static final Set<Material> SIGNS;

    static {
        if (VersionUtil.getMinorVersion() > 13) {
            SIGNS = EnumSet.of(Material.OAK_WALL_SIGN, Material.BIRCH_WALL_SIGN,
                    Material.SPRUCE_WALL_SIGN, Material.JUNGLE_WALL_SIGN, Material.ACACIA_WALL_SIGN,
                    Material.DARK_OAK_WALL_SIGN, Material.OAK_SIGN, Material.BIRCH_SIGN, Material.SPRUCE_SIGN,
                    Material.JUNGLE_SIGN, Material.ACACIA_SIGN, Material.DARK_OAK_SIGN);
        } else {
            SIGNS = EnumSet.of(Objects.requireNonNull(Material.getMaterial("SIGN")), Objects.requireNonNull(Material.getMaterial("WALL_SIGN")));
        }
    }

    {
        for (Material material : Material.values()) {
            String materialName = LWC.normalizeMaterialName(material);

            // add the name & the block id
            materialCache.put(materialName, material);
//			materialCache.put(material.getId() + "", material);

            if (!materialName.equals(material.toString().toLowerCase())) {
                materialCache.put(material.toString().toLowerCase(), material);
            }
        }
    }

    public abstract class Limit {

        /**
         * The limit
         */
        private final int limit;

        public Limit(int limit) {
            this.limit = limit;
        }

        /**
         * Get the player's protection count that should be used with this limit
         *
         * @param player
         * @param material
         * @return
         */
        public abstract int getProtectionCount(Player player, Material material);

        /**
         * @return
         */
        public int getLimit() {
            return limit;
        }
    }

    public final class DefaultLimit extends Limit {

        public DefaultLimit(int limit) {
            super(limit);
        }

        @Override
        public int getProtectionCount(Player player, Material material) {
            return LWC.getInstance().getPhysicalDatabase().getProtectionCount(player.getName());
        }

    }

    public final class BlockLimit extends Limit {

        /**
         * The block material to limit
         */
        private final Material material;

        public BlockLimit(Material material, int limit) {
            super(limit);
            this.material = material;
        }

        @Override
        public int getProtectionCount(Player player, Material material) {
            BlockCache blockCache = BlockCache.getInstance();
            return LWC.getInstance().getPhysicalDatabase().getProtectionCount(player.getName(), blockCache.getBlockId(material));
        }

        /**
         * @return
         */
        public Material getMaterial() {
            return material;
        }

    }

    public final class EntityLimit extends Limit {

        /**
         * The block material to limit
         */

        public EntityLimit(EntityType material, int limit) {
            super(limit);
        }

        @Override
        public int getProtectionCount(Player player, Material material) {
            int i = 0;
            for (World world : Bukkit.getWorlds()) {
                for (Entity ent : world.getEntities()) {
                    int A = 50000 + ent.getUniqueId().hashCode();
                    Protection prot = LWC.getInstance().getPhysicalDatabase().loadProtection(ent.getWorld().getName(),
                            A, A, A);
                    if (prot.getBukkitOwner() == player) {
                        i++;
                    }
                }
            }
            return i;
        }
    }

    public final class SignLimit extends Limit {

        public SignLimit(int limit) {
            super(limit);
        }

        @Override
        public int getProtectionCount(Player player, Material material) {
            LWC lwc = LWC.getInstance();
            BlockCache blockCache = BlockCache.getInstance();
            int signCount = 0;
            for (Material sign : SIGNS) {
                signCount += lwc.getPhysicalDatabase().getProtectionCount(player.getName(), blockCache.getBlockId(sign));
            }
            return signCount;
        }

    }

    public LimitsV2() {
        enabled = LWC.getInstance().getConfiguration().getBoolean("optional.useProtectionLimits", true);

        if (enabled) {
            loadLimits();
        }
    }

    @Override
    public void onReload(LWCReloadEvent event) {
        if (enabled) {
            reload();
        }
    }

    @Override
    public void onRegisterProtection(LWCProtectionRegisterEvent event) {
        if (!enabled || event.isCancelled()) {
            return;
        }

        LWC lwc = event.getLWC();
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (hasReachedLimit(player, block.getType())) {
            lwc.sendLocale(player, "protection.exceeded");
            event.setCancelled(true);
        }
    }

    @Override
    public void onCommand(LWCCommandEvent event) {
        if (!enabled || event.isCancelled()) {
            return;
        }

        if (!event.hasFlag("limits")) {
            return;
        }

        LWC lwc = event.getLWC();
        CommandSender sender = event.getSender();
        String[] args = event.getArgs();
        event.setCancelled(true);

        String playerName;

        if (args.length == 0) {
            if (args.length == 0 && !(sender instanceof Player)) {
                sender.sendMessage(Colors.Dark_Red + "You are not a player!");
                return;
            }

            playerName = sender.getName();
        } else {
            if (lwc.isAdmin(sender)) {
                playerName = args[0];
            } else {
                lwc.sendLocale(sender, "protection.accessdenied");
                return;
            }
        }

        Player player = lwc.getPlugin().getServer().getPlayer(playerName);

        if (player == null) {
            sender.sendMessage(Colors.Dark_Red + "That player is not online!");
            return;
        }

        // send their limits to them
        sendLimits(sender, player, getPlayerLimits(player));
    }

    /**
     * Gets the raw limits configuration
     *
     * @return
     */
    public Configuration getConfiguration() {
        return configuration;
    }

    /**
     * Reload the limits
     */
    public void reload() {
        loadLimits();
    }

    /**
     * Sends the list of limits to the player
     *
     * @param sender the commandsender to send the limits to
     * @param target the player limits are being shown for, can be null
     * @param limits
     */
    public void sendLimits(CommandSender sender, Player target, List<Limit> limits) {
        for (Limit limit : limits) {
            if (limit == null) {
                continue;
            }

            String stringLimit = limit.getLimit() == UNLIMITED ? "Unlimited" : Integer.toString(limit.getLimit());
            String colour = Colors.Yellow;

            if (target != null) {
                Material material = null;

                if (limit instanceof BlockLimit) {
                    material = ((BlockLimit) limit).getMaterial();
                } else if (limit instanceof SignLimit) {
                    material = Material.OAK_SIGN;
                } else if (limit instanceof EntityLimit) {
                    material = Material.AIR;
                }

                boolean reachedLimit = hasReachedLimit(target, material);

                colour = reachedLimit ? Colors.Dark_Red : Colors.Dark_Green;
            }

            if (limit instanceof DefaultLimit) {
                String currentProtected = target != null
                        ? (Integer.toString(limit.getProtectionCount(target, null)) + "/")
                        : "";
                sender.sendMessage("Default: " + colour + currentProtected + stringLimit);
            } else if (limit instanceof BlockLimit) {
                BlockLimit blockLimit = (BlockLimit) limit;
                String currentProtected = target != null
                        ? (Integer.toString(limit.getProtectionCount(target, blockLimit.getMaterial())) + "/")
                        : "";
                sender.sendMessage(LWC.materialToString(blockLimit.getMaterial()) + ": " + colour + currentProtected
                        + stringLimit);
            } else if (limit instanceof SignLimit) {
                String currentProtected = target != null
                        ? (Integer.toString(limit.getProtectionCount(target, null)) + "/")
                        : "";
                sender.sendMessage("Sign: " + colour + currentProtected + stringLimit);
            } else if (limit instanceof EntityLimit) {
                String currentProtected = target != null
                        ? (Integer.toString(limit.getProtectionCount(target, Material.AIR)) + "/")
                        : "";
                sender.sendMessage(((EntityLimit) limit).getClass().getSimpleName() + ": " + colour + currentProtected
                        + stringLimit);
            } else {
                sender.sendMessage(limit.getClass().getSimpleName() + ": " + Colors.Yellow + stringLimit);
            }

        }
    }

    /**
     * Checks if a player has reached their protection limit (both for the block limit, and default global limit)
     *
     * @param player
     * @param material the material type the player has interacted with
     * @return
     */
    public boolean hasReachedLimit(Player player, Material material) {
        Limit limit = getEffectiveLimit(player, material);
        Limit defaultLimit = getEffectiveLimit(player, null);

        // if they don't have a limit it's not possible to reach it ^^
        // ... but if it's null, what the hell did the server owner do?
        if (limit == null || defaultLimit == null) {
            return false;
        }

        // Get the effective limit placed upon them
        int maxProtections = limit.getLimit();
        int maxProtectionsDefault = defaultLimit.getLimit();

        // get the amount of protections the player has
        int protections = limit.getProtectionCount(player, material);
        int protectionsDefault = defaultLimit.getProtectionCount(player, material);

        return protections >= maxProtections || protectionsDefault >= maxProtectionsDefault;
    }

    /**
     * Find limits that are attached to the player via permissions (e.g
     * lwc.protect.*.10 = 10 of any protection)
     *
     * @param player
     * @return
     */
    private List<Limit> findLimitsViaPermissions(Player player) {
        List<Limit> limits = new LinkedList<>();

        for (PermissionAttachmentInfo pai : player.getEffectivePermissions()) {
            String permission = pai.getPermission();
            boolean value = pai.getValue();

            if (!value || !permission.startsWith("lwc.protect.")) {
                continue;
            }

            String[] split = permission.substring("lwc.protect.".length()).split(".");

            if (split.length != 2) {
                continue;
            }

            String matchName = split[0];
            String strCount = split[1];

            int count;
            try {
                count = Integer.parseInt(strCount);
            } catch (NumberFormatException e) {
                continue;
            }

            if (matchName.equals("*")) {
                limits.add(new DefaultLimit(count));
            } else if (matchName.equals("sign")) {
                limits.add(new SignLimit(count));
            } else if (!matchName.equals("*") && !matchName.equals("sign")) {
                Material material = materialCache.get(matchName);

                if (material == null) {
                    continue;
                }

                limits.add(new BlockLimit(material, count));
            } else if (EntityType.valueOf(matchName.toUpperCase()) != null) {
                limits.add(new EntityLimit(EntityType.valueOf(matchName.toUpperCase()), count));
            }
        }

        return limits;
    }

    /**
     * Gets the list of limits that may apply to the player. For group limits, it
     * uses the highest one found.
     *
     * @param player
     * @return
     */
    public List<Limit> getPlayerLimits(Player player) {
        LWC lwc = LWC.getInstance();
        List<Limit> limits = new LinkedList<>();

        // get all of their own limits
        String playerName = player.getName().toLowerCase();
        if (playerLimits.containsKey(playerName)) {
            limits.addAll(playerLimits.get(playerName));
        }

        for (Limit limit : findLimitsViaPermissions(player)) {
            Limit matched = findLimit(limits, limit);

            if (matched != null) {
                // Is our limit better?
                if (limit.getLimit() > matched.getLimit()) {
                    limits.remove(matched);
                    limits.add(limit);
                }
            } else {
                limits.add(limit);
            }
        }

        // Look over the group limits
        for (String group : lwc.getPermissions().getGroups(player)) {
            if (groupLimits.containsKey(group.toLowerCase())) {
                for (Limit limit : groupLimits.get(group.toLowerCase())) {
                    // try to match one already inside what we found
                    Limit matched = findLimit(limits, limit);

                    if (matched != null) {
                        // Is our limit better?
                        if (limit.getLimit() > matched.getLimit()) {
                            limits.remove(matched);
                            limits.add(limit);
                        }
                    } else {
                        limits.add(limit);
                    }
                }
            }
        }

        // Look at the default limits
        for (Limit limit : defaultLimits) {
            // try to match one already inside what we found
            Limit matched = findLimit(limits, limit);

            if (matched == null) {
                limits.add(limit);
            }
        }

        return limits;
    }

    /**
     * Get the player's effective limit that should take precedence
     *
     * @param player
     * @param material
     * @return
     */
    public Limit getEffectiveLimit(Player player, Material material) {
        return getEffectiveLimit(getPlayerLimits(player), material);
    }

    /**
     * Gets an immutable list of the default limits
     *
     * @return
     */
    public List<Limit> getDefaultLimits() {
        return Collections.unmodifiableList(defaultLimits);
    }

    /**
     * Gets an unmodiable map of the player limits
     *
     * @return
     */
    public Map<String, List<Limit>> getPlayerLimits() {
        return Collections.unmodifiableMap(playerLimits);
    }

    /**
     * Gets an unmodiable map of the group limits
     *
     * @return
     */
    public Map<String, List<Limit>> getGroupLimits() {
        return Collections.unmodifiableMap(groupLimits);
    }

    /**
     * Orders the limits, putting the default limit at the top of the list
     *
     * @param limits
     * @return
     */
    private List<Limit> orderLimits(List<Limit> limits) {
        Limit defaultLimit = null;

        // Locate the default limit
        for (Limit limit : limits) {
            if (limit instanceof DefaultLimit) {
                defaultLimit = limit;
                break;
            }
        }

        // remove it
        limits.remove(defaultLimit);

        // readd it at the head
        limits.add(0, defaultLimit);

        return limits;
    }

    /**
     * Gets the material's effective limit that should take precedence
     *
     * @param limits
     * @param material
     * @return Limit object if one is found otherwise NULL
     */
    private Limit getEffectiveLimit(List<Limit> limits, Material material) {
        if (limits == null) {
            return null;
        }

        // Temporary storage to use if the default is found so we save time if no
        // override was found
        Limit defaultLimit = null;

        for (Limit limit : limits) {
            // Record the default limit if found
            if (limit instanceof DefaultLimit) {
                if (material == null) {
                    return limit;
                }
                defaultLimit = limit;
            } else if (limit instanceof SignLimit) {
                if (SIGNS.contains(material)) {
                    return limit;
                }
            } else if (limit instanceof BlockLimit) {
                BlockLimit blockLimit = (BlockLimit) limit;

                // If it's the appropriate material we can return immediately
                if (blockLimit.getMaterial() == material) {
                    return blockLimit;
                }
            } else if (limit instanceof EntityLimit) {
                if (material == Material.AIR) {
                    return limit;
                }
            }
        }

        return defaultLimit;
    }

    /**
     * Load all of the limits
     */
    private void loadLimits() {
        // make sure we're working on a clean slate
        defaultLimits.clear();
        playerLimits.clear();
        groupLimits.clear();

        // add the default limits
        defaultLimits.addAll(findLimits("defaults"));

        // add all of the player limits
        try {
            for (String player : configuration.getKeys("players")) {
                playerLimits.put(player.toLowerCase(), findLimits("players." + player));
            }
        } catch (NullPointerException e) {
        }

        // add all of the group limits
        try {
            for (String group : configuration.getKeys("groups")) {
                groupLimits.put(group.toLowerCase(), findLimits("groups." + group));
            }
        } catch (NullPointerException e) {
        }
    }

    /**
     * Find and match all of the limits in a given list of nodes for the config
     *
     * @param node
     * @return
     */
    private List<Limit> findLimits(String node) {
        List<Limit> limits = new LinkedList<>();
        List<String> keys = configuration.getKeys(node);

        for (String key : keys) {
            String value = configuration.getString(node + "." + key);

            int limit;

            if (value.equalsIgnoreCase("unlimited")) {
                limit = UNLIMITED;
            } else {
                limit = Integer.parseInt(value);
            }

            // Match default
            if (key.equalsIgnoreCase("default")) {
                limits.add(new DefaultLimit(limit));
            } else if (key.equalsIgnoreCase("sign")) {
                limits.add(new SignLimit(limit));
            } else if (!key.equalsIgnoreCase("default") && !key.equalsIgnoreCase("sign")) {
                // attempt to resolve it as a block id
                int blockId = -1;
                try {
                    blockId = Integer.parseInt(key);
                } catch (NumberFormatException e) {
                }

                // resolve the material
                Material material;

                BlockCache blockCache = BlockCache.getInstance();
                if (blockId >= 0) {
                    material = blockCache.getBlockType(blockId);
                } else {
                    material = Material.getMaterial(key.toUpperCase());
                }

                if (material != null) {
                    limits.add(new BlockLimit(material, limit));
                }
            } else if (EntityType.valueOf(key.toUpperCase()) != null) {
                limits.add(new EntityLimit(EntityType.valueOf(key.toUpperCase()), limit));
            }
        }

        // Order it
        orderLimits(limits);

        return limits;
    }

    /**
     * Find a limit in the list of limits that equals the given limit in class; The
     * LIMIT itself does not need to be equal; only the class & if it's a
     * BlockLimit, the material must equal
     *
     * @param limits
     * @param compare
     * @return
     */
    private Limit findLimit(List<Limit> limits, Limit compare) {
        for (Limit limit : limits) {
            if (limit != null && limit.getClass().isInstance(compare)) {
                if (limit instanceof BlockLimit) {
                    BlockLimit cmp1 = (BlockLimit) limit;
                    BlockLimit cmp2 = (BlockLimit) compare;

                    if (cmp1.getMaterial() == cmp2.getMaterial()) {
                        return limit;
                    }
                } else {
                    return limit;
                }
            }
        }

        return null;
    }

}
