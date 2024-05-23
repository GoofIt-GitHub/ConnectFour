package com.matthew.plugin.connectfour.game;

import com.matthew.plugin.connectfour.ConnectFourPlugin;
import com.matthew.plugin.connectfour.modules.game.GameModule;
import com.matthew.plugin.connectfour.modules.regions.structure.Cuboid;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;

public class ConnectFourBoardGame {

    private final HashMap<Player, Player> players;
    private ArrayList<Block> bottomBlocks;

    private final Player player1;
    private final Player player2;
    private Player turn;
    private Cuboid region;
    private Player winner;
    private Block origin;

    /**
     * Construct a new connect four game using two players. Set the turn to player1 > add both players to the games
     * players hashmap (player1 is key, player 2 is value) > add the new game to the ConnectFourManager classes arraylist >
     * call the private createBoard method.
     *
     * @param player1 - one of the players (the player who started the game & yellow blocks)
     * @param player2 - one of the players (red blocks)
     */
    public ConnectFourBoardGame(Player player1, Player player2) {
        players = new HashMap<>();
        this.player1 = player1;
        this.player2 = player2;
        turn = player1;

        players.put(player1, player2);
        GameModule.getConnectFourBoards().add(this);
        createBoard(player1.getLocation());
    }

    /**
     * Create the ConnectFour game's playable board (a 7x8 board) starting with the bottom of the board then generating the top >
     * create the region for where the board is based off of the top and bottom furthest corners from eachother. Region
     * created using the Cuboid class > spawn both players in front of the board
     *
     * @param playerLoc - location of where the board is going to spawn (should always be player1's location)
     */

    private void createBoard(Location playerLoc) {

        bottomBlocks = new ArrayList<>();
        ArrayList<Block> topBlocks = new ArrayList<>();

        Block newBottomBlock;
        Block newTopBlock;

        //Create bottom of board
            if (playerLoc.getBlock().getType().equals(Material.AIR)) {

                Block bottomBlock = playerLoc.getBlock();
                bottomBlock.setType(Material.QUARTZ_BLOCK);
                bottomBlocks.add(bottomBlock);

                for (int i = 0; i < 6; i++) {
                    newBottomBlock = bottomBlock.getRelative(BlockFace.EAST);
                    newBottomBlock.setType(Material.QUARTZ_BLOCK);
                    bottomBlocks.add(newBottomBlock);
                    bottomBlock = newBottomBlock;
                }

                //Create top of board
                Location loc = playerLoc.getBlock().getRelative(BlockFace.UP).getLocation().add(0, 6, 0);
                Block topBlock = loc.getBlock();
                topBlock.setType(Material.QUARTZ_BLOCK);
                topBlocks.add(topBlock);

                for (int i = 0; i < 6; i++) {
                    newTopBlock = topBlock.getRelative(BlockFace.EAST);
                    newTopBlock.setType(Material.QUARTZ_BLOCK);
                    topBlocks.add(newTopBlock);
                    topBlock = newTopBlock;
                }

            }

        //Create board cuboid region
        Location bottomCorner = bottomBlocks.get(0).getLocation();
        Location topCorner = topBlocks.get(topBlocks.size() - 1).getLocation();
        region = new Cuboid(bottomCorner, topCorner);

        // Spawn the players in front of the board
        Location spawn = bottomBlocks.get(3).getLocation().add(0.0, 1.0, -3.0);
        player1.teleport(spawn);
        player2.teleport(spawn);

    }

    /**
     * Check if player1 has a line of 4 blocks (yellow stained clay) in a row horizontally. First check the east side of the last placed block
     * and increment the matchesInARow variable by one if the block type is the same. Then check the west side by starting back
     * at the last placed block and perform the same function.
     *
     * @return true if player1 has a line of 4 blocks in a horizontal line, else return false.
     */

