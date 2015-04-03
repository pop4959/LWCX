package com.griefcraft.bukkit.v1_8_R2;

import org.bukkit.block.BlockState;
import org.bukkit.entity.minecart.*;
import org.bukkit.event.Listener;
import com.griefcraft.bukkit.StorageNMS;

public class StoreageMinecartBlock extends EntityBlock implements StorageNMS,
		Listener {
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
