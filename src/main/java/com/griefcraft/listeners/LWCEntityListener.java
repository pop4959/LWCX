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
import com.griefcraft.cache.ProtectionCache;
import com.griefcraft.lwc.LWC;
import com.griefcraft.lwc.LWCPlugin;
import com.griefcraft.model.Flag;
import com.griefcraft.model.Protection;
import com.griefcraft.scripting.event.LWCProtectionDestroyEvent;
import com.griefcraft.scripting.event.LWCProtectionRegisterEvent;
import com.griefcraft.scripting.event.LWCProtectionRegistrationPostEvent;
import com.griefcraft.util.Colors;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class LWCEntityListener implements Listener {

    /**
     * The plugin instance
     */
    private LWCPlugin plugin;

    private UUID placedArmorStandPlayer;

    public LWCEntityListener(LWCPlugin plugin) {
        this.plugin = plugin;
    }


    @EventHandler(ignoreCancelled = true)
    public void onHangingPlace(HangingPlaceEvent event) {
        Player player = event.getPlayer();
        Entity block = event.getEntity();
        entityCreatedByPlayer(block, player);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent e) {
        ItemStack inHand = e.getItem();
        if (inHand != null) {
            placedArmorStandPlayer = e.getPlayer().getUniqueId();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!LWC.ENABLED || event.isCancelled()) {
            return;
        }

        LWC lwc = plugin.getLWC();

        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            Entity entity = event.getEntity();
            EntityBlock entityBlock = new EntityBlock(entity);

            boolean ignoreBlockDestruction = Boolean
                    .parseBoolean(lwc.resolveProtectionConfiguration(entityBlock, "ignoreBlockDestruction"));

            if (ignoreBlockDestruction) {
                return;
            }

            if (event.getEntityType().equals(EntityType.ARMOR_STAND)) {
                if (event.getDamage() < 1.0 ||
                        ((Player) event.getDamager()).getGameMode().equals(GameMode.CREATIVE)) { // Armor Stand Broke
                    ProtectionCache cache = lwc.getProtectionCache();
                    String cacheKey = cache.cacheKey(entityBlock.getLocation());

                    // In the event they place a block, remove any known nulls there
                    if (cache.isKnownNull(cacheKey)) {
                        cache.remove(cacheKey);
                    }

                    Protection protection = lwc.findProtection(entityBlock);

                    if (protection == null) {
                        return;
                    }

                    boolean canAccess = lwc.canAccessProtection(player, protection);
                    boolean canAdmin = lwc.canAdminProtection(player, protection);

                    try {
                        // Removing protection
                        LWCProtectionDestroyEvent evt = new LWCProtectionDestroyEvent(player, protection,
                                LWCProtectionDestroyEvent.Method.ENTITY_DESTRUCTION, canAccess, canAdmin);
                        lwc.getModuleLoader().dispatchEvent(evt);

                        protection.remove();
                        protection.removeAllPermissions();
                        protection.removeCache();

                        if (evt.isCancelled() || !canAccess) {
                            event.setCancelled(true);
                        }
                    } catch (Exception e) {
                        event.setCancelled(true);
                        lwc.sendLocale(player, "protection.internalerror", "id", "BLOCK_BREAK");
                        e.printStackTrace();
                    }
                }
                /*else { // Armor Stand Punched
                    LWC.getInstance().log("Armor Stand Punched");
                    if(plugin.getLWC().isProtectable(entity.getType())){
                        int A = 50000 + entity.getUniqueId().hashCode();
                        Protection protection = lwc.getPhysicalDatabase().loadProtection(entity.getWorld().getName(), A, A, A);
                        boolean canAccess = lwc.canAccessProtection(player, protection);
                        boolean canAdmin = lwc.canAdminProtection(player, protection);
                        Set<String> actions = lwc.wrapPlayer(player).getActionNames();
                        Module.Result result = Module.Result.CANCEL;

                        // TODO: Finish this implementation
                        if (protection != null) {
                            LWCEntityDamageByEntityEvent evt =
                                    new LWCEntityDamageByEntityEvent(event, protection, actions, canAccess, canAdmin);
                            lwc.getModuleLoader().dispatchEvent(evt);

                            result = evt.getResult();
                        } else {

                        }
                        if (result == Module.Result.ALLOW) {
                            return;
                        }
                        if (player.hasPermission("lwc.lockentity." + entity.getType()) || player.hasPermission("lwc.lockentity.all")) {
                            if (onPlayerEntityInteract(p, entity, e.isCancelled())) {
                                chunkUnload(entity.getWorld().getName(), A);
                                e.setCancelled(true);
                            }
                        }
                        if (protection != null) {
                            if (canAccess)
                                return;
                            e.setCancelled(true);
                        }
                    }
                }*/
            }
        }


    }

    @EventHandler(ignoreCancelled = true)
    public void onCreateSpawn(CreatureSpawnEvent e) {
        if (placedArmorStandPlayer != null) {
            Player player = plugin.getServer().getPlayer(placedArmorStandPlayer);
            Entity block = e.getEntity();
            placedArmorStandPlayer = null;
            if (player != null) {
                if (player.getWorld().equals(block.getWorld())
                        && player.getLocation().distance(block.getLocation()) <= 5) {
                    entityCreatedByPlayer(block, player);
                }
            }
        }
    }

    private void entityCreatedByPlayer(Entity entity, Player player) {
        if (!LWC.ENABLED) {
            return;
        }

        LWC lwc = plugin.getLWC();

        int A = 50000 + entity.getUniqueId().hashCode();

        // Update the cache if a protection is matched here
        try {
            Protection current = lwc.findProtection(EntityBlock.getEntityBlock(entity));
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
        } catch (java.lang.ClassCastException ex) {
            ex.printStackTrace();
        }


        if (!lwc.isProtectable(entity.getType())) {
            return;
        }

        String autoRegisterType = lwc.resolveProtectionConfiguration(entity.getType(), "autoRegister");

        // is it auto protectable?
        if (!autoRegisterType.equalsIgnoreCase("private") && !autoRegisterType.equalsIgnoreCase("public")) {
            return;
        }

        if (!lwc.hasPermission(player, "lwc.create." + autoRegisterType, "lwc.create", "lwc.protect")) {
            return;
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

        try {
            LWCProtectionRegisterEvent evt = new LWCProtectionRegisterEvent(player, EntityBlock.getEntityBlock(entity));
            lwc.getModuleLoader().dispatchEvent(evt);

            // something cancelled registration
            if (evt.isCancelled()) {
                return;
            }

            // All good!
            Protection protection = lwc.getPhysicalDatabase().registerProtection(EntityBlock.ENTITY_BLOCK_ID, type,
                    entity.getWorld().getName(), player.getUniqueId().toString(), "", A, A, A);

            if (!Boolean.parseBoolean(lwc.resolveProtectionConfiguration(EntityBlock.getEntityBlock(entity), "quiet"))) {
                lwc.sendLocale(player, "protection.onplace.create.finalize", "type",
                        lwc.getPlugin().getMessageParser().parseMessage(autoRegisterType.toLowerCase()), "block",
                        LWC.materialToString(EntityBlock.getEntityBlock(entity)));
            }

            if (protection != null) {
                lwc.getModuleLoader().dispatchEvent(new LWCProtectionRegistrationPostEvent(protection));
            }
        } catch (Exception e) {
            lwc.sendLocale(player, "protection.internalerror", "id", "PLAYER_INTERACT");
            e.printStackTrace();
        }
    }

    @EventHandler
    public void entityInteract(EntityInteractEvent event) {
        Block block = event.getBlock();

        Protection protection = plugin.getLWC().findProtection(block.getLocation());

        if (protection != null) {
            boolean allowEntityInteract = Boolean
                    .parseBoolean(plugin.getLWC().resolveProtectionConfiguration(block, "allowEntityInteract"));

            if (!allowEntityInteract) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void entityBreakDoor(EntityBreakDoorEvent event) {
        Block block = event.getBlock();

        // See if there is a protection there
        Protection protection = plugin.getLWC().findProtection(block.getLocation());

        if (protection != null) {
            // protections.allowEntityBreakDoor
            boolean allowEntityBreakDoor = Boolean
                    .parseBoolean(plugin.getLWC().resolveProtectionConfiguration(block, "allowEntityBreakDoor"));

            if (!allowEntityBreakDoor) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityExplode(EntityExplodeEvent event) {
        if (!LWC.ENABLED || event.isCancelled()) {
            return;
        }

        LWC lwc = LWC.getInstance();

        for (Block block : event.blockList()) {
            Protection protection = plugin.getLWC().findProtection(block.getLocation());

            if (protection != null) {
                boolean ignoreExplosions = Boolean
                        .parseBoolean(lwc.resolveProtectionConfiguration(protection.getBlock(), "ignoreExplosions"));

                if (!(ignoreExplosions || protection.hasFlag(Flag.Type.ALLOWEXPLOSIONS))) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