    private boolean checkPlayer1Horizontal() {
        int matchesInARow = 1;
        Block original = origin;
        Block block = origin;

        //Check EAST side
        for(int i = 0; i<4; i++) {
            if(matchesInARow != 4) {
                if (block.getRelative(BlockFace.EAST).getData() == (byte)4) {
                    block = block.getRelative(BlockFace.EAST);
                    matchesInARow++;
                }
            } else {
                return true;
            }
        }
        //Check WEST side
        for(int i = 0; i<4; i++) {
            if(matchesInARow != 4) {
                if (origin.getRelative(BlockFace.WEST).getType().equals(origin.getType()) && origin.getRelative(BlockFace.WEST).getData() == (byte)4)  {
                    origin = origin.getRelative(BlockFace.WEST);
                    matchesInARow++;
                } else {
                    matchesInARow = 1;
                }
            } else {
                return true;
            }

        }
        origin = original;
        return false;
    }

    /**
     * Check if player2 has a line of 4 blocks (red stained clay) in a row horizontally. First check the east side of the last placed block
     * and increment the matchesInARow variable by one if the block type is the same. Then check the west side by starting back
     * at the last placed block and perform the same function.
     *
     * @return true if player2 has a line of 4 blocks in a horizontal line, else return false.
     */
    private boolean checkPlayer2Horizontal() {
        int matchesInARow = 1;
        Block original = origin;
        Block block = origin;

        //Check EAST side
        for(int i = 0; i<4; i++) {
            if(matchesInARow != 4) {
                if (block.getRelative(BlockFace.EAST).getData() == (byte)14) {
                    block = block.getRelative(BlockFace.EAST);
                    matchesInARow++;
                }
            } else {
                return true;
            }
        }
        //Check WEST side
        for(int i = 0; i<4; i++) {
            if(matchesInARow != 4) {
                if (origin.getRelative(BlockFace.WEST).getType().equals(origin.getType()) && origin.getRelative(BlockFace.WEST).getData() == (byte)14)  {
                    origin = origin.getRelative(BlockFace.WEST);
                    matchesInARow++;
                } else {
                    matchesInARow = 1;
                }
            } else {
                return true;
            }

        }

        origin = original;
        return false;
    }

    /**
     * Check if player1 has a line of 4 blocks (yellow stained clay) in a row vertically. First check above the last placed block
     * and increment the matchesInARow variable by one if the block type is the same. Then check below by starting back
     * at the last placed block and perform the same function.
     *
     * @return true if player1 has a line of 4 blocks in a vertical line, else return false.
     */
    private boolean checkPlayer1Vertical() {
        int matchesInARow = 1;
        Block original = origin;
        Block block = origin;

        //Check EAST side
        for(int i = 0; i<4; i++) {
            if(matchesInARow != 4) {
                if (block.getRelative(BlockFace.UP).getData() == (byte)4) {
                    block = block.getRelative(BlockFace.UP);
                    matchesInARow++;
                }
            } else {
                return true;
            }
        }
        //Check WEST side
        for(int i = 0; i<4; i++) {
            if(matchesInARow != 4) {
                if (origin.getRelative(BlockFace.DOWN).getType().equals(origin.getType()) && origin.getRelative(BlockFace.DOWN).getData() == (byte)4)  {
                    origin = origin.getRelative(BlockFace.DOWN);
                    matchesInARow++;
                } else {
                    matchesInARow = 1;
                }
            } else {
                return true;
            }
        }
        origin = original;
        return false;
    }

    /**
     * Check if player1 has a line of 4 blocks (red stained clay) in a row vertically. First check above the last placed block
     * and increment the matchesInARow variable by one if the block type is the same. Then check below by starting back
     * at the last placed block and perform the same function.
     *
     * @return true if player2 has a line of 4 blocks in a vertical line, else return false.
     */
    private boolean checkPlayer2Vertical() {
        int matchesInARow = 1;
        Block block = origin;
        Block original = origin;

        //Check EAST side
        for(int i = 0; i<4; i++) {
            if(matchesInARow != 4) {
                if (block.getRelative(BlockFace.UP).getData() == (byte)14) {
                    block = block.getRelative(BlockFace.UP);
                    matchesInARow++;
                }
            } else {
                return true;
            }
        }
        //Check WEST side
        for(int i = 0; i<4; i++) {
            if(matchesInARow != 4) {
                if (origin.getRelative(BlockFace.DOWN).getType().equals(origin.getType()) && origin.getRelative(BlockFace.DOWN).getData() == (byte)14)  {
                    origin = origin.getRelative(BlockFace.DOWN);
                    matchesInARow++;
                } else {
                    matchesInARow = 1;
                }
            } else {
                return true;
            }
        }

        origin = original;
        return false;
    }

