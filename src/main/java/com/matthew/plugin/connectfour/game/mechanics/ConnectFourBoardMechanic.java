package com.matthew.plugin.connectfour.game.mechanics;

import com.matthew.plugin.connectfour.game.GameState;
import com.matthew.plugin.connectfour.modules.regions.structure.Cuboid;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;

public class ConnectFourBoardMechanic {

    /*
     Best to use ArrayList here to maintain insertion order
     */
    private final ArrayList<Player> players;

    private Player turn;

    private Player winner;

    private Cuboid region;

    private GameState state;

    public ConnectFourBoardMechanic(final Player player1, final Player player2) {
        players = new ArrayList<>(Arrays.asList(player1, player2));
        setup();
    }

    public void spawn(@Nonnull Location location) {

    }

    public void teardown() {

    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    private void setup() {
        this.turn = this.players.get(0);
    }
}
