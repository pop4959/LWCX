package com.griefcraft.bukkit;

import org.bukkit.block.BlockState;
import org.bukkit.entity.minecart.*;

public class StoreageMinecartBlock extends EntityBlock {
	private StorageMinecart minecart;

	public StoreageMinecartBlock(StorageMinecart minecart) {
		super(minecart);
		this.minecart = minecart;
	}

	public BlockState getState() {
		return (BlockState) this.minecart.getInventory().getHolder();
	}

	public StorageMinecart getMinecart() {
		return this.minecart;
	}
}