    /**
     * Check if player1 has a line of 4 blocks (yellow stained clay) in a row diagonally. First check based on a positive slope
     * from where the last placed block is located and increment the matchesInARow variable by one if the block type is the same.
     * Then check based on a negative slope by starting back at the last placed block and perform the same function.
     *
     * @return true if player1 has a line of 4 blocks in a diagonal line, else return false.
     */
    private boolean checkPlayer1Diagonal() {
        int matchesInARow = 1;
        Block original = origin;
        Block block = origin;

        //Check positive slope
        //Check EAST side
        for(int i = 0; i<4; i++) {
            if(matchesInARow != 4) {
                if (block.getRelative(BlockFace.EAST).getRelative(BlockFace.DOWN).getData() == (byte) 4) {
                    block = block.getRelative(BlockFace.EAST).getRelative(BlockFace.DOWN);
                    matchesInARow++;
                }
            } else {
                return true;
            }
        }
        //Check WEST side
        for(int i = 0; i<4; i++) {
            if(matchesInARow != 4) {
                if (origin.getRelative(BlockFace.WEST).getRelative(BlockFace.UP).getData() == (byte) 4)  {
                    origin = origin.getRelative(BlockFace.WEST).getRelative(BlockFace.UP);
                    matchesInARow++;
                } else {
                    matchesInARow = 1;
                }
            } else {
                return true;
            }
        }

        origin = original;

        //Check negative slope
        //Check EAST side
        for(int i = 0; i<4; i++) {
            if(matchesInARow != 4) {
                if (block.getRelative(BlockFace.EAST).getRelative(BlockFace.UP).getData() == (byte) 4) {
                    block = block.getRelative(BlockFace.EAST).getRelative(BlockFace.UP);
                    matchesInARow++;
                }
            } else {
                return true;
            }
        }
        //Check WEST side
        for(int i = 0; i<4; i++) {
            if(matchesInARow != 4) {
                if (origin.getRelative(BlockFace.WEST).getRelative(BlockFace.DOWN).getData() == (byte) 4)  {
                    origin = origin.getRelative(BlockFace.WEST).getRelative(BlockFace.DOWN);
                    matchesInARow++;
                } else {
                    matchesInARow = 1;
                }
            } else {
                return true;
            }
        }

        origin = original;
        return false;
    }

    /**
     * Check if player2 has a line of 4 blocks (red stained clay) in a row diagonally. First check based on a positive slope
     * from where the last placed block is located and increment the matchesInARow variable by one if the block type is the same.
     * Then check based on a negative slope by starting back at the last placed block and perform the same function.
     *
     * @return true if player2 has a line of 4 blocks in a diagonal line, else return false.
     */
    private boolean checkPlayer2Diagonal() {
        int matchesInARow = 1;
        Block original = origin;
        Block block = origin;

        //Check EAST side
        for(int i = 0; i<4; i++) {
            if(matchesInARow != 4) {
                if (block.getRelative(BlockFace.EAST).getRelative(BlockFace.DOWN).getData() == (byte) 14) {
                    block = block.getRelative(BlockFace.EAST).getRelative(BlockFace.DOWN);
                    matchesInARow++;
                }
            } else {
                return true;
            }
        }
        //Check WEST side
        for(int i = 0; i<4; i++) {
            if(matchesInARow != 4) {
                if (origin.getRelative(BlockFace.WEST).getRelative(BlockFace.UP).getData() == (byte) 14)  {
                    origin = origin.getRelative(BlockFace.WEST).getRelative(BlockFace.UP);
                    matchesInARow++;
                } else {
                    matchesInARow = 1;
                }
            } else {
                return true;
            }
        }

        origin = original;

        //Check negative slope
        //Check EAST side
        for(int i = 0; i<4; i++) {
            if(matchesInARow != 4) {
                if (block.getRelative(BlockFace.EAST).getRelative(BlockFace.UP).getData() == (byte) 14) {
                    block = block.getRelative(BlockFace.EAST).getRelative(BlockFace.UP);
                    matchesInARow++;
                }
            } else {
                return true;
            }
        }
        //Check WEST side
        for(int i = 0; i<4; i++) {
            if(matchesInARow != 4) {
                if (origin.getRelative(BlockFace.WEST).getRelative(BlockFace.DOWN).getData() == (byte) 14)  {
                    origin = origin.getRelative(BlockFace.WEST).getRelative(BlockFace.DOWN);
                    matchesInARow++;
                } else {
                    matchesInARow = 1;
                }
            } else {
                return true;
            }
        }

        origin = original;
        return false;
    }


