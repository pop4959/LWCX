package com.griefcraft.util.matchers;

import com.griefcraft.util.ProtectionFinder;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.type.Bed;

import java.util.EnumSet;
import java.util.Set;

/**
 * Matches beds
 */
public class BedMatcher implements ProtectionFinder.Matcher {

    public static final Set<Material> BEDS = EnumSet.of(Material.BLACK_BED, Material.BLUE_BED, Material.BROWN_BED,
            Material.CYAN_BED, Material.GRAY_BED, Material.GREEN_BED, Material.LIGHT_BLUE_BED, Material.LIGHT_GRAY_BED,
            Material.LIME_BED, Material.MAGENTA_BED, Material.ORANGE_BED, Material.PINK_BED, Material.PURPLE_BED,
            Material.RED_BED, Material.WHITE_BED, Material.YELLOW_BED);

    @Override
    public boolean matches(ProtectionFinder finder) {
        BlockState baseBlockState = finder.getBaseBlock();
        Block baseBlock = baseBlockState.getBlock();
        Bed baseBlockData;
        try {
            baseBlockData = (Bed) baseBlockState.getBlockData();
            if (baseBlockData.getPart() == Bed.Part.FOOT) {
                finder.addBlock(baseBlock.getRelative(baseBlockData.getFacing()));
            } else {
                finder.addBlock(baseBlock.getRelative(baseBlockData.getFacing().getOppositeFace()));
            }
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }

}
