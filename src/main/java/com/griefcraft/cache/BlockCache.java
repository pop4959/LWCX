package com.griefcraft.cache;

import com.griefcraft.lwc.LWC;
import com.griefcraft.sql.PhysDB;
import com.griefcraft.util.MaterialUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Loads and stores block mappings from the database, as well as caching them during use for performance.
 */
public class BlockCache {

    /**
     * The BlockCache singleton.
     */
    private static BlockCache blockCache = new BlockCache();

    /**
     * The LWC instance.
     */
    private LWC lwc;

    /**
     * Storing block mappings internally using two HashMaps, to allow bidirectional lookups.
     */
    private Map<Integer, Material> intBlockCache;
    private Map<Material, Integer> materialBlockCache;

    /**
     * The lowest available block ID for new additions.
     */
    private int nextId = 1;

    /**
     * Values that LWC will expect to be constant across databases.
     */
    public static enum Constants {
        AIR(0),
        ENTITY(1);

        private int id;

        Constants(int id) {
            this.id = id;
        }

        public int getValue() {
            return id;
        }
    }

    /**
     * Initialization for the block cache.
     */
    private BlockCache() {
        lwc = LWC.getInstance();
        intBlockCache = new HashMap<>();
        materialBlockCache = new HashMap<>();
    }

    /**
     * Gets the block cache.
     *
     * @return
     */
    public static BlockCache getInstance() {
        return blockCache;
    }

    /**
     * Clean up the singleton instance when disabling.
     */
    public static void destruct() {
        blockCache = null;
    }

    /**
     * Add a mapping to both maps. Only to be used internally.
     *
     * @param integer
     * @param material
     */
    private void addMapping(Integer integer, Material material) {
        intBlockCache.put(integer, material);
        materialBlockCache.put(material, integer);
    }

    /**
     * Remove a mapping from both maps. Only to be used internally.
     *
     * @param integer
     * @param material
     */
    private void removeMapping(Integer integer, Material material) {
        intBlockCache.remove(integer);
        materialBlockCache.remove(material);
    }

