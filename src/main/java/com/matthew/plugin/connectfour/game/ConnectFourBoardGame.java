package com.matthew.plugin.connectfour.game;

import com.matthew.plugin.connectfour.ConnectFourPlugin;
import com.matthew.plugin.connectfour.apis.Game;
import com.matthew.plugin.connectfour.game.mechanics.ConnectFourBoardMechanic;
import com.matthew.plugin.connectfour.modules.game.GameModule;
import com.matthew.plugin.connectfour.modules.manager.ServerModuleManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ConnectFourBoardGame implements Game {

    private final ConnectFourBoardMechanic mechanic;

    private final GameModule module;

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

        this.mechanic = new ConnectFourBoardMechanic(Arrays.asList(player1, player2));
    }

    @Override
    public void placeBlock(Player player, Block blockClicked) {
        this.mechanic.placeBlock(player, blockClicked);
    }

    @Override
    public boolean hasWinningSequence(Player player) {
        return this.mechanic.hasWinningSequence(player);
    }

    /**
     * Send a message to the players in the same game
     *
     * @param message - message for the players which will display in chat
     */
    @Override
    public void sendMessage(String message) {
        this.mechanic.sendMessage(message);
    }

    @Override
    public Player getPlayer1() {
        return this.mechanic.getPlayers().get(0);
    }

    @Override
    public Player getPlayer2() {
        return this.mechanic.getPlayers().get(1);
    }

    @Override
    public List<Player> getPlayers() {
        return this.mechanic.getPlayers();
    }

    /**
     * Get the winner of the game
     *
     * @return the winner
     */
    @Override
    public Player getWinner() {
        return this.mechanic.getWinner();
    }

    /**
     * Get whose turn it is to place down a blocks
     *
     * @return whoever turn it is
     */
    @Override
    public Player getTurn() {
        return this.mechanic.getTurn();
    }

    @Override
    public void startGame() {
        this.mechanic.setTurn(getPlayers().get(0)); //Player 1 starts
        this.mechanic.spawnBoard();
        this.module.addGame(this);
    }

    @Override
    public void stopGame() {
        mechanic.destroyBoard();
        module.removeGame(this);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ConnectFourBoardGame check = (ConnectFourBoardGame) o;
        return getPlayers().equals(check.getPlayers());
    }

    @Override
    public int hashCode() {
        return getPlayers().hashCode();
    }
}
