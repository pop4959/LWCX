package com.griefcraft.util.matchers;

import com.griefcraft.util.ProtectionFinder;
import com.griefcraft.util.VersionUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

/**
 * Matches hanging blocks
 */
public class HangingMatcher implements ProtectionFinder.Matcher {
    public static final Set<Material> PROTECTABLES_HANGING = EnumSet.noneOf(Material.class);

    static {
        if (VersionUtil.getMinorVersion() > 19) {
            PROTECTABLES_HANGING.addAll(EnumSet.of(Material.OAK_HANGING_SIGN, Material.BIRCH_HANGING_SIGN,
                    Material.SPRUCE_HANGING_SIGN, Material.JUNGLE_HANGING_SIGN, Material.ACACIA_HANGING_SIGN,
                    Material.DARK_OAK_HANGING_SIGN, Material.CRIMSON_HANGING_SIGN, Material.WARPED_HANGING_SIGN,
                    Material.MANGROVE_HANGING_SIGN, Material.BAMBOO_HANGING_SIGN, Material.CHERRY_HANGING_SIGN));
        }
        if (VersionUtil.getMinorVersion() > 20) {
            // Added in 1.21.4
            Optional.ofNullable(Material.getMaterial("PALE_OAK_HANGING_SIGN")).ifPresent(PROTECTABLES_HANGING::add);
        }
    }

    @Override
    public boolean matches(ProtectionFinder finder) {
        // Nothing to match
        if (PROTECTABLES_HANGING.isEmpty()) {
            return false;
        }

        final Block block = finder.getBaseBlock().getBlock();

        boolean found = false;
        Block below = block;
        while (PROTECTABLES_HANGING.contains((below = below.getRelative(BlockFace.DOWN)).getType())) {
            finder.addBlock(below);
            found = true;
        }

        return found;
    }

}
