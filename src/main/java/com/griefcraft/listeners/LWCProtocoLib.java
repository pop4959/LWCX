package com.griefcraft.listeners;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.griefcraft.lwc.LWC;
import com.griefcraft.lwc.LWCPlugin;

public class LWCProtocoLib implements Listener {

	private static LWCPlugin plugin;
	
	public LWCProtocoLib(LWCPlugin plugin) {
		LWCProtocoLib.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerInteract(PlayerInteractEvent event) {
		LWC lwc = plugin.getLWC();
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		try {
			if (lwc.findProtection(block) != null) {
				ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(lwc.getPlugin(),
						ListenerPriority.MONITOR, PacketType.Play.Server.OPEN_WINDOW) {
					@Override
					public void onPacketSending(PacketEvent e) {
						if (lwc.isAdmin(player) || lwc.isMod(player)) {
							e.setReadOnly(false);
							e.setCancelled(false);
							return;
						} else if (lwc.canAccessProtection(player, block)) {
							e.setReadOnly(false);
							e.setCancelled(false);
							return;
						} else {
							e.setReadOnly(false);
							e.setCancelled(true);
							return;	
						}
					}
				});
			}
		} catch (Exception e) {
		}
	}
}
