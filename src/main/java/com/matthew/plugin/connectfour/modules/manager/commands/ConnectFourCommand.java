package com.matthew.plugin.connectfour.modules.manager.commands;

import com.matthew.plugin.connectfour.game.ConnectFourBoardGame;
import com.matthew.plugin.connectfour.modules.game.GameModule;
import com.matthew.plugin.connectfour.modules.manager.ServerModuleManager;
import org.bukkit.Bukkit;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public class ConnectFourCommand extends Command {

    private static final String COMMAND_NAME = "connectfour";

    private final GameModule module;

    public ConnectFourCommand() {
        super(COMMAND_NAME);
        final ServerModuleManager manager = ServerModuleManager.getInstance();
        this.module = manager.getRegisteredModule(GameModule.class);

    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {

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
            player.sendMessage(ChatColor.RED + "Player not found");
            return true;
        }

        //TODO: Implement a game invite system so that target can deny or accept game invite
        ConnectFourBoardGame game = new ConnectFourBoardGame(player, target);
        game.startGame();

        return true;
    }
}
