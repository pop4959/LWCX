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

import com.griefcraft.bukkit.EntityBlock;
import com.griefcraft.cache.BlockCache;
import com.griefcraft.lwc.LWC;
import com.griefcraft.lwc.LWCPlugin;
import com.griefcraft.model.Flag;
import com.griefcraft.model.LWCPlayer;
import com.griefcraft.model.Protection;
import com.griefcraft.scripting.Module;
import com.griefcraft.scripting.event.*;
import com.griefcraft.util.UUIDRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class LWCPlayerListener implements Listener {

    /**
     * The plugin instance
     */
    private static LWCPlugin plugin;

    public LWCPlayerListener(LWCPlugin plugin) {
        LWCPlayerListener.plugin = plugin;
    }

    @EventHandler
    public void hangingBreakByEvent(HangingBreakByEntityEvent event) {
        Entity entity = event.getEntity();
        if (plugin.getLWC().isProtectable(event.getEntity().getType())) {
            int A = 50000 + entity.getUniqueId().hashCode();
            LWC lwc = LWC.getInstance();
            Protection protection = lwc.getPhysicalDatabase().loadProtection(entity.getWorld().getName(), A, A, A);
            if (event.getRemover() instanceof Projectile && protection != null) {
                event.setCancelled(true);
            }
            if (event.getRemover() instanceof Player) {
                Player p = (Player) event.getRemover();
                if (p.hasPermission("lwc.lockentity." + entity.getType()) || p.hasPermission("lwc.lockentity.all")) {
                    if (onPlayerEntityInteract(p, entity, event.isCancelled())) {
                        event.setCancelled(true);
                    }
                }
                if (!event.isCancelled() && protection != null) {
                    boolean canAccess = lwc.canAccessProtection(p, protection);
                    if (canAccess) {
                        protection.remove();
                        protection.removeAllPermissions();
                        protection.removeCache();
                        return;
                    }
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void minecartBreak(VehicleDestroyEvent e) {
        Entity entity = e.getVehicle();
        if (plugin.getLWC().isProtectable(e.getVehicle().getType())) {
            int A = 50000 + entity.getUniqueId().hashCode();
            LWC lwc = LWC.getInstance();
            Protection protection = lwc.getPhysicalDatabase().loadProtection(entity.getWorld().getName(), A, A, A);
            if ((((entity instanceof StorageMinecart)) || ((entity instanceof HopperMinecart)))
                    && (protection != null)) {
                if (e.getAttacker() instanceof Projectile) {
                    e.setCancelled(true);
                }
                Player p = (Player) e.getAttacker();
                boolean canAccess = lwc.canAccessProtection(p, protection);
                if (canAccess) {
                    protection.remove();
                    protection.removeAllPermissions();
                    protection.removeCache();
                    return;
                }
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void hangingBreak(HangingBreakEvent event) {
        Entity entity = event.getEntity();
        if (plugin.getLWC().isProtectable(event.getEntity().getType())) {
            int A = 50000 + entity.getUniqueId().hashCode();

            LWC lwc = LWC.getInstance();
            Protection protection = lwc.getPhysicalDatabase().loadProtection(entity.getWorld().getName(), A, A, A);
            if (protection != null) {
                if (event.getCause() == RemoveCause.PHYSICS || event.getCause() == RemoveCause.EXPLOSION
                        || event.getCause() == RemoveCause.OBSTRUCTION) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerArmorStandManipulate(PlayerArmorStandManipulateEvent e) {
        Entity entity = e.getRightClicked();
        if (plugin.getLWC().isProtectable(e.getRightClicked().getType())) {
            int A = 50000 + entity.getUniqueId().hashCode();

            LWC lwc = LWC.getInstance();
            Protection protection = lwc.getPhysicalDatabase().loadProtection(entity.getWorld().getName(), A, A, A);
            Player p = e.getPlayer();
            boolean canAccess = lwc.canAccessProtection(p, protection);
            if (onPlayerEntityInteract(p, entity, e.isCancelled())) {
                e.setCancelled(true);
            }
            if (protection != null) {
                if (canAccess)
                    return;
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (e instanceof EntityDamageByEntityEvent
                && !(e.getCause() == DamageCause.BLOCK_EXPLOSION || e.getCause() == DamageCause.ENTITY_EXPLOSION))
            return;
        Entity entity = e.getEntity();
        if (plugin.getLWC().isProtectable(e.getEntity().getType())) {
            int A = 50000 + entity.getUniqueId().hashCode();
            LWC lwc = LWC.getInstance();
            Protection protection = lwc.getPhysicalDatabase().loadProtection(entity.getWorld().getName(), A, A, A);
            if (protection != null) {
                if (e.getCause() != DamageCause.CONTACT)
                    e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void itemFrameItemRemoval(EntityDamageByEntityEvent e) {
        if (e.isCancelled())
            return;
        Entity entity = e.getEntity();
        if (plugin.getLWC().isProtectable(e.getEntity().getType())) {
            int A = 50000 + entity.getUniqueId().hashCode();
            LWC lwc = LWC.getInstance();
            Protection protection = lwc.getPhysicalDatabase().loadProtection(entity.getWorld().getName(), A, A, A);
            if (!(entity instanceof Player)) {
                if (e.getDamager() instanceof Projectile) {
                    if (protection != null) {
                        e.setCancelled(true);
                    }
                    if ((((entity instanceof StorageMinecart)) || ((entity instanceof HopperMinecart)))
                            && (protection != null)) {
                        e.setCancelled(true);
                    }
                }
                if (e.getDamager() instanceof Player) {
                    Player p = (Player) e.getDamager();
                    if (protection != null && !lwc.canAccessProtection(p, protection)) {
                        e.setCancelled(true);
                    }
                    if (p.hasPermission("lwc.lockentity." + e.getEntityType())
                            || p.hasPermission("lwc.lockentity.all")) {
                        if (onPlayerEntityInteract(p, entity, e.isCancelled())) {
                            e.setCancelled(true);
                        }
                    }
                    if ((((entity instanceof StorageMinecart)) || ((entity instanceof HopperMinecart)))
                            && (protection != null)) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        Entity entity = e.getEntity();
        if (plugin.getLWC().isProtectable(e.getEntity().getType())) {
            int A = 50000 + entity.getUniqueId().hashCode();
            Player player = e.getEntity().getKiller();
            LWC lwc = LWC.getInstance();
            Protection protection = lwc.getPhysicalDatabase().loadProtection(entity.getWorld().getName(), A, A, A);
            if (protection != null) {
                boolean canAccess = lwc.canAccessProtection(player, protection);
                boolean canAdmin = lwc.canAdminProtection(player, protection);
                try {
                    if (player != null) {
                        LWCProtectionDestroyEvent evt = new LWCProtectionDestroyEvent(player, protection,
                                LWCProtectionDestroyEvent.Method.ENTITY_DESTRUCTION, canAccess, canAdmin);
                        lwc.getModuleLoader().dispatchEvent(evt);
                    } else {
                        protection.remove();
                        protection.removeAllPermissions();
                        protection.removeCache();
                    }
                } catch (Exception ex) {
                    if (player != null) {
                        lwc.sendLocale(player, "protection.internalerror", "id", "ENTITY_DEATH");
                    }
                    lwc.sendLocale(Bukkit.getServer().getConsoleSender(), "protection.internalerror", "id",
                            "ENTITY_DEATH");
                    ex.printStackTrace();
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityInteract(PlayerInteractEntityEvent e) {
        Entity entity = e.getRightClicked();
        if (plugin.getLWC().isProtectable(entity.getType())) {
            int A = 50000 + entity.getUniqueId().hashCode();
            LWC lwc = LWC.getInstance();
            Protection protection = lwc.getPhysicalDatabase().loadProtection(entity.getWorld().getName(), A, A, A);
            Player p = e.getPlayer();
            boolean canAccess = lwc.canAccessProtection(p, protection);
            if (entity instanceof Player) {
                return;
            }
            boolean canAdmin = lwc.canAdminProtection(p, protection);
            Set<String> actions = lwc.wrapPlayer(p).getActionNames();
            Module.Result result;
            if (protection != null) {
                LWCProtectionInteractEntityEvent evt = new LWCProtectionInteractEntityEvent(e, protection, actions,
                        canAccess, canAdmin);
                lwc.getModuleLoader().dispatchEvent(evt);

                result = evt.getResult();
            } else {
                LWCEntityInteractEvent evt = new LWCEntityInteractEvent(e, entity, actions);
                lwc.getModuleLoader().dispatchEvent(evt);

                result = evt.getResult();
            }
            if (result == Module.Result.ALLOW) {
                return;
            }
            if (p.hasPermission("lwc.lockentity." + entity.getType()) || p.hasPermission("lwc.lockentity.all")) {
                if (onPlayerEntityInteract(p, entity, e.isCancelled())) {
                    e.setCancelled(true);
                }
            }
            if (protection != null) {
                if (canAccess)
                    return;
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void storageMinecraftInventoryOpen(InventoryOpenEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        Player player = (Player) event.getPlayer();
        if ((!(holder instanceof StorageMinecart)) && (!(holder instanceof HopperMinecart))) {
            return;
        }
        Entity entity = (Entity) holder;
        if (plugin.getLWC().isProtectable(entity.getType())) {
            if (!plugin.getLWC().hasPermission(player, "lwc.protect")
                    && plugin.getLWC().hasPermission(player, "lwc.deny") && !plugin.getLWC().isAdmin(player)
                    && !plugin.getLWC().isMod(player)) {
                plugin.getLWC().sendLocale(player, "protection.interact.error.blocked");
                event.setCancelled(true);
                return;
            }
            if (onPlayerEntityInteract((Player) event.getPlayer(), entity, event.isCancelled())) {
                event.setCancelled(true);
            }
        }
    }

    private boolean onPlayerEntityInteract(Player player, Entity entity, boolean cancelled) {
        int A = EntityBlock.POSITION_OFFSET + entity.getUniqueId().hashCode();

        // attempt to load the protection for this cart
        LWC lwc = LWC.getInstance();
        Protection protection = lwc.getPhysicalDatabase().loadProtection(entity.getWorld().getName(), A, A, A);
        LWCPlayer lwcPlayer = lwc.wrapPlayer(player);

        try {
            Set<String> actions = lwcPlayer.getActionNames();
            Module.Result result;
            boolean canAccess = lwc.canAccessProtection(player, protection);

            // Calculate if the player has a pending action (i.e any action
            // besides 'interacted')
            int actionCount = actions.size();
            boolean hasInteracted = actions.contains("interacted");
            boolean hasPendingAction = (hasInteracted && actionCount > 1) || (!hasInteracted && actionCount > 0);

            // If the event was cancelled and they have an action, warn them
            if (cancelled) {
                // only send it if a non-"interacted" action is set which is
                // always set on the player
                if (hasPendingAction) {
                    lwc.sendLocale(player, "lwc.pendingaction");
                }

                // it's cancelled, do not continue !
                return false;
            }

            // register in an action what protection they interacted with (if
            // applicable.)
            if (protection != null) {
                com.griefcraft.model.Action action = new com.griefcraft.model.Action();
                action.setName("interacted");
                action.setPlayer(lwcPlayer);
                action.setProtection(protection);

                lwcPlayer.addAction(action);
            }

            // events are only used when they already have an action pending
            boolean canAdmin = lwc.canAdminProtection(player, protection);
            Block fakeBlock = EntityBlock.getEntityBlock(entity);
            PlayerInteractEvent fakeEvent = new PlayerInteractEvent(player, Action.RIGHT_CLICK_BLOCK, null, fakeBlock,
                    null);

            if (protection != null) {
                LWCProtectionInteractEvent evt = new LWCProtectionInteractEvent(fakeEvent, protection, actions,
                        canAccess, canAdmin);
                lwc.getModuleLoader().dispatchEvent(evt);

                result = evt.getResult();
            } else {
                LWCBlockInteractEvent evt = new LWCBlockInteractEvent(fakeEvent, fakeBlock, actions);
                lwc.getModuleLoader().dispatchEvent(evt);

                result = evt.getResult();
            }

            if (result == Module.Result.ALLOW) {
                return false;
            }

            // optional.onlyProtectIfOwnerIsOnline
            if (protection != null && !canAccess
                    && lwc.getConfiguration().getBoolean("optional.onlyProtectWhenOwnerIsOnline", false)) {
                Player owner = protection.getBukkitOwner();

                // If they aren't online, allow them in :P
                if (owner == null || !owner.isOnline()) {
                    return false;
                }
            }

            // optional.onlyProtectIfOwnerIsOffline
            if (protection != null && !canAccess
                    && lwc.getConfiguration().getBoolean("optional.onlyProtectWhenOwnerIsOffline", false)) {
                Player owner = protection.getBukkitOwner();

                // If they aren't online, allow them in :P
                if (owner != null && owner.isOnline()) {
                    return false;
                }
            }

            if (result == Module.Result.DEFAULT) {
                canAccess = lwc.enforceAccess(player, protection, entity, canAccess);
            }

            if (!canAccess || result == Module.Result.CANCEL) {
                return true;
            }
        } catch (Exception e) {
            lwc.sendLocale(player, "protection.internalerror", "id", "PLAYER_INTERACT");
            e.printStackTrace();
            return true;
        }
        return false;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUIDRegistry.updateCache(player.getUniqueId(), player.getName());
    }

    @EventHandler(ignoreCancelled = true)
    public void onMoveItem(InventoryMoveItemEvent event) {
        if (plugin.getLWC().useAlternativeHopperProtection()
                && !(event.getSource().getHolder() instanceof HopperMinecart || event.getDestination().getHolder() instanceof HopperMinecart)) {
            return;
        }

        boolean result;

        // if the initiator is the same as the source it is a dropper i.e.
        // depositing items
        if (event.getInitiator() == event.getSource()) {
            result = handleMoveItemEvent(event, event.getInitiator(), event.getDestination());
        } else {
            result = handleMoveItemEvent(event, event.getInitiator(), event.getSource());
        }

        if (result) {
            event.setCancelled(true);
        }
    }

    /**
     * Handle the item move event
     *
     * @param inventory
     */
    private boolean handleMoveItemEvent(InventoryMoveItemEvent event, Inventory initiator, Inventory inventory) {
        LWC lwc = LWC.getInstance();

        if (inventory == null) {
            return false;
        }

        Location location;
        InventoryHolder holder;
        Location hopperLocation = null;
        InventoryHolder hopperHolder;

        try {
            holder = inventory.getHolder();
            hopperHolder = initiator.getHolder();
        } catch (AbstractMethodError e) {
            return false;
        }

        try {
            if (holder instanceof BlockState) {
                location = ((BlockState) holder).getLocation();
            } else if (holder instanceof DoubleChest) {
                location = ((DoubleChest) holder).getLocation();
            } else {
                return false;
            }

            if (hopperHolder instanceof Hopper) {
                hopperLocation = ((Hopper) hopperHolder).getLocation();
            } else if (hopperHolder instanceof HopperMinecart) {
                hopperLocation = ((HopperMinecart) hopperHolder).getLocation();
            }
        } catch (Exception e) {
            return false;
        }

        // High-intensity zone: increase protection cache if it's full,
        // otherwise
        // the database will be getting rammed
        lwc.getProtectionCache().increaseIfNecessary();

        // Attempt to load the protection at that location
        Protection protection = lwc.findProtection(location);

        // If no protection was found we can safely ignore it
        if (protection == null) {
            return false;
        }

        if (hopperLocation != null
                && Boolean.parseBoolean(lwc.resolveProtectionConfiguration(Material.HOPPER, "enabled"))) {
            Protection hopperProtection = lwc.findProtection(hopperLocation);

            if (hopperProtection != null) {
                // if they're owned by the same person then we can allow the
                // move
                if (protection.getOwner().equals(hopperProtection.getOwner())) {
                    return false;
                }
            }
        }

        BlockCache blockCache = BlockCache.getInstance();
        boolean denyHoppers = Boolean.parseBoolean(
                lwc.resolveProtectionConfiguration(blockCache.getBlockType(protection.getBlockId()), "denyHoppers"));
        boolean protectHopper = protection.hasFlag(Flag.Type.HOPPER);
        boolean protectHopperIn = inventory == event.getDestination() && protection.hasFlag(Flag.Type.HOPPERIN);
        boolean protectHopperOut = inventory == event.getSource() && protection.hasFlag(Flag.Type.HOPPEROUT);

        // xor = (a && !b) || (!a && b)
        if (denyHoppers ^ (protectHopper || protectHopperIn || protectHopperOut)) {
            return true;
        }

        return false;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (event.isCancelled() || !LWC.ENABLED) {
            return;
        }

        Player player = event.getPlayer();

        LWCDropItemEvent evt = new LWCDropItemEvent(player, event);
        plugin.getLWC().getModuleLoader().dispatchEvent(evt);

        if (evt.isCancelled()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        LWC lwc = plugin.getLWC();
        Player player = event.getPlayer();
        LWCPlayer lwcPlayer = lwc.wrapPlayer(player);

        if (event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        // This event can sometimes be thrown twice (for the main hand and offhand), and while we need to check both
        // for protection access, we want to avoid doing duplicate work (throwing events, printing messages, etc).
        boolean usingMainHand = event.getHand() == EquipmentSlot.HAND;

        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }

        BlockState state = block.getState();
        // Prevent players with lwc.deny from interacting with blocks that have an inventory
        if (state instanceof InventoryHolder && lwc.isProtectable(block)) {
            if (!lwc.hasPermission(player, "lwc.protect") && lwc.hasPermission(player, "lwc.deny")
                    && !lwc.isAdmin(player) && !lwc.isMod(player)) {
                if (usingMainHand) {
                    lwc.sendLocale(player, "protection.interact.error.blocked");
                }
                event.setCancelled(true);
                return;
            }
        }

        try {
            Set<String> actions = lwcPlayer.getActionNames();
            Module.Result result = Module.Result.DEFAULT;
            Protection protection = lwc.findProtection(block.getLocation());
            boolean canAccess = lwc.canAccessProtection(player, protection);

            if (usingMainHand) {
                if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                    boolean ignoreLeftClick = Boolean
                            .parseBoolean(lwc.resolveProtectionConfiguration(block, "ignoreLeftClick"));
                    if (ignoreLeftClick) {
                        return;
                    }
                } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    boolean ignoreRightClick = Boolean
                            .parseBoolean(lwc.resolveProtectionConfiguration(block, "ignoreRightClick"));
                    if (ignoreRightClick) {
                        return;
                    }
                }

                // Calculate if the player has a pending action (i.e any action besides 'interacted')
                int actionCount = actions.size();
                boolean hasInteracted = actions.contains("interacted");
                boolean hasPendingAction = (hasInteracted && actionCount > 1) || (!hasInteracted && actionCount > 0);

                // If the event was cancelled and they have an action, warn them
                if (event.isCancelled()) {
                    // only send it if a non-"interacted" action is set which is always set on the player
                    if (hasPendingAction) {
                        lwc.sendLocale(player, "lwc.pendingaction");
                    }
                    // it's cancelled, do not continue !
                    return;
                }

                // register in an action what protection they interacted with (if applicable.)
                if (protection != null) {
                    com.griefcraft.model.Action action = new com.griefcraft.model.Action();
                    action.setName("interacted");
                    action.setPlayer(lwcPlayer);
                    action.setProtection(protection);
                    lwcPlayer.addAction(action);
                }

                // events are only used when they already have an action pending
                boolean canAdmin = lwc.canAdminProtection(player, protection);

                if (protection != null) {
                    LWCProtectionInteractEvent evt = new LWCProtectionInteractEvent(event, protection, actions,
                            canAccess, canAdmin);
                    lwc.getModuleLoader().dispatchEvent(evt);

                    result = evt.getResult();
                } else {
                    LWCBlockInteractEvent evt = new LWCBlockInteractEvent(event, block, actions);
                    lwc.getModuleLoader().dispatchEvent(evt);

                    result = evt.getResult();
                }

                if (result == Module.Result.ALLOW) {
                    return;
                }

                // optional.onlyProtectIfOwnerIsOnline
                if (protection != null && !canAccess
                        && lwc.getConfiguration().getBoolean("optional.onlyProtectWhenOwnerIsOnline", false)) {
                    Player owner = protection.getBukkitOwner();
                    // If they aren't online, allow them in :P
                    if (owner == null || !owner.isOnline()) {
                        return;
                    }
                }

                // optional.onlyProtectIfOwnerIsOffline
                if (protection != null && !canAccess
                        && lwc.getConfiguration().getBoolean("optional.onlyProtectWhenOwnerIsOffline", false)) {
                    Player owner = protection.getBukkitOwner();
                    // If they aren't online, allow them in :P
                    if (owner != null && owner.isOnline()) {
                        return;
                    }
                }
            }

            if (result == Module.Result.DEFAULT) {
                canAccess = lwc.enforceAccess(player, protection, block, canAccess, usingMainHand);
            }

            if (!canAccess || result == Module.Result.CANCEL) {
                event.setCancelled(true);
                event.setUseInteractedBlock(org.bukkit.event.Event.Result.DENY);
            }
        } catch (Exception e) {
            event.setCancelled(true);
            event.setUseInteractedBlock(org.bukkit.event.Event.Result.DENY);
            lwc.sendLocale(player, "protection.internalerror", "id", "PLAYER_INTERACT");
            e.printStackTrace();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (!LWC.ENABLED) {
            return;
        }

        // remove the place from the player cache and reset anything they can
        // access
        LWCPlayer.removePlayer(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        LWC lwc = LWC.getInstance();

        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        // Player interacting with the inventory
        Player player = (Player) event.getWhoClicked();

        // The inventory they are using
        Inventory inventory = event.getInventory();

        if (inventory == null || event.getSlot() < 0) {
            return;
        }

        // Location of the container
        Location location;
        InventoryHolder holder = null;

        try {
            holder = event.getInventory().getHolder();
        } catch (AbstractMethodError e) {
            e.printStackTrace();
            return;
        }

        try {
            if (holder instanceof BlockState) {
                location = ((BlockState) holder).getLocation();
            } else if (holder instanceof DoubleChest) {
                location = ((DoubleChest) holder).getLocation();
            } else {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // Attempt to load the protection at that location
        Protection protection = lwc.findProtection(location);

        // If no protection was found we can safely ignore it
        if (protection == null) {
            return;
        }

        // If it's not a donation or display chest, ignore it
        if (protection.getType() != Protection.Type.DONATION && protection.getType() != Protection.Type.DISPLAY) {
            return;
        }

        if (protection.getType() == Protection.Type.DONATION && event.getAction() != InventoryAction.COLLECT_TO_CURSOR) {
            // If it's not a container, we don't want it
            if (event.getSlotType() != InventoryType.SlotType.CONTAINER) {
                return;
            }

            // Nifty trick: these will different IFF they are interacting with
            // the player's inventory or hotbar instead of the block's inventory
            if (event.getSlot() != event.getRawSlot()) {
                return;
            }

            // The item they are taking/swapping with
            ItemStack item;

            try {
                item = event.getCurrentItem();
            } catch (ArrayIndexOutOfBoundsException e) {
                return;
            }

            if (item == null || item.getType() == null || item.getType() == Material.AIR) {
                return;
            }

            // if it's not a right click or a shift click it should be a left
            // click (no shift)
            // this is for when players are INSERTing items (i.e. item in hand
            // and left clicking)
            if (player.getInventory().getItemInMainHand() == null && (!event.isRightClick() && !event.isShiftClick())) {
                return;
            }
        }

        // Can they admin it? (remove items/etc)
        boolean canAdmin = lwc.canAdminProtection(player, protection);

        // nope.avi
        if (!canAdmin) {
            event.setCancelled(true);
        }
    }

    // Mostly a copy of the inventory click event, but intended to disable dragging in display chests
    @EventHandler(ignoreCancelled = true)
    public void onInventoryDrag(InventoryDragEvent event) {
        LWC lwc = LWC.getInstance();

        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        // Player interacting with the inventory
        Player player = (Player) event.getWhoClicked();

        // Location of the container
        Location location;
        InventoryHolder holder = null;

        try {
            holder = event.getInventory().getHolder();
        } catch (AbstractMethodError e) {
            e.printStackTrace();
            return;
        }

        try {
            if (holder instanceof BlockState) {
                location = ((BlockState) holder).getLocation();
            } else if (holder instanceof DoubleChest) {
                location = ((DoubleChest) holder).getLocation();
            } else {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // Attempt to load the protection at that location
        Protection protection = lwc.findProtection(location);

        // If no protection was found we can safely ignore it
        if (protection == null) {
            return;
        }

        // If it's not a display chest, ignore it
        if (protection.getType() != Protection.Type.DISPLAY) {
            return;
        }

        // Can they admin it? (remove items/etc)
        boolean canAdmin = lwc.canAdminProtection(player, protection);

        // nope.avi
        if (!canAdmin) {
            event.setCancelled(true);
        }
    }

}
