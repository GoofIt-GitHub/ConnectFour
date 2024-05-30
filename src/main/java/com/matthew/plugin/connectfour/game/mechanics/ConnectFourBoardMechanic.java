package com.matthew.plugin.connectfour.game.mechanics;

import com.matthew.plugin.connectfour.game.mechanics.framework.ConnectFourBoard;
import com.matthew.plugin.connectfour.utils.Cuboid;
import com.matthew.plugin.connectfour.utils.DirectionUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.List;

public class ConnectFourBoardMechanic extends ConnectFourBoard {

    /**
     * List of players currently in the provided connect four game.
     * Maintains insertion order; player1 is always at index 0.
     */
    private final List<Player> players;

    /**
     * Whose turn it is to make their move.
     */
    private Player turn;

    /**
     * The winner of the provided connect four game.
     */
    private Player winner;

    /**
     * The most recently placed block on the connect four board.
     */
    private Block recentBlock;

    /**
     * Region as a cuboid used to represent all blocks on the connect four board.
     */
    private Cuboid region;

    /**
     * Whether the game is currently running.
     */
    private boolean running;

    /**
     * Constructor for initializing the ConnectFourBoardMechanic with a list of players.
     *
     * @param players the list of players participating in the game
     */
    public ConnectFourBoardMechanic(final List<Player> players) {
        super(players.get(0)); // player1 being the location the board is going to spawn
        this.players = players;
        this.winner = null;
        this.running = false;
        this.recentBlock = null;
    }

    /**
     * Spawns the Connect Four board by setting the types of blocks in the bottom and top rows.
     */
    @Override
    public void spawnBoard() {
        // Spawn bottom blocks
        for (Block block : getBottomBlocks()) {
            block.setType(Material.QUARTZ_BLOCK);
        }

        // Spawn top blocks
        for (Block block : getTopBlocks()) {
            block.setType(Material.QUARTZ_BLOCK);
        }

        // Determine the spawn location in front of the board
        Location spawn = getBottomBlocks().get(3).getLocation().add(DirectionUtil.getOffsetForDirection(getDirection()));

        // Create board region
        Location bottomCorner = getBottomBlocks().get(0).getLocation();
        Location topCorner = getTopBlocks().get(getTopBlocks().size() - 1).getLocation();
        this.region = new Cuboid(bottomCorner, topCorner);

        // Teleport players to the spawn location
        players.get(0).teleport(spawn);
        players.get(1).teleport(spawn);
    }

    /**
     * Destroys the Connect Four board by setting the types of blocks to air and clearing lists.
     */
    @Override
    public void destroyBoard() {
        // Set block types to air for bottom blocks
        getBottomBlocks().forEach(block -> block.setType(Material.AIR));

        // Set block types to air for top blocks
        getTopBlocks().forEach(block -> block.setType(Material.AIR));

        // Clear lists
        getBottomBlocks().clear();
        getTopBlocks().clear();

        // Release any allocated resources associated with the region
        region.getBlocks().clear();
        region = null;
    }

    /**
     * Places a block on the board for the specified player if the game is running and valid conditions are met.
     *
     * @param player the player making the move
     * @param blockClicked the block clicked by the player
     */
    public void placeBlock(final Player player, final Block blockClicked) {
        // Check if the game is currently running
        if (!running) {
            return;
        }

        // Failsafe, double check that the player is indeed in the game. If not, then return
        if (!players.contains(player)) {
            return;
        }

        // Make sure that the block clicked was quartz and is part of the bottom row of the board. If not, then return
        if (blockClicked.getType() != Material.QUARTZ_BLOCK || !getBottomBlocks().contains(blockClicked)) {
            return;
        }

        Block currentBlock = blockClicked;

        // Height of the board is 6 and always will be 6, so it is safe to hard code the height here
        for (int i = 0; i < 6; i++) {
            Block blockAbove = currentBlock.getRelative(BlockFace.UP);
            if (blockAbove.getType() != Material.AIR) {
                currentBlock = blockAbove;
                continue;
            }

            // Place the correct wool color based on the player
            Material woolType = isPlayer1(player) ? Material.RED_WOOL : Material.YELLOW_WOOL;
            blockAbove.setType(woolType);
            recentBlock = blockAbove;

            // Update the turn to the next player
            turn = isPlayer1(player) ? players.get(1) : players.get(0);

            break;
        }
    }

