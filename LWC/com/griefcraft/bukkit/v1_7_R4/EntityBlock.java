package com.griefcraft.bukkit.v1_7_R4;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R4.block.CraftBlock;
import org.bukkit.entity.Entity;

import com.griefcraft.bukkit.NMS;

public class EntityBlock extends CraftBlock implements NMS {

	public static final int ENTITY_BLOCK_ID = 5000;
	public static final int POSITION_OFFSET = 50000;
	private Entity entity;

	public EntityBlock(Entity entity) {
		super(null, 0, 0, 0);
		this.entity = entity;
	}

	@Override
	public int getX() {
		return 50000 + this.entity.getUniqueId().hashCode();
	}

	@Override
	public int getY() {
		return 50000 + this.entity.getUniqueId().hashCode();
	}

	@Override
	public int getZ() {
		return 50000 + this.entity.getUniqueId().hashCode();
	}

	@Override
	public int getTypeId() {
		return 5000;
	}

	@Override
	public World getWorld() {
		return this.entity.getWorld();
	}

	@Override
	public Entity getEntity() {
		return this.entity;
	}

	@Override
	public Block getEntityBlock(Entity entity) {
		return new EntityBlock(entity);
	}
}