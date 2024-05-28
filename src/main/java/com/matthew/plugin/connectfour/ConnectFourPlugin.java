package com.matthew.plugin.connectfour;

import com.matthew.plugin.connectfour.modules.game.GameModule;
import com.matthew.plugin.connectfour.modules.manager.ServerModuleManager;
import com.matthew.plugin.connectfour.modules.manager.commands.ConnectFourCommand;
import com.matthew.plugin.connectfour.game.listeners.PlayerInteractListener;
import com.matthew.plugin.connectfour.game.listeners.PlayerQuitListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class ConnectFourPlugin extends JavaPlugin {

    private static ConnectFourPlugin instance;

    @Override
    public void onEnable() {
        final ServerModuleManager moduleManager = ServerModuleManager.getInstance();
        moduleManager.registerModule(new GameModule());
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

    public static ConnectFourPlugin getInstance() {
        return instance;
    }
}
