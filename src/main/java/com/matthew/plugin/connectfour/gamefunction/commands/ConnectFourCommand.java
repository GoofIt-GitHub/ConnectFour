package com.matthew.plugin.connectfour.gamefunction.commands;

import com.matthew.plugin.connectfour.apis.ConnectFourBoardGame;
import com.matthew.plugin.connectfour.apis.ConnectFourManager;
import org.bukkit.Bukkit;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class ConnectFourCommand implements CommandExecutor {

    /**
     * Command - /connectfour (player2)
     *
     * If all conditions are met a game of connect four will commence by calling the ConnectFourBoardGame class constructor
     * and the game will be placed in the static connectFourBoards arraylist in the ConnectFourManager class
     */

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length == 1) {
                Player target = Bukkit.getPlayerExact(args[0]);
                if(!target.equals(player)) {
                    if (ConnectFourManager.getConnectFourBoard(player) == null) {
                        if (target.isOnline()) {
                            if (!ConnectFourManager.isPlaying(target)) {
                                ConnectFourManager.getConnectFourBoards().add(new ConnectFourBoardGame(player, target));
                            } else {
                                player.sendMessage(ChatColor.BLUE + ">> " + ChatColor.GOLD + target.getName() + ChatColor.GRAY + " is currently in a game");
                            }
                        } else {
                            player.sendMessage(ChatColor.BLUE + ">> " + ChatColor.GRAY + "Player not found");
                        }
                    } else {
                        player.sendMessage(ChatColor.BLUE + ">> " + ChatColor.GRAY + "You are currently in a game");
                    }
                } else {
                    player.sendMessage(ChatColor.BLUE + ">> " + ChatColor.GRAY + "You cannot start a game with yourself");
                }
            }
        }

        return false;
    }

}
