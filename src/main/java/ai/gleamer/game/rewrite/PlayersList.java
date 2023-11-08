package ai.gleamer.game.rewrite;

import java.util.ArrayList;
import java.util.List;

public class PlayersList {

    private final int maximumPlayersCount;
    private final List<Player> players;
    private int currentPlayerIndex;

    public PlayersList(int maximumPlayersCount) {
        this.maximumPlayersCount = maximumPlayersCount;
        this.players = new ArrayList<>(this.maximumPlayersCount);
        this.currentPlayerIndex = 0;
    }

    public Player addPlayer(String playerName) {
        if (this.getPlayersCount() >= this.maximumPlayersCount) {
            throw new RuntimeException("Too many players");
        }

        Player player = new Player(playerName);

        this.players.add(player);

        return player;
    }

    public int getPlayersCount() {
        return this.players.size();
    }

    public Player getCurrentPlayer() {
        if (this.players.isEmpty()) {
            throw new IndexOutOfBoundsException("There is no players");
        }

        return this.players.get(this.currentPlayerIndex);
    }

    public Player getNextPlayer() {
        if (this.players.isEmpty()) {
            throw new IndexOutOfBoundsException("There is no players");
        }

        this.currentPlayerIndex = (this.currentPlayerIndex + 1) % this.players.size();

        return this.players.get(this.currentPlayerIndex);
    }

}
