package com.griefcraft.listeners;

import com.griefcraft.lwc.LWC;
import com.griefcraft.model.Permission;
import com.griefcraft.model.Protection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTakeLecternBookEvent;

public class LWC114Listener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPlayerTakeLecternBook(PlayerTakeLecternBookEvent event) {
        LWC lwc = LWC.getInstance();
        Protection protection = lwc.findProtection(event.getLectern());
        if (protection == null || protection.isOwner(event.getPlayer()) || protection.getType() == Protection.Type.PUBLIC) {
            return;
        }
        if (protection.getAccess(event.getPlayer().getUniqueId().toString(), Permission.Type.PLAYER) == Permission.Access.NONE) {
            event.setCancelled(true);
        }
    }

}
