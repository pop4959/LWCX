package com.griefcraft.cache;

import com.griefcraft.lwc.LWC;
import com.griefcraft.util.UUIDRegistry;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.command.CommandSender;

/**
 *
 */
public class DefaultsCache {
    private final LWC lwc;
    private final Map<CommandSender, String> cache;


    public DefaultsCache(LWC lwc) {
        this.lwc = lwc;
        cache = new HashMap<>();
    }

    /**
     * Gets the default value from the cache, or if not set in cache, returns it from the DB.
     * @param sender
     * @return
     */
    public String getOrLoad(CommandSender sender) {
        if (sender == null) {
            return null;
        }
        if(cache.containsKey(sender)) {
            return cache.get(sender);
        }
        UUID uuid = UUIDRegistry.getUUID(sender.getName());
        String data = lwc.getPhysicalDatabase().loadDefault(uuid.toString());
        cache.put(sender, data);
        return data;
    }

    public void set(CommandSender sender, String data) {
        cache.put(sender, data);
    }

    public void clear(CommandSender sender) {
        cache.remove(sender);
    }

}
