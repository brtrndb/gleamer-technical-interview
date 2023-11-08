package ai.gleamer.game.rewrite;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class Player {

    private final String name;
    private int location;
    private int coins;
    private boolean isInPenaltyBox;

    public Player(String name) {
        this.name = Objects.requireNonNull(name, "Player name is null");
        this.location = 0;
        this.coins = 0;
        this.isInPenaltyBox = false;
    }

    public int addOneCoin() {
        this.coins = this.coins + 1;

        return this.coins;
    }

}
