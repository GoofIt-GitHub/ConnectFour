package com.matthew.plugin.connectfour.modules.manager.commands;

import com.matthew.plugin.connectfour.game.ConnectFourBoardGame;
import com.matthew.plugin.connectfour.modules.game.GameModule;
import com.matthew.plugin.connectfour.modules.manager.ServerModuleManager;
import org.bukkit.Bukkit;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ConnectFourCommand implements CommandExecutor {

    private final GameModule module;

    public ConnectFourCommand() {
        final ServerModuleManager manager = ServerModuleManager.getInstance();
        this.module = manager.getRegisteredModule(GameModule.class);

    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        Player player = sender instanceof Player ? (Player) sender : null;

        if(player == null) {
            return true;
        }

        if(args.length != 1) {
            player.sendMessage(ChatColor.RED + "Invalid usage. /connectfour <player>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if(target == null) {
            player.sendMessage(ChatColor.RED + "Player not found.");
            return true;
        }

        if(target.getName().equals(player.getName())) {
            player.sendMessage(ChatColor.RED + "Target must not be self.");
            return true;
        }

        if(module.getGame(target) != null) {
            player.sendMessage(ChatColor.RED + target.getName() + " is already in a game!");
            return true;
        }

        if(module.getGame(player) != null) {
            player.sendMessage(ChatColor.RED + "You must finish the game you are in before starting another.");
        }

        //TODO: Implement a game invite system so that target can deny or accept game invite
        ConnectFourBoardGame game = new ConnectFourBoardGame(player, target);
        game.startGame();

        return true;
    }
}
