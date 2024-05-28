package com.matthew.plugin.connectfour.modules.game;

import com.matthew.plugin.connectfour.apis.ServerModule;
import com.matthew.plugin.connectfour.game.ConnectFourBoardGame;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class GameModule implements ServerModule {

    private ArrayList<ConnectFourBoardGame> connectFourGames;

    /**
     * Construct the ConnectFourManager class (done in the onEnable method in the main class)
     * Purpose is to instantiate the arraylist that will hold any running connect four games
     *
     * Games are placed in the arraylist upon execution of the connect four command to start the game
     */
    public GameModule() {
        setup();
    }

    public boolean addGame(ConnectFourBoardGame newGame) {
        for(ConnectFourBoardGame game: connectFourGames) {
            if(game.equals(newGame)) {
                return false;
            }
        }
        connectFourGames.add(newGame);
        return true;
    }

    /**
     * Get the arraylist containing all the current connect four games that are running
     *
     * @return the connectFourBoards arraylist holding the running games
     */
    public ArrayList<ConnectFourBoardGame> getGames() {

        return connectFourGames;
    }

    /**
     * Get the current connect four game that a player is in
     *
     * @param player - player that is currently in one of the connect four games located in the connectFourBoards arraylist
     * @return the game the player is currently in, if any. If not then return null
     */
    public ConnectFourBoardGame getGame(Player player) {

        for(ConnectFourBoardGame game: connectFourGames) {
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
    public boolean isPlaying(Player player) {
        for(ConnectFourBoardGame game: connectFourGames) {
            if(game.getPlayers().containsValue(player) || game.getPlayers().containsKey(player)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setup() {
        connectFourGames = new ArrayList<>();
    }

    @Override
    public void teardown() {
        connectFourGames.clear();
    }
}
