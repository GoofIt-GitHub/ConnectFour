package com.matthew.plugin.connectfour.apis;

import org.bukkit.entity.Player;

import java.util.ArrayList;

public class ConnectFourManager {

    private static ArrayList<ConnectFourBoardGame> connectFourBoards;

    /**
     * Construct the ConnectFourManager class (done in the onEnable method in the main class)
     * Purpose is to instantiate the arraylist that will hold any running connect four games
     *
     * Games are placed in the arraylist upon execution of the connect four command to start the game
     */
    public ConnectFourManager() {
        connectFourBoards = new ArrayList<>();

    }

    /**
     * Get the arraylist containing all the current connect four games that are running
     *
     * @return the connectFourBoards arraylist holding the running games
     */
    public static ArrayList<ConnectFourBoardGame> getConnectFourBoards() {

        return connectFourBoards;
    }

    /**
     * Get the current connect four game that a player is in
     *
     * @param player - player that is currently in one of the connect four games located in the connectFourBoards arraylist
     * @return the game the player is currently in, if any. If not then return null
     */
    public static ConnectFourBoardGame getConnectFourBoard(Player player) {

        for(ConnectFourBoardGame game: connectFourBoards) {
            if(game.getPlayers().containsKey(player) || game.getPlayers().containsValue(player)) {
                return game;
            }
        }
        return null;
    }

    /**
     * Check if a player is currently playing a connect four game
     *
     * @param player - player that is being checked for if they are currently in a connect four game
     * @return true if the player is currently in a connect for game, else if otherwise
     */
    public static boolean isPlaying(Player player) {
        for(ConnectFourBoardGame game: connectFourBoards) {
            if(game.getPlayers().containsValue(player) || game.getPlayers().containsKey(player)) {
                return true;
            }
        }
        return false;
    }
}
