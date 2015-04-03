package com.griefcraft.bukkit;

import org.bukkit.block.BlockState;
import org.bukkit.entity.minecart.StorageMinecart;

public interface StorageNMS {
	public BlockState getState();

	public StorageMinecart getMinecart();
}
