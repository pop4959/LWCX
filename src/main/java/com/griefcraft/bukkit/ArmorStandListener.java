package com.griefcraft.bukkit;

import com.griefcraft.listeners.LWCPlayerListener;
import com.griefcraft.lwc.LWC;
import com.griefcraft.model.Protection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class ArmorStandListener implements Listener {
	@EventHandler
	public void onEntityAtInteract(PlayerInteractAtEntityEvent e) {
		Entity entity = e.getRightClicked();
		int A = 50000 + entity.getUniqueId().hashCode();

		LWC lwc = LWC.getInstance();
		Protection protection = lwc.getPhysicalDatabase().loadProtection(
				entity.getWorld().getName(), A, A, A);
		Player p = e.getPlayer();
		boolean canAccess = lwc.canAccessProtection(p, protection);
		if ((entity instanceof Player)) {
			return;
		}
		if ((entity instanceof ArmorStand)) {
			if (p.hasPermission("lwc.lockentity." + entity.getType())) {
				if (LWCPlayerListener.onPlayerEntityInteract(p, entity,
						e.isCancelled())) {
					e.setCancelled(true);
				}
			} else if ((p.hasPermission("lwc.lockentity.all")) || (p.isOp())) {
				if (LWCPlayerListener.onPlayerEntityInteract(p, entity,
						e.isCancelled())) {
					e.setCancelled(true);
				}
			} else {
				return;
			}
			if (protection != null) {
				if (canAccess) {
					return;
				}
				e.setCancelled(true);
			}
		}
	}
}