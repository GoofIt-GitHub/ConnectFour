package com.matthew.plugin.connectfour.gamefunction.events;

import com.matthew.plugin.connectfour.apis.ConnectFourBoardGame;
import com.matthew.plugin.connectfour.apis.ConnectFourManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    /**
     * Sudo summary of event
     *
     * If the player that quit is currently in a connect four game then call the endGame method in ConnectFourBoard game
     * and send a message to the other player that the game has been stopped.
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerQuit(PlayerQuitEvent e) {

        Player player = e.getPlayer();
        if(ConnectFourManager.getConnectFourBoard(player) != null) {
            ConnectFourBoardGame game = ConnectFourManager.getConnectFourBoard(player);

            if(player.equals(game.getPlayer1())) {
                game.getPlayer2().sendMessage(ChatColor.BLUE + ">> " + ChatColor.GRAY + ChatColor.BOLD + "Game stopped");
            } else {
                game.getPlayer1().sendMessage(ChatColor.BLUE + ">> " + ChatColor.GRAY + ChatColor.BOLD + "Game stopped");
            }
            game.endGame();
        }
    }
}
