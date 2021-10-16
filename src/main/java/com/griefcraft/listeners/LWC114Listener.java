package com.griefcraft.listeners;

import com.griefcraft.integration.IPermissions;
import com.griefcraft.lwc.LWC;
import com.griefcraft.model.Permission;
import com.griefcraft.model.Protection;
import org.bukkit.entity.Player;
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
        Player player = event.getPlayer();
        boolean canAdmin = lwc.canAdminProtection(player, protection);
        boolean canAccess = false;
        if (protection.getAccess(player.getUniqueId().toString(), Permission.Type.PLAYER) == Permission.Access.PLAYER) {
            canAccess = true;
        } else if (protection.getAccess(player.getName(), Permission.Type.PLAYER) == Permission.Access.PLAYER) {
            canAccess = true;
        } else {
            IPermissions permissions = lwc.getPermissions();
            if (permissions != null) {
                for (String groupName : permissions.getGroups(player)) {
                    if (protection.getAccess(groupName, Permission.Type.GROUP) == Permission.Access.PLAYER) {
                        canAccess = true;
                    }
                }
            }
        }
        if (!(canAdmin || canAccess)) {
            event.setCancelled(true);
        }
    }
}
