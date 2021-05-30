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
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.towny.event.PlotClearEvent;
import com.palmergames.bukkit.towny.event.town.TownRuinedEvent;
import com.palmergames.bukkit.towny.event.town.TownUnclaimEvent;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.object.WorldCoord;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;

import java.util.ArrayList;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

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

            Block block = event.getProtection().getBlock();
            if (TownyAPI.getInstance().isWilderness(block))
            	return; // Return if we're in the wilderness.

            Town town = TownyAPI.getInstance().getTown(block.getLocation());
            TownBlock townBlock = TownyAPI.getInstance().getTownBlock(block.getLocation());
            if (town == null || townBlock == null) {
            	return;
            }

            String ownerName = null;
            if (townBlock.hasResident()) {
            	try { // Get the name of the resident who personally owns the plot, if someone does own it personally.
					ownerName = townBlock.getResident().getName();
				} catch (NotRegisteredException ignored) {}
            }

            // Check if the player is a resident of said town, or should otherwise have permissions based on Towny's ruleset.
            if (town.getMayor().getName().equalsIgnoreCase(player.getName())) {
                // Town mayor
                event.setAccess(Permission.Access.ADMIN);
            } else if (townBlock.hasResident() && ownerName != null && ownerName.equals(player.getName())) {
            	// Owns the plot personally (plot owner, embassy plot owner.)
            	event.setAccess(Permission.Access.ADMIN);
            } else if (PlayerCacheUtil.getCachePermission(player, block.getLocation(), block.getType(), TownyPermission.ActionType.DESTROY)) {
                // Has access to destroy in this plot (and could break the chest if they wanted to for example.)
                event.setAccess(Permission.Access.PLAYER);
            } else if (town.hasResident(player.getName())) {
                // Resident of the Town.
                event.setAccess(Permission.Access.PLAYER);
            } else {
                // Doesn't meet any of the requirements.
                event.setAccess(Permission.Access.NONE);
            }
        }
    }

    /*
     * Cancel the event and inform the player that they cannot protect there
     */
    private void cancel(LWCProtectionRegisterEvent event) {
        event.getLWC().sendLocale(event.getPlayer(), "lwc.towny.blocked");
        event.setCancelled(true);
    }

    /*
     * Prevents protection registration if either the block is not accessible to the player in the town, or if the
     * block is in the wilderness and towny borders is enabled.
     */
    @Override
    public void onRegisterProtection(LWCProtectionRegisterEvent event) {
        if (event.isCancelled() || towny == null || !TownyAPI.getInstance().isTownyWorld(event.getBlock().getWorld())) {
            return;
        }

        // The block being protected
        Block block = event.getBlock();

        Town town = TownyAPI.getInstance().getTown(block.getLocation());

        if (town != null && !PlayerCacheUtil.getCachePermission(event.getPlayer(), block.getLocation(), block.getType(), TownyPermission.ActionType.DESTROY)) {
            cancel(event);
            return;
        }

        if (!townyBorders) {
            return;
        }

        if (TownyAPI.getInstance().isWilderness(block)) {
        	cancel(event);
        }
    }

    /*
     * When a townblock is unclaimed by a town,
     * this will remove any protections present in the townblock,
     * if this feature is enabled in the config.
     */
    @EventHandler
    public void onTownUnclaim(TownUnclaimEvent event) {
        if (!configuration.getBoolean("towny.cleanup.townUnclaim", false)) {
            return;
        }
        removeProtections(event.getWorldCoord());
    }

    /*
     * When a plot is "cleared" of special blocks,
     * this will remove any protections present in the townblock,
     * if this feature is enabled in the config.
     */
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

    /*
     * When a town falls to Ruin status,
     * this will remove any protections present in the town,
     * if this feature is enabled in the config.
     */
    @EventHandler
    public void onTownRuin(TownRuinedEvent event) {
    	if (!configuration.getBoolean("towny.cleanup.townRuin", false)) {
    		return;
    	}

    	for (TownBlock townBlock : new ArrayList<TownBlock>(event.getTown().getTownBlocks())) {
    		removeProtections(townBlock.getWorldCoord());
    	}
    }

    /**
     * Parses over the blocks in a Towny WorldCoord (Essentially a TownBlock,)
     * and removes any LWC protections present.
     *
     * @param worldCoord WorldCoord from which to remove any protections.
     */
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
