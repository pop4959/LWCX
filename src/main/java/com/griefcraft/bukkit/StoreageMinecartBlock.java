package com.griefcraft.bukkit;

import com.griefcraft.util.InvHolderUtil;
import org.bukkit.block.BlockState;
import org.bukkit.entity.minecart.StorageMinecart;

public class StoreageMinecartBlock extends EntityBlock {
    private StorageMinecart minecart;

    public StoreageMinecartBlock(StorageMinecart minecart) {
        super(minecart);
        this.minecart = minecart;
    }

    public BlockState getState() {
        return (BlockState) InvHolderUtil.get(this.minecart.getInventory());
    }

    public StorageMinecart getMinecart() {
        return this.minecart;
    }
}
