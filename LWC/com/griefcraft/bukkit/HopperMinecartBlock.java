package com.griefcraft.bukkit;

import org.bukkit.block.BlockState;
import org.bukkit.entity.minecart.HopperMinecart;

public class HopperMinecartBlock extends EntityBlock {
	private HopperMinecart minecart;

	public HopperMinecartBlock(HopperMinecart minecart) {
		super(minecart);
		this.minecart = minecart;
	}

	public BlockState getState() {
		return (BlockState) this.minecart.getInventory().getHolder();
	}

	public HopperMinecart getMinecart() {
		return this.minecart;
	}
}
