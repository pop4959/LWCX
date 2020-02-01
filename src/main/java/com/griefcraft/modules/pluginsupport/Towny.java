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

package com.griefcraft.modules.pluginsupport;

import com.griefcraft.lwc.LWC;
import com.griefcraft.model.Permission;
import com.griefcraft.model.Protection;
import com.griefcraft.scripting.JavaModule;
import com.griefcraft.scripting.event.LWCAccessEvent;
import com.griefcraft.scripting.event.LWCProtectionRegisterEvent;
import com.griefcraft.util.config.Configuration;
import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.towny.event.PlotClearEvent;
import com.palmergames.bukkit.towny.event.TownUnclaimEvent;
import com.palmergames.bukkit.towny.object.Coord;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.palmergames.bukkit.towny.object.TownyWorld;
import com.palmergames.bukkit.towny.object.WorldCoord;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

public class Towny extends JavaModule implements Listener {

    /**
     * The Towny module configuration
     */
    private Configuration configuration = Configuration.load("towny.yml");

    /**
     * The Towny plugin
     */
    private com.palmergames.bukkit.towny.Towny towny;

    /**
     * townBorders: If Towny borders are to be used.
     */
    private boolean townyBorders = false;

    /**
     * Load the module
     */
    @Override
    public void load(LWC lwc) {
        // Check for Towny
        Plugin townyPlugin = lwc.getPlugin().getServer().getPluginManager().getPlugin("Towny");
        if (townyPlugin == null) {
            return;
        }

        // Check configuration
        this.townyBorders = configuration.getBoolean("towny.townBorders", false);

        // Get the Towny instance
        this.towny = (com.palmergames.bukkit.towny.Towny) townyPlugin;
    }

    @Override
    public void onAccessRequest(LWCAccessEvent event) {
        Player player = event.getPlayer();
        Protection protection = event.getProtection();

        if (event.getAccess() != Permission.Access.NONE) {
            return;
        }

        if (protection.getType() != Protection.Type.PRIVATE) {
            return;
        }

        if (towny == null) {
            return;
        }

        for (Permission permission : protection.getPermissions()) {
            if (permission.getType() != Permission.Type.TOWN) {
                continue;
            }

            try {
                TownBlock townBlock = WorldCoord.parseWorldCoord(event.getProtection().getBlock()).getTownBlock();
                Town town = townBlock.getTown();
                Block block = event.getProtection().getBlock();
                // Does the town exist?
                if (town == null) {
                    return;
                }

                // Check if the player is a resident of said town
                if (town.getMayor().getName().equalsIgnoreCase(player.getName())) {
                    // Town mayor
                    event.setAccess(Permission.Access.ADMIN);
                } else if (PlayerCacheUtil.getCachePermission(player, block.getLocation(), block.getType(), TownyPermission.ActionType.DESTROY)) {
                    // Has access to build (e.g. plot owner, embassy owner)
                    event.setAccess(Permission.Access.PLAYER);
                } else if (town.hasResident(player.getName())) {
                    // Resident
                    event.setAccess(Permission.Access.PLAYER);
                } else {
                    // Not a resident
                    event.setAccess(Permission.Access.NONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Cancel the event and inform the player that they cannot protect there
     *
     * @param event
     */
    private void cancel(LWCProtectionRegisterEvent event) {
        event.getLWC().sendLocale(event.getPlayer(), "lwc.towny.blocked");
        event.setCancelled(true);
    }

    /**
     * Just a note: catching NotRegisteredException (which where an Exception is
     * caught is where its thrown) will throw a ClassNotFoundException when
     * Towny is not installed.
     */
    @Override
    public void onRegisterProtection(LWCProtectionRegisterEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!townyBorders || towny == null) {
            return;
        }

        // The block being protected
        Block block = event.getBlock();

        // Get the towny world
        TownyWorld world;

        try {
            try {
                world = WorldCoord.parseWorldCoord(block).getTownyWorld();
            } catch (IncompatibleClassChangeError e) {
                // Towny Advanced
                try {
                    // We need to use Reflection because of the two TownyUniverse
                    // instances loaded (to retain Towny: CE support)
                    Method method = TownyUniverse.class.getDeclaredMethod("getWorld", String.class);
                    // Resolve the world (note: the method is static)
                    world = (TownyWorld) method.invoke(null, block.getWorld().getName());
                } catch (Exception ex) {
                    // No world, or something bad happened
                    cancel(event);
                    return;
                }
            }
        } catch (Exception e) {
            // No world, don't let them protect it!
            cancel(event);
            return;
        }

        if (!world.isUsingTowny()) {
            return;
        }

        try {
            TownBlock townBlock = world.getTownBlock(Coord.parseCoord(block));
            // If an exception is not thrown, we are in a town.

            // Check if they have access to the plot.
            if (!PlayerCacheUtil.getCachePermission(event.getPlayer(), block.getLocation(), block.getType(), TownyPermission.ActionType.DESTROY)) {
                cancel(event);
            }
        } catch (Exception e) {
            // If an exception is thrown, we are not in a town.
            cancel(event);
        }
    }

    @EventHandler
    public void onTownUnclaim(TownUnclaimEvent event) {
        if (!configuration.getBoolean("towny.cleanup.townUnclaim", false)) {
            return;
        }
        removeProtections(event.getWorldCoord());
    }

    @EventHandler
    public void onPlotClear(PlotClearEvent event) {
        if (!configuration.getBoolean("towny.cleanup.plotClear", false)) {
            return;
        }
        TownBlock townBlock = event.getTownBlock();
        if (townBlock == null) {
            return;
        }
        removeProtections(townBlock.getWorldCoord());
    }

    private void removeProtections(WorldCoord worldCoord) {
        if (worldCoord == null) {
            return;
        }
        LWC lwc = LWC.getInstance();
        World world = worldCoord.getBukkitWorld();
        int townBlockHeight = world.getMaxHeight() - 1;
        int townBlockSize = TownySettings.getTownBlockSize();
        for (int x = 0; x < townBlockSize; ++x) {
            for (int z = 0; z < townBlockSize; ++z) {
                for (int y = townBlockHeight; y > 0; --y) {
                    int blockX = worldCoord.getX() * townBlockSize + x;
                    int blockZ = worldCoord.getZ() * townBlockSize + z;
                    if (!lwc.isProtectable(world.getBlockAt(blockX, y, blockZ))) {
                        continue;
                    }
                    Protection protection = lwc.getPhysicalDatabase().loadProtection(world.getName(), blockX, y, blockZ);
                    if (protection != null) {
                        protection.remove();
                    }
                }
            }
        }
    }

}
