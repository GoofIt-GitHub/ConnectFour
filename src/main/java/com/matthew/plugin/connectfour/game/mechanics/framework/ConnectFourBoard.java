package com.matthew.plugin.connectfour.game.mechanics.framework;

import com.matthew.plugin.connectfour.utils.DirectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class ConnectFourBoard {

    /**
     * The bottom row of quartz blocks representing the bottom of the Connect Four board.
     */
    private List<Block> bottomBlocks;

    /**
     * The top row of quartz blocks representing the top of the Connect Four board.
     */
    private List<Block> topBlocks;

    /**
     * The direction the board is facing. The board faces the direction the player was facing when the board was
     * created (NOT WHEN IT WAS SPAWNED).
     */
    private BlockFace direction;

    /**
     * Constructor that initializes the ConnectFourBoard with player1's location.
     *
     * @param player1 the player whose location is used to create the board
     */
    public ConnectFourBoard(Player player1) {
        createBoard(player1.getLocation());
    }

    /**
     * Abstract method for spawning the board. Implementation should be provided by the subclass.
     */
    public abstract void spawnBoard();

    /**
     * Abstract method for destroying the board. Implementation should be provided by the subclass.
     */
    public abstract void destroyBoard();

    /**
     * Returns the list of bottom blocks.
     *
     * @return list of bottom blocks
     */
    public List<Block> getBottomBlocks() {
        return this.bottomBlocks;
    }

    /**
     * Returns the list of top blocks.
     *
     * @return list of top blocks
     */
    public List<Block> getTopBlocks() {
        return this.topBlocks;
    }

    /**
     * Returns the direction the board is facing.
     *
     * @return the direction the board is facing
     */
    public BlockFace getDirection() {
        return direction;
    }

    /**
     * Creates the Connect Four game's playable board (a 7x8 board) starting with the bottom of the board then generating the top.
     * Creates the region for where the board is based off of the top and bottom furthest corners from each other.
     * This method does not spawn the board or teleport the players, it only creates the board in memory.
     *
     * @param playerLoc the location where the board is going to spawn (should always be player1's location)
     */
    private void createBoard(Location playerLoc) {
        bottomBlocks = new ArrayList<>();
        topBlocks = new ArrayList<>();

        // Determine the direction the player is facing
        this.direction = DirectionUtil.getPlayerFacingDirection(playerLoc);

        Block initialBottomBlock = playerLoc.getBlock().getLocation().add(0, 1, 0).getBlock();
        bottomBlocks.add(initialBottomBlock);

        Location topLocation = initialBottomBlock.getLocation().add(0, 6, 0); // The height is hardcoded to 6
        Block initialTopBlock = topLocation.getBlock();
        topBlocks.add(initialTopBlock);

        // Determine the horizontal faces to use based on the player's facing direction
        BlockFace[] horizontalFaces = getHorizontalFaces(this.direction);
        if (horizontalFaces == null) {
            return; // Exit if direction is not recognized
        }

        // Generate blocks in the specified directions
        for (BlockFace face : horizontalFaces) {
            generateBlocks(initialBottomBlock, initialTopBlock, face);
        }
    }

    private BlockFace[] getHorizontalFaces(BlockFace direction) {
        switch(direction) {
            case NORTH:
            case SOUTH:
                return new BlockFace[] {BlockFace.EAST, BlockFace.WEST};
            case EAST:
            case WEST:
                return new BlockFace[] {BlockFace.NORTH, BlockFace.SOUTH};
            default:
                return null; // Exit if direction is not recognized
        }
    }

    private void generateBlocks(Block initialBottomBlock, Block initialTopBlock, BlockFace face) {
        for (int i = 0; i < 3; i++) {
            Block bottomBlock = (i == 0) ? initialBottomBlock.getRelative(face) : bottomBlocks.get(bottomBlocks.size() - 1).getRelative(face);
            Block topBlock = (i == 0) ? initialTopBlock.getRelative(face) : topBlocks.get(topBlocks.size() - 1).getRelative(face);

            bottomBlocks.add(bottomBlock);
            topBlocks.add(topBlock);
        }
    }
}
