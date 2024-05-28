package com.matthew.plugin.connectfour.game.mechanics;

import com.matthew.plugin.connectfour.game.GameState;
import com.matthew.plugin.connectfour.game.mechanics.framework.ConnectFourBoard;
import com.matthew.plugin.connectfour.utils.Cuboid;
import com.matthew.plugin.connectfour.utils.DirectionUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;


public class ConnectFourBoardMechanic extends ConnectFourBoard {

    private Player winner;

    private Cuboid region;

    public ConnectFourBoardMechanic(final Player player1, final Player player2) {
        super(player1, player2);
        setup();
    }

    public Cuboid getRegion() {
        return this.region;
    }

    @Override
    public boolean checkWinner() {
        for (Block block : getBottomBlocks()) {

            // Check in directions based on the orientation of the board
            switch (getDirection()) {
                case EAST:
                    if (checkDirection(block, 0, 1, 0) ||     // Right
                            checkDirection(block, 1, 0, 0) ||     // Down
                            checkDirection(block, 1, 1, 0) ||     // Diagonal (down-right)
                            checkDirection(block, -1, 1, 0)) {    // Diagonal (up-right)
                        return true; // Found a winning sequence
                    }
                    break;
                case WEST:
                    if (checkDirection(block, 0, -1, 0) ||    // Left
                            checkDirection(block, 1, 0, 0) ||     // Down
                            checkDirection(block, 1, -1, 0) ||    // Diagonal (down-left)
                            checkDirection(block, -1, -1, 0)) {   // Diagonal (up-left)
                        return true; // Found a winning sequence
                    }
                    break;
                case SOUTH:
                    if (checkDirection(block, 1, 0, 0) ||     // Down
                            checkDirection(block, 0, 1, 0) ||     // Right
                            checkDirection(block, -1, 1, 0) ||    // Diagonal (up-right)
                            checkDirection(block, -1, -1, 0)) {   // Diagonal (up-left)
                        return true; // Found a winning sequence
                    }
                    break;
                case NORTH:
                default:
                    if (checkDirection(block, -1, 0, 0) ||    // Up
                            checkDirection(block, 0, -1, 0) ||    // Left
                            checkDirection(block, 1, -1, 0) ||    // Diagonal (down-left)
                            checkDirection(block, 1, 1, 0)) {     // Diagonal (down-right)
                        return true; // Found a winning sequence
                    }
                    break;
            }
        }
        return false; // No winning sequence found
    }


    @Override
    public void spawnBoard() {
        // Set block types for the bottom blocks
        for (Block block : getBottomBlocks()) {
            block.setType(Material.QUARTZ_BLOCK);
        }

        // Set block types for the top blocks
        for (Block block : getTopBlocks()) {
            block.setType(Material.QUARTZ_BLOCK);
        }

        // Determine the spawn location in front of the board
        Location spawn = getBottomBlocks().get(3).getLocation().add(DirectionUtil.getOffsetForDirection(getDirection()));

        // Create board cuboid region
        Location bottomCorner = getBottomBlocks().get(0).getLocation();
        Location topCorner = getTopBlocks().get(getTopBlocks().size() - 1).getLocation();
        this.region = new Cuboid(bottomCorner, topCorner);

        // Teleport players to the spawn location
        getPlayers().get(0).teleport(spawn);
        getPlayers().get(1).teleport(spawn);
    }

    @Override
    public void destroyBoard() {
        // Set block types to air for bottom blocks
        getBottomBlocks().forEach(block -> block.setType(Material.AIR));

        // Set block types to air for top blocks
        getTopBlocks().forEach(block -> block.setType(Material.AIR));

        // Clear lists
        getBottomBlocks().clear();
        getTopBlocks().clear();

        // Release any allocated resources associated with the region
        region.getBlocks().clear();
        region = null;
    }

    private void setup() {
        //this.turn = this.players.get(0);
    }

    private boolean checkDirection(Block startBlock, int dx, int dy, int dz) {
        Material type = startBlock.getType();
        Location startLocation = startBlock.getLocation();

        for (int i = 0; i < 4; i++) {
            boolean matched = true;
            for (int j = 1; j < 4; j++) {
                Location currentLocation = startLocation.clone().add(dx * j, dy * j, dz * j);
                if (currentLocation.getBlock().getType() != type) {
                    matched = false;
                    break;
                }
            }
            if (matched) {
                return true;
            }
            startLocation.add(-dx, -dy, -dz); // Move back to the starting position to check in the opposite direction
        }
        return false;
    }

}
