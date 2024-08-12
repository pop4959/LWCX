package com.griefcraft.bukkit;

import com.griefcraft.util.InvHolderUtil;
import org.bukkit.block.BlockState;
import org.bukkit.entity.minecart.HopperMinecart;

public class HopperMinecartBlock extends EntityBlock {
    private HopperMinecart minecart;

    public HopperMinecartBlock(HopperMinecart minecart) {
        super(minecart);
        this.minecart = minecart;
    }

    public BlockState getState() {
        return (BlockState) InvHolderUtil.get(this.minecart.getInventory());
    }

    public HopperMinecart getMinecart() {
        return this.minecart;
    }
}
