package com.matthew.plugin.connectfour.game.mechanics.framework;

import com.matthew.plugin.connectfour.utils.DirectionUtil;
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
        // Ensure the initial block location is air before creating the board
        if (!playerLoc.getBlock().getType().equals(Material.AIR)) {
            return;
        }

        bottomBlocks = new ArrayList<>();
        topBlocks = new ArrayList<>();

        // Determine the direction the player is facing
        this.direction = DirectionUtil.getPlayerFacingDirection(playerLoc);

        // Create the initial bottom block
        Block initialBottomBlock = playerLoc.getBlock();
        bottomBlocks.add(initialBottomBlock);

        // Create the initial top block
        Location topLocation = initialBottomBlock.getLocation().add(0, 6, 0); // The height is hardcoded to 6
        Block initialTopBlock = topLocation.getBlock();
        topBlocks.add(initialTopBlock);

        // Create the remaining bottom blocks by moving in the direction the player is facing
        bottomBlocks.addAll(IntStream.range(0, 6)
                .mapToObj(i -> bottomBlocks.get(i).getRelative(direction))
                .collect(Collectors.toList())
        );

        // Create the remaining top blocks by moving in the direction the player is facing
        topBlocks.addAll(IntStream.range(0, 6)
                .mapToObj(i -> topBlocks.get(i).getRelative(direction))
                .collect(Collectors.toList())
        );
    }
}
