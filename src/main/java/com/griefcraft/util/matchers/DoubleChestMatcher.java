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
import org.bukkit.block.BlockState;
import org.bukkit.block.data.type.Chest;

import java.util.EnumSet;
import java.util.Set;

/**
 * Matches double chests
 */
public class DoubleChestMatcher implements ProtectionFinder.Matcher {

    /**
     * Blocks that act like double chests
     */
    public static final Set<Material> PROTECTABLES_CHESTS = EnumSet.of(Material.CHEST, Material.TRAPPED_CHEST);

    /**
     * Possible faces around the base block that protections could be at
     */
    public static final BlockFace[] POSSIBLE_FACES = new BlockFace[]{BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST};

    public boolean matches(ProtectionFinder finder) {
        // get the block state if it's a chest
        BlockState baseBlockState = finder.getBaseBlock();
        Chest baseBlockData = null;
        try {
            baseBlockData = (Chest) baseBlockState.getBlockData();
        } catch (ClassCastException e) {
            return false;
        }

        // get the block face for the neighboring chest if there is one
        BlockFace neighboringBlockFace = getNeighboringChestBlockFace(baseBlockData);
        if (neighboringBlockFace == null) {
            return false;
        }

        // if the neighboring block is a chest as well, we have a match
        Block neighboringBlock = baseBlockState.getBlock().getRelative(neighboringBlockFace);
        if (baseBlockState.getType() == neighboringBlock.getType()) {
            finder.addBlock(neighboringBlock);
            return true;
        }

        return false;
    }

    public static BlockFace getNeighboringChestBlockFace(Chest chest) {
        if (chest.getType() == Chest.Type.SINGLE)
            return null;
        BlockFace face = chest.getFacing();
        switch (face) {
            case NORTH:
                return chest.getType() == Chest.Type.LEFT ? BlockFace.EAST : BlockFace.WEST;
            case SOUTH:
                return chest.getType() == Chest.Type.LEFT ? BlockFace.WEST : BlockFace.EAST;
            case EAST:
                return chest.getType() == Chest.Type.LEFT ? BlockFace.SOUTH : BlockFace.NORTH;
            case WEST:
                return chest.getType() == Chest.Type.LEFT ? BlockFace.NORTH : BlockFace.SOUTH;
            default:
                return null;
        }
    }

}
