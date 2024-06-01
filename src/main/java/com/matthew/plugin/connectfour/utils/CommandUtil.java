package com.matthew.plugin.connectfour.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;

import java.lang.reflect.Field;

import static org.bukkit.Bukkit.getServer;

public final class CommandUtil {

    private CommandUtil() {}

    public static void register(final Command command) {
        registerCommand(command);
    }

    private static void registerCommand(Command command) {
        try {
            Field commandMapField = getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            CommandMap commandMap = (CommandMap) commandMapField.get(getServer());
            commandMap.register(command.getName(), command);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Bukkit.getLogger().severe("Failed to register command");
        }
    }
}
