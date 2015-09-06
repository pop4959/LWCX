package com.griefcraft.bukkit;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

public interface NMS extends Block {
	public static final int ENTITY_BLOCK_ID = 5000;
	public static final int POSITION_OFFSET = 50000;

	public int getX();

	public int getY();

	public int getZ();

	public int getTypeId();

	public World getWorld();

	public Entity getEntity();
}
