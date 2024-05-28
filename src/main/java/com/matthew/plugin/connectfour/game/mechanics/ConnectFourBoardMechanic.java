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
     Best to make players a list to maintain insertion order, therefore player1 is always index 0
     */
    private final List<Player> players;

    private Player turn;

    private Player winner;

    private Cuboid region;

    public ConnectFourBoardMechanic(final List<Player> players) {
        super(players.get(0)); //player1 being the location the board is going to spawn
        this.players = players;
        this.winner = null;
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

    /**
     * Place a block in the row of the bottom quartz block that the player has clicked on if there is still room in the row >
     * set the turn to the player who did not click the block and is in the game > but before the next player goes, check and
     * see if there is a winner using the checkForWinner private method. If there is a winner call the endGame private method, if not
     * check and make sure the board isn't full, if it is full then end the game, if not then continue on with the game.
     *
     * @param player - the player who is attempting to place a block on the ConnectFour board
     * @param blockClicked - the bottom quartz block that the player has clicked on
     */
    public void placeBlock(Player player, Block blockClicked) {

        if(blockClicked.getType().equals(Material.QUARTZ_BLOCK)) {
            Block checked = blockClicked;
            boolean finished = false;

            for(int i = 0; i<6; i++) {
                if(!finished) {
                    if(checked.getRelative(BlockFace.UP).getType().equals(Material.AIR)) {
                        if(players.containsKey(player)) {
                            Block newBlock = checked.getRelative(BlockFace.UP);
                            newBlock.setType(Material.RED_WOOL);
                            origin = newBlock;
                            turn = player2;
                            player2.sendMessage(ChatColor.BLUE + ">> " + ChatColor.WHITE + ChatColor.BOLD + "Your turn");

                        } else if(players.containsValue(player)) {
                            Block newBlock = checked.getRelative(BlockFace.UP);
                            newBlock.setType(Material.YELLOW_WOOL);
                            origin = newBlock;
                            turn = player1;
                            player1.sendMessage(ChatColor.BLUE + ">> " + ChatColor.WHITE + ChatColor.BOLD + "Your turn");

                        }
                        finished = true;
                    } else {
                        checked = checked.getRelative(BlockFace.UP);

                    }
                }
            }
            //Check if there is a winner
            if(mechanic.checkWinner()) {
                sendMessage(ChatColor.BLUE + ">> " + ChatColor.YELLOW + ChatColor.BOLD + winner.getName() + ChatColor.GRAY + " has won");
                stopGame();
            } else {
                //Check if there is room on the board for another play
                int availablePlays = 0;
                for (Block b : mechanic.getRegion().getBlocks()) {
                    if (b.getType().equals(Material.AIR)) {
                        availablePlays++;
                    }
                }
                if (availablePlays == 0) {
                    sendMessage(ChatColor.BLUE + ">> " + ChatColor.YELLOW + ChatColor.BOLD + "Tie!" + ChatColor.GRAY + " Nobody wins");
                    stopGame();
                }
            }
        }
    }

    /**
     * Send a message to the players in the same game
     *
     * @param message - message for the players which will display in chat
     */
    public void sendMessage(String message) {
        for (Player player : getPlayers()) {
            player.sendMessage(message);
        }
    }

    private boolean checkWinner() {
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

    public void setTurn(Player turn) {
        this.turn = turn;
    }
}
