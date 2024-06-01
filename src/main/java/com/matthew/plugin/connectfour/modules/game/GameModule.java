package com.matthew.plugin.connectfour.modules.game;

import com.matthew.plugin.connectfour.ConnectFourPlugin;
import com.matthew.plugin.connectfour.apis.ServerModule;
import com.matthew.plugin.connectfour.game.ConnectFourBoardGame;
import com.matthew.plugin.connectfour.game.listeners.ConnectFourListener;
import com.matthew.plugin.connectfour.modules.manager.commands.ConnectFourCommand;
import com.matthew.plugin.connectfour.utils.CommandUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Iterator;

public class GameModule implements ServerModule {

    private final JavaPlugin plugin;

    private ArrayList<ConnectFourBoardGame> connectFourGames;

    private Command command;

    private Listener listener;

    /**
     * Construct the ConnectFourManager class (done in the onEnable method in the main class)
     * Purpose is to instantiate the arraylist that will hold any running connect four games
     *
     * Games are placed in the arraylist upon execution of the connect four command to start the game
     */
    public GameModule(JavaPlugin plugin) {
        this.plugin = plugin;
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

    public boolean removeGame(ConnectFourBoardGame existingGame) {
        Iterator<ConnectFourBoardGame> iterator = connectFourGames.iterator();
        while (iterator.hasNext()) {
            ConnectFourBoardGame game = iterator.next();
            if (game.equals(existingGame)) {
                iterator.remove();
                return true;
            }
        }
        return false;
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
            if(game.getPlayers().contains(player)) {
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
            if(game.getPlayers().contains(player)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setup() {
        connectFourGames = new ArrayList<>();
        this.command = new ConnectFourCommand();
        CommandUtil.register(this.command);

        this.listener = new ConnectFourListener();
        Bukkit.getPluginManager().registerEvents(listener, this.plugin);
    }

    @Override
    public void teardown() {
        connectFourGames.clear();
    }
}
