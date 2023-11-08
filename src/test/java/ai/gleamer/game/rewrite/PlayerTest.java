package ai.gleamer.game.rewrite;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class PlayerTest {

    @Test
    void should_throw_npe_given_name_null() {
        // Given:
        // When:
        Exception exception = Assertions.catchException(() -> new Player(null));

        // Then:
        Assertions.assertThat(exception)
                .isNotNull()
                .isExactlyInstanceOf(NullPointerException.class);
    }

    @Test
    void should_add_one_coin_to_purse() {
        // Given:
        Player player = new Player("Player Name");
        int purseBefore = player.getPurse();

        // When:
        int purseAfter = player.addOneCoinToPurse();

        // Then:
        Assertions.assertThat(purseAfter)
                .isEqualTo(purseBefore + 1);
    }

}
