package com.matthew.plugin.connectfour.game;

import com.matthew.plugin.connectfour.ConnectFourPlugin;
import com.matthew.plugin.connectfour.game.mechanics.ConnectFourBoardMechanic;
import com.matthew.plugin.connectfour.modules.game.GameModule;
import com.matthew.plugin.connectfour.modules.manager.ServerModuleManager;
import com.matthew.plugin.connectfour.utils.Cuboid;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Objects;

public class ConnectFourBoardGame {

    private final ConnectFourBoardMechanic mechanic;

    private GameModule module;

    private final Player player1;

    private final Player player2;

    private Player turn;

    private Player winner;

    /**
     * Construct a new connect four game using two players. Set the turn to player1 > add both players to the games
     * players hashmap (player1 is key, player 2 is value) > add the new game to the ConnectFourManager classes arraylist >
     * call the private createBoard method.
     *
     * @param player1 - one of the players (the player who started the game & yellow blocks)
     * @param player2 - one of the players (red blocks)
     */
    public ConnectFourBoardGame(final Player player1, final Player player2) {
        final ServerModuleManager moduleManager = ServerModuleManager.getInstance();
        this.module = moduleManager.getRegisteredModule(GameModule.class);

        this.mechanic = new ConnectFourBoardMechanic(player1, player2);
        this.player1 = mechanic.getPlayers().get(0);
        this.player2 = mechanic.getPlayers().get(1);
        this.winner = null;
    }

    public List<Player> getPlayers() {
        return this.mechanic.getPlayers();
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
                    endGame();
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
                    endGame();
                }
            }
        }
    }

    public void startGame() {
        this.turn = player1;
        this.mechanic.spawnBoard();
        this.module.addGame(this);
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
                GameModule.getGames().remove(GameModule.getGame(player1));
                cancel();
            }

        }.runTaskTimer(ConnectFourPlugin.getInstance(), 40L, 0L);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ConnectFourBoardGame check = (ConnectFourBoardGame) o;
        return Objects.equals(player1, check.player1) && Objects.equals(player2, check.player2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player1, player2);
    }
}
