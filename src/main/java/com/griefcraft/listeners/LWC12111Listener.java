package com.griefcraft.listeners;

import com.griefcraft.lwc.LWC;
import com.griefcraft.model.Flag;
import com.griefcraft.model.Protection;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.entity.EntityTargetBlockEvent;

public class LWC12111Listener implements Listener {

    @SuppressWarnings("UnstableApiUsage")
    @EventHandler(ignoreCancelled = true)
    public void onEntityTargetBlockEvent(final EntityTargetBlockEvent event) {
        final Block target = event.getTarget();
        if (target == null) {
            return;
        }
        LWC lwc = LWC.getInstance();
        Protection protection = lwc.findProtection(target);
        if (protection == null || protection.getType() == Protection.Type.PUBLIC || protection.hasFlag(Flag.Type.GOLEM)) {
            return;
        }
        event.setCancelled(true);
    }
}
