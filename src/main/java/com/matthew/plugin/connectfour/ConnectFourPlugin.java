package com.matthew.plugin.connectfour;

import com.matthew.plugin.connectfour.modules.game.GameModule;
import com.matthew.plugin.connectfour.modules.manager.ServerModuleManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class ConnectFourPlugin extends JavaPlugin {

    private static ConnectFourPlugin instance;

    private ServerModuleManager moduleManager;

    @Override
    public void onEnable() {
        instance = this;

        Bukkit.getLogger().info("Registering module(s)...");
        moduleManager = ServerModuleManager.getInstance();
        moduleManager.registerModule(new GameModule(this));

        Bukkit.getLogger().info("Setting up module(s)...");
        moduleManager.setup();

        Bukkit.getLogger().info("ConnectFour finished loading");
    }

    @Override
    public void onDisable() {
        moduleManager.teardown();
    }

    public static ConnectFourPlugin getInstance() {
        return instance;
    }
}
