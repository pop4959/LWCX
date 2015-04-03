package com.griefcraft.bukkit.v1_7_R4;

import org.bukkit.block.BlockState;
import org.bukkit.entity.minecart.*;

import com.griefcraft.bukkit.StorageNMS;

public class StoreageMinecartBlock extends EntityBlock  implements StorageNMS {
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
