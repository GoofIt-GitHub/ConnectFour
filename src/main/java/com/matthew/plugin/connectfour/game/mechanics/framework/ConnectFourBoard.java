package com.matthew.plugin.connectfour.game.mechanics.framework;

import com.matthew.plugin.connectfour.utils.Cuboid;
import com.matthew.plugin.connectfour.utils.DirectionUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class ConnectFourBoard {

    private List<Block> bottomBlocks;

    private List<Block> topBlocks;

    /*
    The direction the board is facing. The board faces the direction the player was facing when the board was
    created. (NOT WHEN IT WAS SPAWNED)
     */
    private BlockFace direction;


    public ConnectFourBoard(Player player1) {
        createBoard(player1.getLocation());
    }

    /*
     Mechanic will decide when and how to spawn the board
     */
    public abstract void spawnBoard();

    /*
     Mechanic will implement how the board is to be spawned
     */
    public abstract void destroyBoard();

    public List<Block> getBottomBlocks() {
        return this.bottomBlocks;
    }

    public List<Block> getTopBlocks() {
        return this.topBlocks;
    }

    /**
     * Create the ConnectFour game's playable board (a 7x8 board) starting with the bottom of the board then generating the top >
     * create the region for where the board is based off of the top and bottom furthest corners from eachother.
     * Important note, this will not spawn the board or teleport the players, this will only create the board
     * in memory
     *
     * @param playerLoc - location of where the board is going to spawn (should always be player1's location)
     */
    private void createBoard(Location playerLoc) {
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
        Location topLocation = initialBottomBlock.getLocation().add(0, 6, 0); //TODO: Might be wrong
        Block initialTopBlock = topLocation.getBlock();
        topBlocks.add(initialTopBlock);

        // Create the remaining bottom and top blocks
        bottomBlocks.addAll(IntStream.range(0, 6)
                .mapToObj(i -> bottomBlocks.get(i).getRelative(direction))
                .collect(Collectors.toList())
        );

        topBlocks.addAll(IntStream.range(0, 6)
                .mapToObj(i -> topBlocks.get(i).getRelative(direction))
                .collect(Collectors.toList())
        );
    }

    public BlockFace getDirection() {
        return direction;
    }

}
