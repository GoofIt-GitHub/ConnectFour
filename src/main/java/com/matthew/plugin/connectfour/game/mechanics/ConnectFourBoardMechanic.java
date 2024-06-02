package com.matthew.plugin.connectfour.game.mechanics;


import com.matthew.plugin.connectfour.game.mechanics.framework.ConnectFourBoard;
import com.matthew.plugin.connectfour.utils.Cuboid;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;


import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class ConnectFourBoardMechanic extends ConnectFourBoard {


    /**
     * List of players in the game. Should always be two players
     */
    private final List<Player> players;


    /**
     * Player who is to place their block
     */
    private Player turn;


    /**
     * The winner of the game. Null until set otherwise
     */
    private Player winner;


    /**
     * Block that was most recently placed
     */
    private Block recentBlock;


    /**
     * Region containing all the blocks on the board
     */
    private Cuboid region;


    /**
     * Condition stating whether the game is running or not
     */
    private boolean running;


    public ConnectFourBoardMechanic(final List<Player> players) {
        super(players.get(0)); // player1 being the location the board is going to spawn
        this.players = players;
        this.winner = null;
        this.running = false;
        this.recentBlock = null;
    }


    /**
     * Spawns the Connect Four board by setting the types of blocks in the bottom and top rows,
     * teleporting players to the board, and creating the board region.
     */
    @Override
    public void spawnBoard() {
        setupBoardBlocks();
        teleportPlayers();
        createBoardRegion();
    }


    /**
     * Destroys the Connect Four board on the main thread by scheduling the removal of blocks after a delay.
     */
    @Override
    public void destroyBoard() {
        scheduleDestroyBoard(3000);
    }


    /**
     * Places a block on the board for the specified player if the game is running and valid conditions are met.
     *
     * @param player       the player making the move
     * @param blockClicked the block clicked by the player
     */
    public void placeBlock(final Player player, final Block blockClicked) {
        if (isValidMove(player, blockClicked)) {
            executeMove(player, blockClicked);
            Bukkit.getLogger().info("ran3");
        }
    }


    /**
     * Checks if the specified player has a winning sequence of four blocks.
     *
     * @param player the player to check for a winning sequence
     * @return true if the player has a winning sequence, otherwise false
     */
    public boolean hasWinningSequence(Player player) {
        Material type = isPlayer1(player) ? Material.RED_WOOL : Material.YELLOW_WOOL;
        BlockFace direction = getDirection();
        BlockFace[] directions = getPrimaryDirections(direction);

        for (BlockFace dir : directions) {
            if (checkWinningSequence(dir, type)) {
                return true;
            }
        }

        return checkWinningSequence(BlockFace.UP, type) || checkWinningSequence(BlockFace.DOWN, type);
    }

    /**
     * Sends a message to all players in the game.
     *
     * @param message the message to send
     */
    public void sendMessage(final String message) {
        players.forEach(player -> player.sendMessage(message));
    }


    /**
     * Checks if the game is currently running.
     *
     * @return true if the game is running, otherwise false
     */
    public boolean isRunning() {
        return running;
    }


    /**
     * Gets the list of players in the game.
     *
     * @return the list of players
     */
    public List<Player> getPlayers() {
        return players;
    }


    /**
     * Gets the cuboid region representing all blocks on the Connect Four board.
     *
     * @return the cuboid region
     */
    public Cuboid getRegion() {
        return region;
    }


    /**
     * Gets the winner of the game.
     *
     * @return the winning player, or null if there is no winner yet
     */
    public Player getWinner() {
        return winner;
    }


    /**
     * Gets the player whose turn it is to make a move.
     *
     * @return the player whose turn it is
     */
    public Player getTurn() {
        return turn;
    }


    /**
     * Sets the player whose turn it is to make a move.
     *
     * @param turn the player whose turn it is
     */
    public void setTurn(final Player turn) {
        this.turn = turn;
    }

    public void setRunning(final boolean running) {
        this.running = running;
    }


    /**
     * Sets up the Connect Four board by spawning bottom and top blocks.
     */
    private void setupBoardBlocks() {
        getBottomBlocks().forEach(block -> block.setType(Material.QUARTZ_BLOCK));
        getTopBlocks().forEach(block -> block.setType(Material.QUARTZ_BLOCK));
    }


    /**
     * Teleports players to the spawn location in front of the board.
     */
    private void teleportPlayers() {
        Vector direction = players.get(0).getLocation().getDirection();

        Location spawn = getBottomBlocks().get(0).getLocation().add(0.5, 1, 0.5); // Adjusted spawn location to center of the block
        spawn.setDirection(direction);

        // Teleport players and set their direction
        players.forEach(player -> {
            player.teleport(spawn);
        });
    }


    /**
     * Creates the cuboid region representing all blocks on the Connect Four board.
     */
    private void createBoardRegion() {
        Location bottomCorner = getBottomBlocks().get(0).getLocation();
        Location topCorner = getTopBlocks().get(getTopBlocks().size() - 1).getLocation();
        region = new Cuboid(bottomCorner, topCorner);
    }


    /**
     * Checks if a move is valid for the specified player and block.
     *
     * @param player       the player making the move
     * @param blockClicked the block clicked by the player
     * @return true if the move is valid, otherwise false
     */
    private boolean isValidMove(Player player, Block blockClicked) {
        return running && players.contains(player) && blockClicked.getType() == Material.QUARTZ_BLOCK
                && getBottomBlocks().contains(blockClicked);
    }


    /**
     * Executes a move by placing a block on the board for the specified player.
     *
     * @param player       the player making the move
     * @param blockClicked the block clicked by the player
     */
    private void executeMove(Player player, Block blockClicked) {
        if(!turn.equals(player)) {
            player.sendMessage("Please wait your turn");
            return;
        }

        Block currentBlock = blockClicked;

        for (int i = 0; i < 6; i++) {
            Block blockAbove = currentBlock.getRelative(BlockFace.UP);
            if (blockAbove.getType() == Material.AIR) {
                Material woolType = isPlayer1(player) ? Material.RED_WOOL : Material.YELLOW_WOOL;
                blockAbove.setType(woolType);
                recentBlock = blockAbove;
                turn = isPlayer1(player) ? players.get(1) : players.get(0);
                Bukkit.getLogger().info("ran4");
                break;
            }
            currentBlock = blockAbove;
        }
    }


    /**
     * Gets the primary directions to check for a winning sequence based on the board's orientation.
     *
     * @param direction the board's orientation direction
     * @return an array of primary directions
     */
    private BlockFace[] getPrimaryDirections(BlockFace direction) {
        if (direction == BlockFace.EAST || direction == BlockFace.WEST) {
            return new BlockFace[]{BlockFace.NORTH, BlockFace.SOUTH};
        } else if (direction == BlockFace.NORTH || direction == BlockFace.SOUTH) {
            return new BlockFace[]{BlockFace.WEST, BlockFace.EAST};
        } else {
            return new BlockFace[0];
        }
    }


    /**
     * Checks for a winning sequence in a specified direction for a given material type.
     *
     * @param direction the direction to check
     * @param type      the material type to check for
     * @return true if a winning sequence is found, otherwise false
     */
    private boolean checkWinningSequence(BlockFace direction, Material type) {
        return checkDirection(direction, type) || checkDirection(direction.getOppositeFace(), type)
                || checkDirection(direction, BlockFace.UP, type) || checkDirection(direction, BlockFace.DOWN, type)
                || checkDirection(direction.getOppositeFace(), BlockFace.UP, type) || checkDirection(direction.getOppositeFace(), BlockFace.DOWN, type);
    }


    /**
     * Checks for a winning sequence in a specified direction for a given material type.
     *
     * @param direction the direction to check
     * @param type      the material type to check for
     * @return true if a winning sequence is found, otherwise false
     */
    private boolean checkDirection(BlockFace direction, Material type) {
        if (recentBlock == null) {
            return false;
        }

        int matchesInARow = 0;

        for (int i = 0; i < 4; i++) {
            recentBlock = recentBlock.getRelative(direction);
            if (recentBlock.getType() == type) {
                matchesInARow++;
                if (matchesInARow == 4) {
                    return true;
                }
            } else {
                break;
            }
        }
        return false;
    }


    /**
     * Checks for a winning sequence in a specified diagonal direction and slope for a given material type.
     *
     * @param direction the primary direction to check
     * @param slope     the slope direction to check
     * @param type      the material type to check for
     * @return true if a winning sequence is found, otherwise false
     */
    private boolean checkDirection(BlockFace direction, BlockFace slope, Material type) {
        if (recentBlock == null) {
            return false;
        }

        int matchesInARow = 1;

        for (int i = 0; i < 3; i++) {
            recentBlock = recentBlock.getRelative(direction).getRelative(slope);
            if (recentBlock.getType() == type) {
                matchesInARow++;
                if (matchesInARow == 4) {
                    return true;
                }
            } else {
                break;
            }
        }
        return false;
    }


    /**
     * Schedules the destruction of the Connect Four board after a delay.
     *
     * @param delayMillis the delay in milliseconds
     */
    private void scheduleDestroyBoard(long delayMillis) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        try (ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1)) {
            scheduler.schedule(() -> {
                future.complete(null);
                scheduler.shutdown();
            }, delayMillis, TimeUnit.MILLISECONDS);
        }


        future.thenRun(() -> runOnMainThread(this::performDestroyBoard));
    }


    /**
     * Runs a task on the main thread after a delay.
     *
     * @param task the task to run
     */
    private void runOnMainThread(Runnable task) {
        if (Thread.currentThread().getName().equals("main")) {
            task.run();
        } else {
            CompletableFuture.runAsync(task, Executors.newSingleThreadExecutor(r -> {
                Thread mainThread = new Thread(r);
                mainThread.setName("main");
                return mainThread;
            }));
        }
    }


    /**
     * Performs the destruction of the Connect Four board by setting block types to air and clearing lists.
     */
    private void performDestroyBoard() {
        getBottomBlocks().forEach(block -> block.setType(Material.AIR));
        getTopBlocks().forEach(block -> block.setType(Material.AIR));
        getBottomBlocks().clear();
        getTopBlocks().clear();
        region.getBlocks().clear();
        region = null;
    }


    /**
     * Checks if the specified player is player 1.
     *
     * @param player the player to check
     * @return true if the player is player 1, otherwise false
     */
    private boolean isPlayer1(final Player player) {
        return players.get(0).equals(player);
    }
}