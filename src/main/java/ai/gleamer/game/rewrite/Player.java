package ai.gleamer.game.rewrite;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class Player {

    private final String name;
    private int place;
    private int purse;
    private boolean isInPenaltyBox;

    public Player(String name) {
        this.name = Objects.requireNonNull(name, "Player name is null");
        this.place = 0;
        this.purse = 0;
        this.isInPenaltyBox = false;
    }

    public int addOneCoinToPurse() {
        this.purse = this.purse + 1;

        return this.purse;
    }

}
