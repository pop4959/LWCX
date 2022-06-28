/*
 * Copyright 2011 Tyler Blair. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ''AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and contributors and should not be interpreted as representing official policies,
 * either expressed or implied, of anybody else.
 */

package com.griefcraft.util.matchers;

import com.griefcraft.util.ProtectionFinder;
import com.griefcraft.util.VersionUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.EnumSet;
import java.util.Set;

/**
 * Matches blocks such as the sign post
 */
public class GravityMatcher implements ProtectionFinder.Matcher {

    public static final Set<Material> PROTECTABLES_POSTS;

    static {
        PROTECTABLES_POSTS = EnumSet.of(Material.RAIL,
                Material.ACTIVATOR_RAIL, Material.DETECTOR_RAIL, Material.POWERED_RAIL, Material.LEVER, Material.OAK_BUTTON,
                Material.BIRCH_BUTTON, Material.SPRUCE_BUTTON, Material.JUNGLE_BUTTON, Material.ACACIA_BUTTON,
                Material.DARK_OAK_BUTTON, Material.STONE_BUTTON, Material.OAK_PRESSURE_PLATE,
                Material.SPRUCE_PRESSURE_PLATE, Material.BIRCH_PRESSURE_PLATE, Material.JUNGLE_PRESSURE_PLATE,
                Material.ACACIA_PRESSURE_PLATE, Material.DARK_OAK_PRESSURE_PLATE, Material.STONE_PRESSURE_PLATE,
                Material.LIGHT_WEIGHTED_PRESSURE_PLATE, Material.HEAVY_WEIGHTED_PRESSURE_PLATE, Material.WHITE_BANNER,
                Material.ORANGE_BANNER, Material.MAGENTA_BANNER, Material.LIGHT_BLUE_BANNER, Material.YELLOW_BANNER,
                Material.LIME_BANNER, Material.PINK_BANNER, Material.GRAY_BANNER, Material.LIGHT_GRAY_BANNER,
                Material.CYAN_BANNER, Material.PURPLE_BANNER, Material.BLUE_BANNER, Material.BROWN_BANNER,
                Material.GREEN_BANNER, Material.RED_BANNER, Material.BLACK_BANNER, Material.ARMOR_STAND);
        if (VersionUtil.getMinorVersion() > 13) {
            PROTECTABLES_POSTS.addAll(EnumSet.of(Material.OAK_SIGN, Material.BIRCH_SIGN,
                    Material.SPRUCE_SIGN, Material.JUNGLE_SIGN, Material.ACACIA_SIGN, Material.DARK_OAK_SIGN));
        } else {
            PROTECTABLES_POSTS.add(Material.getMaterial("SIGN"));
        }
        if (VersionUtil.getMinorVersion() > 15) {
            PROTECTABLES_POSTS.addAll(EnumSet.of(Material.CRIMSON_BUTTON, Material.WARPED_BUTTON,
                    Material.POLISHED_BLACKSTONE_BUTTON, Material.CRIMSON_PRESSURE_PLATE, Material.WARPED_PRESSURE_PLATE,
                    Material.POLISHED_BLACKSTONE_PRESSURE_PLATE, Material.CRIMSON_SIGN, Material.WARPED_SIGN));
        }
        if (VersionUtil.getMinorVersion() > 18) {
            PROTECTABLES_POSTS.addAll(EnumSet.of(Material.MANGROVE_BUTTON, Material.MANGROVE_PRESSURE_PLATE,
                    Material.MANGROVE_SIGN));
        }
    }

    public boolean matches(ProtectionFinder finder) {
        Block block = finder.getBaseBlock().getBlock();

        // Easy to match, just try to match the block above the base block :P
        Block up = block.getRelative(BlockFace.UP);

        if (PROTECTABLES_POSTS.contains(up.getType())) {
            finder.addBlock(up);
            return true;
        }
        return false;
    }

}
