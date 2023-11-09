package ai.gleamer.game.rewrite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class Game {

    private static final Logger log = LoggerFactory.getLogger(Game.class);

    private static final int MINIMUM_PLAYERS = 2;
    private static final int MAXIMUM_PLAYERS = 6;
    private static final int QUESTION_LIST_SIZE = 50;
    private static final int REQUIRED_COINS_FOR_WINNING = 6;
    private static final int TOTAL_SQUARES = 3 * Category.values().length;

    private final PlayersList playersList;
    private final QuestionsDeck questionsDeck;
    private boolean isGettingOutOfPenaltyBox;

    public Game() {
        this.playersList = new PlayersList(MAXIMUM_PLAYERS);
        this.questionsDeck = new QuestionsDeck(QUESTION_LIST_SIZE);
        this.isGettingOutOfPenaltyBox = false;
    }

    public boolean isReadyToPlay() {
        return (this.getPlayersCount() >= MINIMUM_PLAYERS);
    }

    public boolean addPlayer(String playerName) {
        int playersCount = this.getPlayersCount();

        if (playersCount >= MAXIMUM_PLAYERS) {
            throw new RuntimeException("Too many players");
        }

        if (Objects.isNull(playerName) || playerName.isEmpty()) {
            throw new RuntimeException("Invalid player name");
        }

        this.playersList.addPlayer(playerName);

        log.info("{} was added with player number {}.", playerName, this.getPlayersCount());

        return true;
    }

    public int getPlayersCount() {
        return this.playersList.getPlayersCount();
    }

    public void roll(int roll) {
        this.throwIfNotReady();

        if (roll <= 0) {
            throw new RuntimeException("Invalid roll");
        }

        Player player = this.playersList.getCurrentPlayer();

        log.info("Current player {} has rolled a {}.", player.getName(), roll);

        boolean isCurrentPlayerOutOfPenaltyBox = !player.isInPenaltyBox();
        this.isGettingOutOfPenaltyBox = this.canPlayerGetOutOfPenaltyBox(player, roll);

        if (isCurrentPlayerOutOfPenaltyBox || this.isGettingOutOfPenaltyBox) {
            int newPlayerPosition = Game.movePlayer(player, roll);

            Category category = Game.getCategoryAtLocation(newPlayerPosition);

            log.info("The current category is {}.", category);

            this.askQuestion(category);
        }
    }

    private boolean canPlayerGetOutOfPenaltyBox(Player player, int roll) {
        boolean isCurrentPlayerInPenaltyBox = player.isInPenaltyBox();
        boolean isRollEven = (roll % 2) == 0;

        if (isCurrentPlayerInPenaltyBox && isRollEven) {
            log.info("{} is not getting out of the penalty box.", player);
            return false;
        }

        if (isCurrentPlayerInPenaltyBox) {
            log.info("{} is getting out of the penalty box.", player);
            return true;
        }

        return this.isGettingOutOfPenaltyBox;
    }

    private static int movePlayer(Player player, int roll) {
        int currentPlayerLocation = player.getLocation();
        int newPlayerLocation = currentPlayerLocation + roll;

        if (newPlayerLocation >= TOTAL_SQUARES) {
            newPlayerLocation = newPlayerLocation - TOTAL_SQUARES;
        }

        player.setLocation(newPlayerLocation);

        log.info("{}'s new location is {}.", player, player.getLocation());

        return player.getLocation();
    }

    private static Category getCategoryAtLocation(int location) {
        int categoryIndex = location % Category.values().length;

        return Category.values()[categoryIndex];
    }

    private void askQuestion(Category category) {
        String currentQuestion = this.questionsDeck.pickQuestion(category);

        log.info(currentQuestion);
    }

    public boolean isCorrectlyAnswered() {
        this.throwIfNotReady();

        Player player = this.playersList.getCurrentPlayer();
        boolean isCurrentPlayerInPenaltyBox = player.isInPenaltyBox();

        if (isCurrentPlayerInPenaltyBox && !this.isGettingOutOfPenaltyBox) {
            this.playersList.getNextPlayer();

            return true;
        }

        log.info("Answer was correct !");
        player.setInPenaltyBox(false);
        player.addOneCoin();
        log.info("{} now has {} Gold Coins.", player, player.getCoins());

        boolean winner = Game.didPlayerWin(player);

        this.playersList.getNextPlayer();

        return winner;
    }

    private static boolean didPlayerWin(Player player) {
        return player.getCoins() == REQUIRED_COINS_FOR_WINNING;
    }

    public boolean isWronglyAnswered() {
        this.throwIfNotReady();

        Player player = this.playersList.getCurrentPlayer();

        log.info("{} incorrectly answered question and has been sent to penalty box.", player);
        player.setInPenaltyBox(true);

        this.playersList.getNextPlayer();

        return true;
    }

    private void throwIfNotReady() {
        if (!this.isReadyToPlay()) {
            throw new RuntimeException("The game is not ready");
        }
    }
}
