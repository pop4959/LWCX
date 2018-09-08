package com.griefcraft.bukkit;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import java.util.List;

@SuppressWarnings("deprecation")
public class EntityBlockState implements BlockState {
    private static EntityBlock entityBlock;

    public EntityBlockState(EntityBlock entityBlock) {
        EntityBlockState.entityBlock = entityBlock; // TODO: Should do deep copy.
    }

    public static EntityBlock getEntityBlock() {
        return EntityBlockState.entityBlock;
    }

    public void setEntityBlock(EntityBlock entityBlock) {
        EntityBlockState.entityBlock = entityBlock;
    }

    @Override
    public Block getBlock() {
        return entityBlock;
    }

    @Override
    public Material getType() {
        return entityBlock.getType();
    }

    @Override
    public byte getLightLevel() {
        return entityBlock.getLightLevel();
    }

    @Override
    public World getWorld() {
        return entityBlock.getWorld();
    }

    @Override
    public int getX() {
        return entityBlock.getX();
    }

    @Override
    public int getY() {
        return entityBlock.getY();
    }

    @Override
    public int getZ() {
        return entityBlock.getZ();
    }

    @Override
    public Location getLocation() {
        return entityBlock.getLocation();
    }

    @Override
    public Location getLocation(Location location) {
        return entityBlock.getLocation(); // TODO: What to do with param location
    }

    @Override
    public Chunk getChunk() {
        return entityBlock.getChunk();
    }

    @Override
    public void setData(MaterialData materialData) {
        // Yeah, this does not work: entityBlock.setData(materialData.getData());
        // TODO: What to do with it, deprecated?
    }

    @Override
    public void setType(Material material) {
        entityBlock.setType(material);
    }

    @Override
    public boolean update() {
        return false;
    }

    @Override
    public boolean update(boolean b) {
        return false;
    }

    @Override
    public boolean update(boolean b, boolean b1) {
        return false;
    }

    @Override
    public byte getRawData() {
        return 0;
    }

    @Override
    public void setRawData(byte b) {

    }

    @Override
    public boolean isPlaced() {
        return false;
    }

    @Override
    public void setMetadata(String s, MetadataValue metadataValue) {

    }

    @Override
    public List<MetadataValue> getMetadata(String s) {
        return null;
    }

    @Override
    public boolean hasMetadata(String s) {
        return false;
    }

    @Override
    public void removeMetadata(String s, Plugin plugin) {

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
    public MaterialData getData() {
        // TODO Auto-generated method stub
        return null;
    }
}
