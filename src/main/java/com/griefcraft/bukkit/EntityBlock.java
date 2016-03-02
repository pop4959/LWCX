package com.griefcraft.bukkit;

import java.util.Collection;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

public class EntityBlock implements Block, NMS {
	public static final int ENTITY_BLOCK_ID = 5000;
	public static final int POSITION_OFFSET = 50000;
	private Entity entity;

	public EntityBlock(Entity entity) {
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
		return ENTITY_BLOCK_ID;
	}

	public World getWorld() {
		return this.entity.getWorld();
	}

	public Entity getEntity() {
		return this.entity;
	}

	public static Block getEntityBlock(Entity entity) {
		return new EntityBlock(entity);
	}

	public List<MetadataValue> getMetadata(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasMetadata(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public void removeMetadata(String arg0, Plugin arg1) {
		// TODO Auto-generated method stub

	}

	public void setMetadata(String arg0, MetadataValue arg1) {
		// TODO Auto-generated method stub

	}

	public boolean breakNaturally() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean breakNaturally(ItemStack arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public Biome getBiome() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getBlockPower() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getBlockPower(BlockFace arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Chunk getChunk() {
		// TODO Auto-generated method stub
		return null;
	}

	public byte getData() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Collection<ItemStack> getDrops() {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<ItemStack> getDrops(ItemStack arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public BlockFace getFace(Block arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public double getHumidity() {
		// TODO Auto-generated method stub
		return 0;
	}

	public byte getLightFromBlocks() {
		// TODO Auto-generated method stub
		return 0;
	}

	public byte getLightFromSky() {
		// TODO Auto-generated method stub
		return 0;
	}

	public byte getLightLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Location getLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	public Location getLocation(Location arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public PistonMoveReaction getPistonMoveReaction() {
		// TODO Auto-generated method stub
		return null;
	}

	public Block getRelative(BlockFace arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Block getRelative(BlockFace arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public Block getRelative(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	public BlockState getState() {
		// TODO Auto-generated method stub
		return null;
	}

	public double getTemperature() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Material getType() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isBlockFaceIndirectlyPowered(BlockFace arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isBlockFacePowered(BlockFace arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isBlockIndirectlyPowered() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isBlockPowered() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isLiquid() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setBiome(Biome arg0) {
		// TODO Auto-generated method stub

	}

	public void setData(byte arg0) {
		// TODO Auto-generated method stub

	}

	public void setData(byte arg0, boolean arg1) {
		// TODO Auto-generated method stub

	}

	public void setType(Material arg0) {
		// TODO Auto-generated method stub

	}

	public void setType(Material arg0, boolean arg1) {
		// TODO Auto-generated method stub

	}

	public boolean setTypeId(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean setTypeId(int arg0, boolean arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean setTypeIdAndData(int arg0, byte arg1, boolean arg2) {
		// TODO Auto-generated method stub
		return false;
	}
}