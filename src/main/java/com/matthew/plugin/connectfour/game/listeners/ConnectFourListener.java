package com.matthew.plugin.connectfour.game.listeners;

import com.matthew.plugin.connectfour.game.ConnectFourBoardGame;
import com.matthew.plugin.connectfour.modules.game.GameModule;
import com.matthew.plugin.connectfour.modules.manager.ServerModuleManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class ConnectFourListener implements Listener {

    private final GameModule module;

    public ConnectFourListener() {
        final ServerModuleManager manager = ServerModuleManager.getInstance();
        this.module = manager.getRegisteredModule(GameModule.class);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        // Check if the player is playing Connect Four
        if (!module.isPlaying(player)) {
            return;
        }

        // Check if the interaction is a right-click on a block
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        // Ensure the interaction is done with the main hand
        if (event.getHand() == EquipmentSlot.OFF_HAND) {
            return;
        }

        // Check if the player's main hand is empty
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand.getType() != Material.AIR) {
            return;
        }

        // Retrieve the clicked block
        Block clickedBlock = event.getClickedBlock();

        // Check if the clicked block is a quartz block
        if (clickedBlock == null || clickedBlock.getType() != Material.QUARTZ_BLOCK) {
            return;
        }

        // Get the Connect Four game instance associated with the player
        ConnectFourBoardGame game = module.getGame(player);

        // Place the player's piece on the column above the clicked block
        game.placeBlock(player, clickedBlock);

        // Check if the player has achieved a winning sequence
        if (game.hasWinningSequence(player)) {
            // Announce the winner and end the game
            game.sendMessage(ChatColor.YELLOW + player.getName() + " wins!");
            game.stopGame();
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onQuit(PlayerQuitEvent event) {

        // Check if the player that left is in a game
        if(!module.isPlaying(event.getPlayer())) {
            return;
        }

        // Get the player that left
        Player player = event.getPlayer();

        // Get the game that the player that left was in
        ConnectFourBoardGame game = module.getGame(player);

        // Tell the opponent in the game that the player has left and that the game is being stopped
        game.sendMessage(ChatColor.YELLOW + player.getName() + " has left. Game stopping.");

        // Stop the game
        game.stopGame();
    }
}
