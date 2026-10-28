package com.griefcraft.util;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Shelf;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ConnectedShelves {
    private static final boolean CAN_USE = classExists();

    private static boolean classExists() {
        try {
            Class.forName("org.bukkit.block.data.type.Shelf");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean canUse() {
        return CAN_USE;
    }

    public static List<Block> connectedShelves(Block block) {
        if (!(block.getBlockData() instanceof Shelf shelf)) {
            return List.of();
        }

        final List<Block> list = new ArrayList<>(3);
        list.add(block);

        switch (shelf.getSideChain()) {
            case UNCONNECTED -> {
            }
            case RIGHT -> {
                final Block center = adjacent(block, Direction.LEFT);
                if (center != null) {
                    list.add(center);
                    final Block left = adjacent(center, Direction.LEFT);
                    if (left != null) {
                        list.add(left);
                    }
                }
            }
            case CENTER -> {
                final Block left = adjacent(block, Direction.LEFT);
                if (left != null) {
                    list.add(left);
                }
                final Block right = adjacent(block, Direction.RIGHT);
                if (right != null) {
                    list.add(right);
                }
            }
            case LEFT -> {
                final Block center = adjacent(block, Direction.RIGHT);
                if (center != null) {
                    list.add(center);
                    final Block right = adjacent(center, Direction.RIGHT);
                    if (right != null) {
                        list.add(right);
                    }
                }
            }
        }

        return list;
    }

    private enum Direction {LEFT, RIGHT}

    private static @Nullable Block adjacent(@Nullable Block block, Direction direction) {
        if (block == null || !(block.getBlockData() instanceof Directional directional)) {
            return null;
        }
        final BlockFace adjacentFace = switch (directional.getFacing()) {
            case NORTH -> direction == Direction.LEFT ? BlockFace.EAST : BlockFace.WEST;
            case SOUTH -> direction == Direction.LEFT ? BlockFace.WEST : BlockFace.EAST;
            case EAST -> direction == Direction.LEFT ? BlockFace.SOUTH : BlockFace.NORTH;
            case WEST -> direction == Direction.LEFT ? BlockFace.NORTH : BlockFace.SOUTH;
            default -> null;
        };
        if (adjacentFace != null) {
            final Block adjacent = block.getRelative(adjacentFace);
            if (adjacent.getBlockData() instanceof Shelf shelf && shelf.getSideChain() != Shelf.SideChain.UNCONNECTED) {
                return adjacent;
            }
        }
        return null;
    }
}
