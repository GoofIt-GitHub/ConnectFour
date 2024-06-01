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

    private ServerModuleManager moduleManager;

    @Override
    public void onEnable() {
        instance = this;
        moduleManager = ServerModuleManager.getInstance();
        moduleManager.registerModule(new GameModule());

        moduleManager.setup();
    }

    @Override
    public void onDisable() {
        moduleManager.teardown();
    }

    public static ConnectFourPlugin getInstance() {
        return instance;
    }
}