    /**
     * Get the players' hashmap holding player 1 and player 2
     *
     * @return the entire players hashmap
     */
    public HashMap<Player, Player> getPlayers() {
        return players;
    }

    /**
     * Get a list of all of the blocks at the bottom of the board
     *
     * @return the entire list of locks at the bottom of the board
     */
    public ArrayList<Block> getBottomBlocks() {
        return bottomBlocks;
    }

    /**
     * Get player1
     *
     * @return player1
     */
    public Player getPlayer1() {
        return player1;
    }

    /**
     * Get player2
     *
     * @return player2
     */
    public Player getPlayer2() {
        return player2;
    }

    /**
     * Get the winner of the game
     *
     * @return the winner
     */
    public Player getWinner() {
        return winner;
    }

    /**
     * Get whose turn it is to place down a blocks
     *
     * @return whoever turn it is
     */
    public Player getTurn() {
        return turn;
    }

    /**
     * Send a message to the players in the same game
     *
     * @param message - message for the players which will display in chat
     */
    public void sendMessage(String message) {
        player1.sendMessage(message);
        player2.sendMessage(message);
    }

    /**
     * Check if player1 or player2 has a line of 4 blocks in a row horizontally, vertically, or diagonally.
     *
     * @return true if one of the players has a line of 4 blocks in a row, else return false
     */
    public boolean checkForWinner() {
        if(!turn.equals(player1)) {
            if(checkPlayer1Horizontal() || checkPlayer1Vertical() || checkPlayer1Diagonal()) {
                winner = player1;
                return true;
            }
        } else {
            if(checkPlayer2Horizontal() || checkPlayer2Vertical() || checkPlayer2Diagonal()) {
                winner = player2;
                return true;
            }
        }
        return false;
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
                            newBlock.setType(Material.STAINED_CLAY);
                            newBlock.setData((byte) 4);
                            origin = newBlock;
                            turn = player2;
                            player2.sendMessage(ChatColor.BLUE + ">> " + ChatColor.WHITE + ChatColor.BOLD + "Your turn");

                        } else if(players.containsValue(player)) {
                            Block newBlock = checked.getRelative(BlockFace.UP);
                            newBlock.setType(Material.STAINED_CLAY);
                            newBlock.setData((byte) 14);
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
            if(checkForWinner()) {
                    sendMessage(ChatColor.BLUE + ">> " + ChatColor.YELLOW + ChatColor.BOLD + winner.getName() + ChatColor.GRAY + " has won");
                    endGame();
            } else {
                //Check if there is room on the board for another play
                int availablePlays = 0;
                for (Block b : region.getBlocks()) {
                    if (b.getType().equals(Material.AIR)) {
                        availablePlays++;
                    }
                }
                if (availablePlays == 0) {
                    sendMessage(ChatColor.BLUE + ">> " + ChatColor.YELLOW + ChatColor.BOLD + "Tie!" + ChatColor.GRAY + " Nobody wins");
                    endGame();
                }
            }
        }
    }

    /**
     * End the game by removing the board after 2 seconds so the player who lost can see how they lost > remove the players
     * from the players hashmap > remove the game from the ConnectFourManager's boards list
     */
    public void endGame() {
        //Remove board
        new BukkitRunnable() {

            @Override
            public void run() {
                for(Block block: region.getBlocks()) {
                    block.setType(Material.AIR);
                }
                players.remove(player1);
                GameModule.getConnectFourBoards().remove(GameModule.getConnectFourBoard(player1));
                cancel();
            }

        }.runTaskTimer(ConnectFourPlugin.getInstance(), 40L, 0L);

    }
}
