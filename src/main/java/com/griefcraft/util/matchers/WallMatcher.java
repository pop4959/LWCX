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

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Banner;
import org.bukkit.material.Sign;

import java.util.EnumSet;
import java.util.Set;

/**
 * Matches wall entities TODO fix buttons and levers
 */
public class WallMatcher implements ProtectionFinder.Matcher {

    /**
     * Blocks that can be attached to the wall and be protected. This assumes that
     * the block is DESTROYED if the wall they are attached to is broken.
     */
    public static final Set<Material> PROTECTABLES_WALL = EnumSet.of(Material.WALL_SIGN, Material.WHITE_WALL_BANNER,
            Material.ORANGE_WALL_BANNER, Material.MAGENTA_WALL_BANNER, Material.LIGHT_BLUE_WALL_BANNER,
            Material.YELLOW_WALL_BANNER, Material.LIME_WALL_BANNER, Material.PINK_WALL_BANNER,
            Material.GRAY_WALL_BANNER, Material.LIGHT_GRAY_WALL_BANNER, Material.CYAN_WALL_BANNER,
            Material.PURPLE_WALL_BANNER, Material.BLUE_WALL_BANNER, Material.BROWN_WALL_BANNER,
            Material.GREEN_WALL_BANNER, Material.RED_WALL_BANNER, Material.BLACK_WALL_BANNER);

    /**
     * Those evil levers and buttons have all different bits for directions. Gah!
     */
    public static final Set<Material> PROTECTABLES_LEVERS_ET_AL = EnumSet.of(Material.LEVER, Material.OAK_BUTTON,
            Material.BIRCH_BUTTON, Material.SPRUCE_BUTTON, Material.JUNGLE_BUTTON, Material.ACACIA_BUTTON,
            Material.DARK_OAK_BUTTON, Material.STONE_BUTTON);

    /**
     * Possible faces around the base block that protections could be at
     */
    public static final BlockFace[] POSSIBLE_FACES = new BlockFace[]{BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST,
            BlockFace.WEST};

    public boolean matches(ProtectionFinder finder) {
        // The block we are working on
        Block block = finder.getBaseBlock().getBlock();

        // Match wall signs to the wall it's attached to
        for (BlockFace blockFace : POSSIBLE_FACES) {
            Block face; // the relative block

            if ((face = block.getRelative(blockFace)) != null) {
                // Try and match it
                Block matched = tryMatchBlock(face, blockFace);

                // We found something ..! Try and load the protection
                if (matched != null) {
                    finder.addBlock(matched);
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Try and match a wall block
     *
     * @param block
     * @param matchingFace
     * @return
     */
    private Block tryMatchBlock(Block block, BlockFace matchingFace) {
        byte direction = block.getData();

        // Blocks such as wall signs
        if (PROTECTABLES_WALL.contains(block.getType())) {
            if (block.getType() == Material.WALL_SIGN) {
                if (((Sign) block.getState().getData()).getAttachedFace().getOppositeFace() == matchingFace) {
                    return block;
                }
            } else if (block.getType().name().contains("_WALL_BANNER")) {
                if (((Banner) block.getState().getData()).getAttachedFace().getOppositeFace() == matchingFace) {
                    return block;
                }
            }
        }

        // Levers, buttons
        else if (PROTECTABLES_LEVERS_ET_AL.contains(block.getType())) {
            byte EAST = 0x4;
            byte WEST = 0x3;
            byte SOUTH = 0x1;
            byte NORTH = 0x2;

            if (matchingFace == BlockFace.EAST && (direction & EAST) == EAST) {
                return block;
            } else if (matchingFace == BlockFace.WEST && (direction & WEST) == WEST) {
                return block;
            } else if (matchingFace == BlockFace.SOUTH && (direction & SOUTH) == SOUTH) {
                return block;
            } else if (matchingFace == BlockFace.NORTH && (direction & NORTH) == NORTH) {
                return block;
            }
        }

        return null;
    }

}
