package com.matthew.plugin.connectfour.game.listeners;

import com.matthew.plugin.connectfour.game.ConnectFourBoardGame;
import com.matthew.plugin.connectfour.modules.game.GameModule;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener {

    /**
     * Sudo summary of event
     *
     * if the player has nothing in their hand, and they are right-clicking, check if what they right-clicked was a
     * quartz block & the quartz block is on their game board after checking to make sure the player is currently in a connect four game.
     * If they are, and it is their turn to place down their block, then call the placeBlock method in ConnectFourBoardGame's class.
     */

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteract(PlayerInteractEvent e) {

        Player player = e.getPlayer();

        if(player.getItemInHand().getType().equals(Material.AIR)) {
            if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Block block = e.getClickedBlock();
                if(GameModule.getGame(player) != null) {
                    ConnectFourBoardGame game = GameModule.getGame(player);
                    if(game.getTurn().equals(player)) {
                        if (block.getType().equals(Material.QUARTZ_BLOCK) && GameModule.getGame(player).getBottomBlocks().contains(block)) {
                            game.placeBlock(player, block);
                        }
                    } else {
                        player.sendMessage(ChatColor.BLUE + ">> " + ChatColor.GRAY + "Please wait for your turn");
                    }
                }
            }
        }
    }
}
