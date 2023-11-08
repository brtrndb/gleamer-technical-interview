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
    void should_add_one_coin() {
        // Given:
        Player player = new Player("Player Name");
        int coinsBefore = player.getCoins();

        // When:
        int coinsAfter = player.addOneCoin();

        // Then:
        Assertions.assertThat(coinsAfter)
                .isEqualTo(coinsBefore + 1);
    }

}