    /**
     * Loads all block mappings from the database.
     */
    public void loadBlocks() {
        PhysDB database = lwc.getPhysicalDatabase();
        String prefix = database.getPrefix();
        try {
            PreparedStatement statement = database.prepare("SELECT id, name FROM " + prefix + "blocks");
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                Integer materialId = set.getInt("id");
                String materialName = set.getString("name");
                Material material = Material.matchMaterial(materialName);
                if (material != null) {
                    addMapping(materialId, material);
                    if (materialId >= nextId) {
                        nextId = materialId + 1;
                    }
                } else {
                    lwc.log("Unable to load " + materialName + "from " + prefix + "blocks!");
                }
            }
        } catch (SQLException e) {
            lwc.log("Unable to load " + prefix + "blocks!");
            e.printStackTrace();
        }
    }

    /**
     * Adds a block the block cache by its Material type, and tries to add it if it doesn't exist.
     *
     * @param blockMaterial
     */
    public int addBlock(Material blockMaterial) {
        if (materialBlockCache.containsKey(blockMaterial)) {
            return materialBlockCache.get(blockMaterial);
        }
        PhysDB database = lwc.getPhysicalDatabase();
        String prefix = database.getPrefix();
        try {
            PreparedStatement statement = database.prepare("INSERT INTO " + prefix
                    + "blocks (id, name) VALUES(?, ?)");
            statement.setInt(1, nextId);
            statement.setString(2, blockMaterial.name());
            statement.executeUpdate();
            addMapping(++nextId, blockMaterial);
            return nextId - 1;
        } catch (SQLException e) {
            lwc.log("Unable to add new block to " + prefix + "blocks!");
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Adds a block to the block cache by its Block name, and tries to add it if it doesn't exist.
     *
     * @param block
     */
    public int addBlock(String block) {
        Material material = Material.matchMaterial(block);
        if (material != null) {
            return addBlock(material);
        }
        return -1;
    }

    /**
     * Adds a block to the block cache by its Block type, and tries to add it if it doesn't exist.
     *
     * @param block
     */
    public int addBlock(Block block) {
        return addBlock(block.getType());
    }

    /**
     * Adds a block to the block cache by its pre-1.13 ID, and tries to add it if it doesn't exist.
     *
     * @param blockId
     */
    public int addBlock(int blockId) {
        return addBlock(MaterialUtil.getMaterialById(blockId));
    }

    /**
     * Removes a block the block cache by its Material type, if it exists.
     *
     * @param blockMaterial
     */
    public void removeBlock(Material blockMaterial) {
        if (materialBlockCache.containsKey(blockMaterial)) {
            removeBlock(materialBlockCache.get(blockMaterial));
        }
    }

    public void removeBlock(String block) {
        Material material = Material.matchMaterial(block);
        if (material != null) {
            removeBlock(material);
        }
    }

    /**
     * Removes a block to the block cache by its Block type, if it exists.
     *
     * @param block
     */
    public void removeBlock(Block block) {
        removeBlock(block.getType());
    }

    /**
     * Removes a block by its ID in the database, if it exists.
     *
     * @param blockId
     */
    public void removeBlock(int blockId) {
        if (!intBlockCache.containsKey(blockId)) {
            return;
        }
        PhysDB database = lwc.getPhysicalDatabase();
        String prefix = database.getPrefix();
        try {
            PreparedStatement statement = database.prepare("DELETE FROM " + prefix + "blocks WHERE id = ?");
            statement.setInt(1, blockId);
            statement.executeUpdate();
            removeMapping(blockId, intBlockCache.get(blockId));
        } catch (SQLException e) {
            lwc.log("Unable to remove block from " + prefix + "blocks!");
            e.printStackTrace();
        }
    }

    /**
     * Get a block's id, or try to add it if it doesn't exist.
     *
     * @param blockMaterial
     * @return
     */
    public int getBlockId(Material blockMaterial) {
        Integer id = materialBlockCache.get(blockMaterial);
        if (id != null) {
            return id;
        }
        return addBlock(blockMaterial);
    }

    /**
     * Get a block's id, or try to add it if it doesn't exist.
     *
     * @param blockName
     * @return
     */
    public int getBlockId(String blockName) {
        Material material = Material.matchMaterial(blockName);
        if (material != null) {
            return getBlockId(material);
        }
        return addBlock(blockName);
    }

    /**
     * Get a block's id, or try to add it if it doesn't exist.
     *
     * @param block
     * @return
     */
    public int getBlockId(Block block) {
        return getBlockId(block.getType());
    }

    /**
     * Get a block's id, or try to add it if it doesn't exist.
     *
     * @param blockId
     * @return
     */
    public int getBlockId(int blockId) {
        if (intBlockCache.containsKey(blockId)) {
            return blockId;
        }
        return addBlock(blockId);
    }

    /**
     * Get a block's type, or try to add it if it doesn't exist.
     *
     * @param blockMaterial
     * @return
     */
    public Material getBlockType(Material blockMaterial) {
        if (materialBlockCache.containsKey(blockMaterial)) {
            return blockMaterial;
        }
        int id = addBlock(blockMaterial);
        if (intBlockCache.containsKey(id)) {
            return intBlockCache.get(id);
        }
        return null;
    }

    /**
     * Get a block's type, or try to add it if it doesn't exist.
     *
     * @param blockName
     * @return
     */
    public Material getBlockType(String blockName) {
        Material material = Material.matchMaterial(blockName);
        if (material != null) {
            return getBlockType(material);
        }
        return null;
    }

    /**
     * Get a block's type, or try to add it if it doesn't exist.
     *
     * @param block
     * @return
     */
    public Material getBlockType(Block block) {
        return getBlockType(block.getType());
    }

    /**
     * Get a block's type, or try to add it if it doesn't exist.
     *
     * @param blockId
     * @return
     */
    public Material getBlockType(int blockId) {
        if (intBlockCache.containsKey(blockId)) {
            return intBlockCache.get(blockId);
        }
        Material material = MaterialUtil.getMaterialById(blockId);
        if (material != null) {
            int id = addBlock(material);
            if (intBlockCache.containsKey(id)) {
                return intBlockCache.get(id);
            }
        }
        return null;
    }

}
