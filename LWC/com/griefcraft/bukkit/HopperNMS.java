package com.griefcraft.bukkit;

import org.bukkit.block.BlockState;
import org.bukkit.entity.minecart.HopperMinecart;

public interface HopperNMS {
	public BlockState getState();

	public HopperMinecart getMinecart();
}
