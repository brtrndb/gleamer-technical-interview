package ai.gleamer.game.rewrite;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class PlayersListTest {

    @Test
    void should_add_a_player() {
        // Given:
        PlayersList playersList = new PlayersList(5);
        String playerName = "Player Name";
        int playersCountBefore = playersList.getPlayersCount();

        // When:
        Player playerAdded = playersList.addPlayer(playerName);
        int playersCountAfter = playersList.getPlayersCount();

        // Then:
        Assertions.assertThat(playerAdded)
                .isNotNull()
                .satisfies(p -> Assertions.assertThat(p.getName())
                        .isNotNull()
                        .isEqualTo(playerName)
                );
        Assertions.assertThat(playersCountAfter)
                .isEqualTo(playersCountBefore + 1);
    }

    @Test
    void should_get_first_current_player() {
        // Given:
        PlayersList playersList = new PlayersList(5);
        Player playerAdded0 = playersList.addPlayer("Player Name 0");
        Player playerAdded1 = playersList.addPlayer("Player Name 1");
        Player playerAdded2 = playersList.addPlayer("Player Name 2");

        // When:
        Player currentPlayer = playersList.getCurrentPlayer();

        // Then:
        Assertions.assertThat(currentPlayer)
                .isNotNull()
                .satisfies(p -> Assertions.assertThat(p.getName())
                        .isNotNull()
                        .isEqualTo(playerAdded0.getName())
                );
    }

    @Test
    void should_get_current_player_after_moving_next() {
        // Given:
        PlayersList playersList = new PlayersList(5);
        Player playerAdded0 = playersList.addPlayer("Player Name 0");
        Player playerAdded1 = playersList.addPlayer("Player Name 1");
        Player playerAdded2 = playersList.addPlayer("Player Name 2");

        // When:
        Player nextPlayer = playersList.getNextPlayer();
        Player currentPlayer = playersList.getCurrentPlayer();

        // Then:
        Assertions.assertThat(nextPlayer)
                .isNotNull()
                .satisfies(p -> Assertions.assertThat(p.getName())
                        .isNotNull()
                        .isEqualTo(playerAdded1.getName())
                )
                .satisfies(p -> Assertions.assertThat(p.getName())
                        .isNotNull()
                        .isEqualTo(currentPlayer.getName())
                );
    }

}
