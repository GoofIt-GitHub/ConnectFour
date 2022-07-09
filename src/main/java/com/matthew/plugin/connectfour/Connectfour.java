package com.matthew.plugin.connectfour;

import com.matthew.plugin.connectfour.apis.ConnectFourManager;
import com.matthew.plugin.connectfour.gamefunction.commands.ConnectFourCommand;
import com.matthew.plugin.connectfour.gamefunction.events.PlayerInteractListener;
import com.matthew.plugin.connectfour.gamefunction.events.PlayerQuitListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Connectfour extends JavaPlugin {

    private static Connectfour main;

    @Override
    public void onEnable() {

        //run first
        new ConnectFourManager();
        main = this;

        registerCommands();
        registerListeners();
        System.out.println("Connect Four running");

    }

    @Override
    public void onDisable() {

    }

    private void registerCommands() {
        getCommand("connectfour").setExecutor(new ConnectFourCommand());
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(), this);
    }


    public static Connectfour getInstance() {
        return main;
    }
}
