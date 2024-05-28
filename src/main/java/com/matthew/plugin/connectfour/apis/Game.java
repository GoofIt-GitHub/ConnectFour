package com.matthew.plugin.connectfour.apis;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.List;

public interface Game {

    /**
     * Start game and allocate necessary resources
     */
    void startGame();

    /**
     * End game and release any additionally allocated resources
     */
    void stopGame();

    /**
     * TODO: Comment
     *
     * @param player - Bukkit Player who is currently in the game and is attempting to place a block
     * @param blockClicked - Bottom row block that was clicked to designate which column the block is to attempt to be placed
     */
    void placeBlock(Player player, Block blockClicked);

    /**
     * Send a message to the players in the same game
     *
     * @param message - message for the players which will display in chat
     */
    void sendMessage(String message);

    /**
     * Get all players currently playing the game. Assuming players is not null, there will always be 2
     *
     * @return list of players in the game
     */
    List<Player> getPlayers();

    /**
     * Get the winner of the game
     *
     * @return a Bukkit Player representing the winner of the game
     */
    Player getWinner();

    /**
     * Get whose turn it is to place down a block
     *
     * @return a Bukkit Player representing the player who is allowed to make their move
     */
    Player getTurn();
}
