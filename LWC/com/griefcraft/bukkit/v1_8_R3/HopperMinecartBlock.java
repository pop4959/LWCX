package com.griefcraft.bukkit.v1_8_R3;

import org.bukkit.block.BlockState;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.entity.minecart.StorageMinecart;

import com.griefcraft.bukkit.HopperNMS;

public class HopperMinecartBlock extends EntityBlock implements HopperNMS {
	private StorageMinecart minecart;

	public HopperMinecartBlock(StorageMinecart minecart) {
		super(minecart);
		this.minecart = minecart;
	}

	public BlockState getState() {
		return (BlockState) this.minecart.getInventory().getHolder();
	}

	public HopperMinecart getMinecart() {
		return (HopperMinecart) this.minecart;
	}
}
