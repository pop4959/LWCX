package com.griefcraft.bukkit.v1_7_R4;

import org.bukkit.block.BlockState;
import org.bukkit.entity.minecart.HopperMinecart;

import com.griefcraft.bukkit.HopperNMS;

public class HopperMinecartBlock extends EntityBlock implements HopperNMS {
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
