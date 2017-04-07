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

public class EntityBlock implements Block{
	public static final int ENTITY_BLOCK_ID = 5000;
	public static final int POSITION_OFFSET = 50000;
	private Entity entity;

	public EntityBlock(Entity entity) {
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
		return ENTITY_BLOCK_ID;
	}

	@Override
	public World getWorld() {
		return this.entity.getWorld();
	}

	public Entity getEntity() {
		return this.entity;
	}

	public static Block getEntityBlock(Entity entity) {
		return new EntityBlock(entity);
	}

	@Override
	public List<MetadataValue> getMetadata(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasMetadata(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeMetadata(String arg0, Plugin arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setMetadata(String arg0, MetadataValue arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean breakNaturally() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean breakNaturally(ItemStack arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Biome getBiome() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getBlockPower() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getBlockPower(BlockFace arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Chunk getChunk() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte getData() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Collection<ItemStack> getDrops() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<ItemStack> getDrops(ItemStack arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BlockFace getFace(Block arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getHumidity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte getLightFromBlocks() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte getLightFromSky() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte getLightLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Location getLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Location getLocation(Location arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PistonMoveReaction getPistonMoveReaction() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Block getRelative(BlockFace arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Block getRelative(BlockFace arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Block getRelative(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BlockState getState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getTemperature() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Material getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isBlockFaceIndirectlyPowered(BlockFace arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isBlockFacePowered(BlockFace arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isBlockIndirectlyPowered() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isBlockPowered() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isLiquid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setBiome(Biome arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setData(byte arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setData(byte arg0, boolean arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setType(Material arg0) {
		// TODO Auto-generated method stub

	}

	public void setType(Material arg0, boolean arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean setTypeId(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setTypeId(int arg0, boolean arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setTypeIdAndData(int arg0, byte arg1, boolean arg2) {
		// TODO Auto-generated method stub
		return false;
	}
}
