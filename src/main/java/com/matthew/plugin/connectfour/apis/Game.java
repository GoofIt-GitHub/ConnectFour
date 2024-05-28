package com.matthew.plugin.connectfour.apis;

import org.bukkit.entity.Player;

import java.util.List;

public interface Game {

    List<Player> getPlayers();

    /**
     * Get the winner of the game
     *
     * @return the winner
     */
    Player getWinner();

    /**
     * Get whose turn it is to place down a blocks
     *
     * @return whoever turn it is
     */
    Player getTurn();

    /**
     * Send a message to the players in the same game
     *
     * @param message - message for the players which will display in chat
     */
    void sendMessage(String message);


    void startGame();

    /**
     * End the game by removing the board after 2 seconds so the player who lost can see how they lost > remove the players
     * from the players hashmap > remove the game from the ConnectFourManager's boards list
     */
    void stopGame();
}
