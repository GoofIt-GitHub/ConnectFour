package com.matthew.plugin.connectfour.game.mechanics;

import com.matthew.plugin.connectfour.game.mechanics.framework.ConnectFourBoard;
import com.matthew.plugin.connectfour.utils.Cuboid;
import com.matthew.plugin.connectfour.utils.DirectionUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.List;


public class ConnectFourBoardMechanic extends ConnectFourBoard {

    /*
     Best to make players a list to maintain insertion order; therefore, player1 is always index 0
     */
    private final List<Player> players;

    private Player turn;

    private Player winner;

    private Cuboid region;

    /*
     Whether the game is currently running
     */
    private boolean running;

    public ConnectFourBoardMechanic(final List<Player> players) {
        super(players.get(0)); //player1 being the location the board is going to spawn
        this.players = players;
        this.winner = null;
        this.running = false;
    }

    @Override
    public void spawnBoard() {
        // Set block types for the bottom blocks
        for (Block block : getBottomBlocks()) {
            block.setType(Material.QUARTZ_BLOCK);
        }

        // Set block types for the top blocks
        for (Block block : getTopBlocks()) {
            block.setType(Material.QUARTZ_BLOCK);
        }

        // Determine the spawn location in front of the board
        Location spawn = getBottomBlocks().get(3).getLocation().add(DirectionUtil.getOffsetForDirection(getDirection()));

        // Create board cuboid region
        Location bottomCorner = getBottomBlocks().get(0).getLocation();
        Location topCorner = getTopBlocks().get(getTopBlocks().size() - 1).getLocation();
        this.region = new Cuboid(bottomCorner, topCorner);

        // Teleport players to the spawn location
        players.get(0).teleport(spawn);
        players.get(1).teleport(spawn);
    }

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

    public void placeBlock(final Player player, final Block blockClicked) {

        /*
         Check if the game is currently running
         */
        if(!running) {
            return;
        }

        /*
         Failsafe, double check that the player is indeed in the game. If not, then return
         */
        if (!players.contains(player)) {
            return;
        }

        /*
         Make sure that the block clicked was quartz. If it wasn't, the conditional will short circuit so that we don't
         iterate through the bottom blocks list for no reason. But if the block is quartz, then double check that it is
         a quartz block that is a part of the bottom row of the connect four board. If not, then return
         */
        if (blockClicked.getType() != Material.QUARTZ_BLOCK || !getBottomBlocks().contains(blockClicked)) {
            return;
        }

        Block currentBlock = blockClicked;

        //Height of the board is 6 and always will be 6, so it is safe to hard code the height here
        for (int i = 0; i < 6; i++) {
            Block blockAbove = currentBlock.getRelative(BlockFace.UP);
            if (blockAbove.getType() != Material.AIR) {
                currentBlock = blockAbove;
                continue;
            }

            // Place the correct wool color based on the player
            Material woolType = isPlayer1(player) ? Material.RED_WOOL : Material.YELLOW_WOOL;
            blockAbove.setType(woolType);

            // Update the turn to the next player
            turn = isPlayer1(player) ? players.get(1) : players.get(0);

            break;
        }
    }

    public boolean hasWinningSequence() {
        for (Block block : getBottomBlocks()) {

            // Check in directions based on the orientation of the board
            switch (getDirection()) {
                case EAST:
                    if (checkDirection(block, 0, 1, 0) ||     // Right
                            checkDirection(block, 1, 0, 0) ||     // Down
                            checkDirection(block, 1, 1, 0) ||     // Diagonal (down-right)
                            checkDirection(block, -1, 1, 0)) {    // Diagonal (up-right)
                        return true; // Found a winning sequence
                    }
                    break;
                case WEST:
                    if (checkDirection(block, 0, -1, 0) ||    // Left
                            checkDirection(block, 1, 0, 0) ||     // Down
                            checkDirection(block, 1, -1, 0) ||    // Diagonal (down-left)
                            checkDirection(block, -1, -1, 0)) {   // Diagonal (up-left)
                        return true; // Found a winning sequence
                    }
                    break;
                case SOUTH:
                    if (checkDirection(block, 1, 0, 0) ||     // Down
                            checkDirection(block, 0, 1, 0) ||     // Right
                            checkDirection(block, -1, 1, 0) ||    // Diagonal (up-right)
                            checkDirection(block, -1, -1, 0)) {   // Diagonal (up-left)
                        return true; // Found a winning sequence
                    }
                    break;
                case NORTH:
                default:
                    if (checkDirection(block, -1, 0, 0) ||    // Up
                            checkDirection(block, 0, -1, 0) ||    // Left
                            checkDirection(block, 1, -1, 0) ||    // Diagonal (down-left)
                            checkDirection(block, 1, 1, 0)) {     // Diagonal (down-right)
                        return true; // Found a winning sequence
                    }
                    break;
            }
        }
        return false; // No winning sequence found
    }


    /**
     * Send a message to the players in the same game
     *
     * @param message - message for the players which will display in chat
     */
    public void sendMessage(final String message) {
        for (Player player : getPlayers()) {
            player.sendMessage(message);
        }
    }

    public List<Player> getPlayers() {
        return this.players;
    }

    public Cuboid getRegion() {
        return this.region;
    }

    public Player getWinner() {
        return winner;
    }

    public Player getTurn() {
        return turn;
    }

    public void setTurn(final Player turn) {
        this.turn = turn;
    }

    public boolean isRunning() {
        return running;
    }

    private boolean checkDirection(Block startBlock, int dx, int dy, int dz) {
        Material type = startBlock.getType();
        Location startLocation = startBlock.getLocation();

        for (int i = 0; i < 4; i++) {
            boolean matched = true;
            for (int j = 1; j < 4; j++) {
                Location currentLocation = startLocation.clone().add(dx * j, dy * j, dz * j);
                if (currentLocation.getBlock().getType() != type) {
                    matched = false;
                    break;
                }
            }
            if (matched) {
                return true;
            }
            startLocation.add(-dx, -dy, -dz); // Move back to the starting position to check in the opposite direction
        }
        return false;
    }

    private boolean isPlayer1(final Player player) {
        return players.get(0).equals(player);
    }
}
