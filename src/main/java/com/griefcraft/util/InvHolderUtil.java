package com.griefcraft.util;

import io.papermc.lib.features.inventoryholdersnapshot.InventoryHolderSnapshotOptionalSnapshots;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class InvHolderUtil {

    private static final InventoryHolderSnapshotOptionalSnapshots inventoryHolderSnapshotOptionalSnapshots = new InventoryHolderSnapshotOptionalSnapshots();
    private static boolean optionalSnapshots = false;


    public static void setOptionalSnapshots(boolean b) {
        optionalSnapshots = b;
    }

    public static InventoryHolder get(Inventory inventory) {
        if (optionalSnapshots) {
            return inventoryHolderSnapshotOptionalSnapshots.getHolder(inventory, false).getHolder();
        } else {
            return inventory.getHolder();
        }
    }
}
