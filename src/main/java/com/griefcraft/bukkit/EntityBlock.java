package com.griefcraft.bukkit;

import org.bukkit.Chunk;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.bukkit.util.VoxelShape;

import java.util.Collection;
import java.util.List;

public class EntityBlock implements Block {
    private static Entity entity;
    public static final int ENTITY_BLOCK_ID = 5000;
    public static String ENTITY_BLOCK_NAME = "Entity";
    public static final int POSITION_OFFSET = 50000;

    public EntityBlock(Entity entity) {
        EntityBlock.entity = entity;
        ENTITY_BLOCK_NAME = entity.getType().name();
    }

    @Override
    public int getX() {
        return 50000 + EntityBlock.entity.getUniqueId().hashCode();
    }

    @Override
    public int getY() {
        return 50000 + EntityBlock.entity.getUniqueId().hashCode();
    }

    @Override
    public int getZ() {
        return 50000 + EntityBlock.entity.getUniqueId().hashCode();
    }

    public int getTypeId() {
        return ENTITY_BLOCK_ID;
    }

    @Override
    public World getWorld() {
        return EntityBlock.entity.getWorld();
    }

    public static Entity getEntity() {
        return EntityBlock.entity;
    }

    public static Block getEntityBlock(Entity entity) {
        return new EntityBlock(entity);
    }

    @Override
    public List<MetadataValue> getMetadata(String s) {
        return entity.getMetadata(s);
    }

    @Override
    public boolean hasMetadata(String s) {
        return entity.hasMetadata(s);
    }

    @Override
    public void removeMetadata(String s, Plugin p) {
        entity.removeMetadata(s, p);
    }

    @Override
    public void setMetadata(String s, MetadataValue mv) {
        entity.setMetadata(s, mv);
    }

    @Override
    public boolean breakNaturally() {
        return false;
    }

    @Override
    public boolean breakNaturally(ItemStack itemStack) {
        return false;
    }

    @Override
    public boolean applyBoneMeal(BlockFace blockFace) {
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
        throw new IllegalStateException("getData should not be called.");
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
    public Collection<ItemStack> getDrops(ItemStack itemStack, Entity entity) {
        return null;
    }

    @Override
    public boolean isPreferredTool(ItemStack itemStack) {
        return false;
    }

    @Override
    public float getBreakSpeed(Player player) {
        return 0;
    }

    @Override
    public boolean isPassable() {
        return false;
    }

    @Override
    public RayTraceResult rayTrace(Location location, Vector vector, double v, FluidCollisionMode fluidCollisionMode) {
        return null;
    }

    @Override
    public BoundingBox getBoundingBox() {
        return null;
    }

    @Override
    public VoxelShape getCollisionShape() {
        return null;
    }

    @Override
    public boolean canPlace(BlockData blockData) {
        return false;
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
        return entity.getLocation();
    }

    @Override
    public Location getLocation(Location arg0) {
        return entity.getLocation(arg0);
    }

    @Override
    public PistonMoveReaction getPistonMoveReaction() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Block getRelative(BlockFace arg0) {
        // TODO Auto-generated method stub
        return this;
    }

    @Override
    public Block getRelative(BlockFace arg0, int arg1) {
        // TODO Auto-generated method stub
        return this;
    }

    @Override
    public Block getRelative(int arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub
        return this;
    }

    @Override
    public BlockState getState() {
        return new EntityBlockState(this);
    }

    @Override
    public double getTemperature() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Material getType() {
        // Temporary fix to avoid null pointer exceptions when using /lock on entities.
        // Entity protections are still locked under ENTITY_BLOCK_ID, not AIR.
        return Material.AIR;
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
    public void setType(Material arg0) {
        // TODO Auto-generated method stub

    }

    public void setType(Material arg0, boolean arg1) {
        // TODO Auto-generated method stub
    }


    @Override
    public BlockData getBlockData() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setBlockData(BlockData arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setBlockData(BlockData arg0, boolean arg1) {
        // TODO Auto-generated method stub

    }
}