    /**
     * Checks if the specified player has a winning sequence of 4 blocks.
     *
     * @param player the player to check for a winning sequence
     * @return true if the player has a winning sequence, otherwise false
     */
    public boolean hasWinningSequence(Player player) {
        Material type = isPlayer1(player) ? Material.RED_WOOL : Material.YELLOW_WOOL;
        BlockFace[] directions;
        BlockFace direction = getDirection();

        // Determine the primary directions to check based on the board orientation
        if (direction == BlockFace.EAST || direction == BlockFace.WEST) {
            directions = new BlockFace[]{BlockFace.NORTH, BlockFace.SOUTH};
        } else if (direction == BlockFace.NORTH || direction == BlockFace.SOUTH) {
            directions = new BlockFace[]{BlockFace.WEST, BlockFace.EAST};
        } else {
            return false; // Invalid direction
        }

        // Check in primary, vertical, and diagonal directions
        for (BlockFace dir : directions) {
            if (checkDirection(dir, type) || checkDirection(dir.getOppositeFace(), type)) {
                return true;
            }
            if (checkDirection(dir, BlockFace.UP, type) || checkDirection(dir, BlockFace.DOWN, type)
                    || checkDirection(dir.getOppositeFace(), BlockFace.UP, type) || checkDirection(dir.getOppositeFace(), BlockFace.DOWN, type)) {
                return true;
            }
        }
        return checkDirection(BlockFace.UP, type) || checkDirection(BlockFace.DOWN, type);
    }

    /**
     * Sends a message to the players in the same game.
     *
     * @param message the message for the players which will display in chat
     */
    public void sendMessage(final String message) {
        for (Player player : getPlayers()) {
            player.sendMessage(message);
        }
    }

    /**
     * Checks if the game is currently running.
     *
     * @return true if the game is running, otherwise false
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Gets the list of players in the game.
     *
     * @return the list of players
     */
    public List<Player> getPlayers() {
        return this.players;
    }

    /**
     * Gets the cuboid region representing all blocks on the Connect Four board.
     *
     * @return the cuboid region
     */
    public Cuboid getRegion() {
        return this.region;
    }

    /**
     * Gets the winner of the game.
     *
     * @return the winner player, or null if there is no winner yet
     */
    public Player getWinner() {
        return winner;
    }

    /**
     * Gets the player whose turn it is to make a move.
     *
     * @return the player whose turn it is
     */
    public Player getTurn() {
        return turn;
    }

    /**
     * Sets the player whose turn it is to make a move.
     *
     * @param turn the player whose turn it is
     */
    public void setTurn(final Player turn) {
        this.turn = turn;
    }

    /**
     * Checks if the specified player is player 1.
     *
     * @param player the player to check
     * @return true if the player is player 1, otherwise false
     */
    private boolean isPlayer1(final Player player) {
        return players.get(0).equals(player);
    }

    /**
     * Checks for a winning sequence in a specified direction for a given material type.
     *
     * @param direction the direction to check
     * @param type the material type to check for
     * @return true if a winning sequence is found, otherwise false
     */
    private boolean checkDirection(BlockFace direction, Material type) {
        int matchesInARow = 1;

        // Traverse in the given direction
        for (int i = 0; i < 3; i++) {
            recentBlock = recentBlock.getRelative(direction);
            if (recentBlock.getType() == type) {
                matchesInARow++;
                if (matchesInARow == 4) {
                    return true;
                }
            } else {
                break;
            }
        }

        return false;
    }

    /**
     * Checks for a winning sequence in a specified diagonal direction and slope for a given material type.
     *
     * @param direction the primary direction to check
     * @param slope the slope direction to check
     * @param type the material type to check for
     * @return true if a winning sequence is found, otherwise false
     */
    private boolean checkDirection(BlockFace direction, BlockFace slope, Material type) {
        int matchesInARow = 1;

        // Traverse in the given direction and slope
        for (int i = 0; i < 3; i++) {
            recentBlock = recentBlock.getRelative(direction).getRelative(slope);
            if (recentBlock.getType() == type) {
                matchesInARow++;
                if (matchesInARow == 4) {
                    return true;
                }
            } else {
                break;
            }
        }

        return false;
    }
}
