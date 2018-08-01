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

import com.griefcraft.cache.ProtectionCache;
import com.griefcraft.lwc.LWC;
import com.griefcraft.lwc.LWCPlugin;
import com.griefcraft.model.Protection;
import com.griefcraft.scripting.event.LWCProtectionDestroyEvent;
import com.griefcraft.scripting.event.LWCProtectionRegisterEvent;
import com.griefcraft.scripting.event.LWCProtectionRegistrationPostEvent;
import com.griefcraft.scripting.event.LWCRedstoneEvent;
import com.griefcraft.util.Colors;
import com.griefcraft.util.matchers.DoubleChestMatcher;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
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
	private final Set<String> blacklistedBlocks = new HashSet<String>();

	public LWCBlockListener(LWCPlugin plugin) {
		this.plugin = plugin;
		loadAndProcessConfig();
	}

	public static final BlockFace[] POSSIBLE_FACES = { BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST,
			BlockFace.UP, BlockFace.DOWN };

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

		for (BlockState newblock : blocks) {
			Block block = newblock.getBlock();
			if (!lwc.isProtectable(block)) {
				continue;
			}
			Protection protection = lwc.findProtection(block);
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
		String cacheKey = ProtectionCache.cacheKey(block.getLocation());

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
					protection.setBlockName(doubleChest.getType().name());
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

	@SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true)
	public void onBlockMultiPlace(BlockMultiPlaceEvent event) {
		LWC lwc = plugin.getLWC();
		Block block = event.getBlock();

		if (block.getType() == Material.LEGACY_BED_BLOCK) {
			for (BlockState state : event.getReplacedBlockStates()) {
				Protection protection = lwc.findProtection(state);

				if (protection != null) {
					event.setCancelled(true);
					return;
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockFromTo(BlockFromToEvent event) {
		Block block = event.getBlock();
		LWC lwc = this.plugin.getLWC();
		if (block.getType() == Material.WATER || block.getType() == Material.LEGACY_STATIONARY_WATER || block.getType() == Material.LEGACY_WATER) {
			if (lwc.findProtection(event.getToBlock().getLocation()) != null) {
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockPistonRetract(BlockPistonRetractEvent event) {
		if (!LWC.ENABLED) {
			return;
		}
		LWC lwc = this.plugin.getLWC();

		for (Block moved : event.getBlocks()) {
			if ((lwc.findProtection(moved) != null)) {
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockPistonExtend(BlockPistonExtendEvent event) {
		if (!LWC.ENABLED) {
			return;
		}
		LWC lwc = plugin.getLWC();
		for (Block moved : event.getBlocks()) {
			if ((lwc.findProtection(moved) != null)) {
				event.setCancelled(true);
				return;
			}
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
		String cacheKey = ProtectionCache.cacheKey(block.getLocation());

		// In the event they place a block, remove any known nulls there
		if (cache.isKnownNull(cacheKey)) {
			cache.remove(cacheKey);
		}

		// check if the block is blacklisted
		boolean blockIsBlacklisted = blacklistedBlocks.contains(block.getType().name())
				|| blacklistedBlocks.contains(block.getType().name() + block.getData());

		if (blockIsBlacklisted) {
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
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onBlockPlaceMonitor(BlockPlaceEvent event) {
		if (!LWC.ENABLED) {
			return;
		}

		LWC lwc = plugin.getLWC();
		Player player = event.getPlayer();
		Block block = event.getBlockPlaced();

		// Update the cache if a protection is matched here
		Protection current = lwc.getPhysicalDatabase().loadProtection(block.getWorld().getName(), block.getX(),
				block.getY(), block.getZ());
		if (current != null) {
			if (!current.isBlockInWorld()) {
				// Corrupted protection
				lwc.log("Removing corrupted protection: " + current);
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

		// If it's a chest, make sure they aren't placing it beside an already
		// registered chest
		if (DoubleChestMatcher.PROTECTABLES_CHESTS.contains(block.getType())) {
			for (BlockFace blockFace : DoubleChestMatcher.POSSIBLE_FACES) {
				Block face = block.getRelative(blockFace);
				// They're placing it beside a chest, check if it's already
				// protected
				if (face.getType() == block.getType()) {
					if (lwc.findProtection(face.getLocation()) != null) {
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
			Protection protection = lwc.getPhysicalDatabase().registerProtection(block.getType().name(), type,
					block.getWorld().getName(), player.getUniqueId().toString(), "", block.getX(), block.getY(),
					block.getZ());

			if (!Boolean.parseBoolean(lwc.resolveProtectionConfiguration(block, "quiet"))) {
				lwc.sendLocale(player, "protection.onplace.create.finalize", "type",
						lwc.getPlugin().getMessageParser().parseMessage(autoRegisterType.toLowerCase()), "block",
						LWC.materialToString(block));
			}

			if (protection != null) {
				lwc.getModuleLoader().dispatchEvent(new LWCProtectionRegistrationPostEvent(protection));
			}
		} catch (Exception e) {
			lwc.sendLocale(player, "protection.internalerror", "id", "PLAYER_INTERACT");
			e.printStackTrace();
		}
	}

	/**
	 * Load and process the configuration
	 */
	public void loadAndProcessConfig() {
		List<String> ids = LWC.getInstance().getConfiguration().getStringList("optional.blacklistedBlocks",
				new ArrayList<String>());

		for (String sId : ids) {
			String[] idParts = sId.trim().split(":");

			String id = idParts[0].trim();
			int data = 0;

			if (idParts.length > 1) {
				data = Integer.parseInt(idParts[1].trim());
			}

			if (data == 0) {
				blacklistedBlocks.add(id);
			} else {
				blacklistedBlocks.add(id + data);
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

}
