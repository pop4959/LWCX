package com.griefcraft.listeners;

import com.griefcraft.lwc.LWC;
import com.griefcraft.lwc.LWCPlugin;
import com.griefcraft.model.Flag;
import com.griefcraft.model.Protection;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

/**
 * Cancels golems (and similar) from targeting blocks behind private protections without the GOLEM flag.
 * Uses {@link org.bukkit.event.entity.EntityTargetBlockEvent} when present at runtime; skips registration otherwise.
 */
public final class LWC12111Listener {

    private static final String ENTITY_TARGET_BLOCK_EVENT = "org.bukkit.event.entity.EntityTargetBlockEvent";

    private LWC12111Listener() {
    }

    @SuppressWarnings("unchecked")
    public static void register(LWCPlugin plugin) {
        final Class<?> rawClass;
        try {
            rawClass = Class.forName(ENTITY_TARGET_BLOCK_EVENT, false, Bukkit.class.getClassLoader());
        } catch (ClassNotFoundException | LinkageError e) {
            return;
        }
        if (!Event.class.isAssignableFrom(rawClass)) {
            return;
        }
        Class<? extends Event> eventClass = (Class<? extends Event>) rawClass.asSubclass(Event.class);
        EventExecutor executor = (listener, event) -> handleEntityTargetBlock(event);
        Bukkit.getPluginManager().registerEvent(
                eventClass,
                new Listener() {
                },
                EventPriority.NORMAL,
                executor,
                plugin,
                true);
    }

    private static void handleEntityTargetBlock(Event event) {
        try {
            Object targetObj = event.getClass().getMethod("getTarget").invoke(event);
            if (!(targetObj instanceof Block target)) {
                return;
            }
            LWC lwc = LWC.getInstance();
            Protection protection = lwc.findProtection(target);
            if (protection == null || protection.getType() == Protection.Type.PUBLIC || protection.hasFlag(Flag.Type.GOLEM)) {
                return;
            }
            event.getClass().getMethod("setCancelled", boolean.class).invoke(event, true);
        } catch (ReflectiveOperationException ignored) {
        }
    }
}
