package com.griefcraft.bukkit;

import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R1.block.CraftBlock;
import org.bukkit.entity.Entity;

public class EntityBlock extends CraftBlock {
	public static final int ENTITY_BLOCK_ID = 5000;
	public static final int POSITION_OFFSET = 50000;
	private Entity entity;

	public EntityBlock(Entity entity) {
		super(null, 0, 0, 0);
		this.entity = entity;
	}

	public int getX() {
		return 50000 + this.entity.getUniqueId().hashCode();
	}

	public int getY() {
		return 50000 + this.entity.getUniqueId().hashCode();
	}

	public int getZ() {
		return 50000 + this.entity.getUniqueId().hashCode();
	}

	public int getTypeId() {
		return 5000;
	}

	public World getWorld() {
		return this.entity.getWorld();
	}

	public Entity getEntity() {
		return this.entity;
	}
}
