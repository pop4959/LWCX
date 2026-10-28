package com.griefcraft.listeners;

import com.griefcraft.lwc.LWC;
import com.griefcraft.model.Flag;
import com.griefcraft.model.Protection;
import io.papermc.paper.event.entity.ItemTransportingEntityValidateTargetEvent;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class LWC12111Listener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onItemTransportingEntityValidateTargetEvent(final ItemTransportingEntityValidateTargetEvent event) {
        final LWC plugin = LWC.getInstance();
        final Block block = event.getBlock();
        final Protection protection = plugin.findProtection(block);
        if (protection == null || protection.getType() == Protection.Type.PUBLIC) {
            return;
        }
        boolean denyGolems = Boolean.parseBoolean(plugin.resolveProtectionConfiguration(block, "denyGolems"));
        if (denyGolems == protection.hasFlag(Flag.Type.GOLEM)) {
            return;
        }
        event.setAllowed(false);
    }
}
