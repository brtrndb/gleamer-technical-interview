package ai.gleamer.game.rewrite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Game {

    private static final Logger log = LoggerFactory.getLogger(Game.class);

    private static final int MINIMUM_PLAYERS = 2;
    private static final int MAXIMUM_PLAYERS = 6;
    private static final int QUESTION_LIST_SIZE = 50;
    private static final int REQUIRED_COINS_FOR_WINNING = 6;
    private static final int TOTAL_SQUARES = 12;

    private final PlayersList playersList;

    List<String> popQuestions;
    List<String> scienceQuestions;
    List<String> sportsQuestions;
    List<String> rockQuestions;

    boolean isGettingOutOfPenaltyBox;

    public Game() {
        this.playersList = new PlayersList(MAXIMUM_PLAYERS);

        this.popQuestions = new LinkedList<>();
        this.scienceQuestions = new LinkedList<>();
        this.sportsQuestions = new LinkedList<>();
        this.rockQuestions = new LinkedList<>();

        this.isGettingOutOfPenaltyBox = false;

        for (int i = 0; i < QUESTION_LIST_SIZE; i++) {
            this.popQuestions.addLast(QuestionsBuilder.createPopQuestion(i));
            this.scienceQuestions.addLast(QuestionsBuilder.createScienceQuestion(i));
            this.sportsQuestions.addLast(QuestionsBuilder.createSportsQuestion(i));
            this.rockQuestions.addLast(QuestionsBuilder.createRockQuestion(i));
        }
    }

    public boolean isReadyToPlay() {
        return (this.getPlayersCount() >= MINIMUM_PLAYERS);
    }

    public boolean add(String playerName) {
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
        if (roll <= 0) {
            throw new RuntimeException("Invalid roll");
        }

        Player player = this.playersList.getCurrentPlayer();

        log.info("Current player {} has rolled a {}.", player.getName(), roll);

        boolean isCurrentPlayerOutOfPenaltyBox = !player.isInPenaltyBox();
        this.isGettingOutOfPenaltyBox = this.canPlayerGetOutOfPenaltyBox(player, roll);

        if (isCurrentPlayerOutOfPenaltyBox || this.isGettingOutOfPenaltyBox) {
            int newPlayerPosition = this.movePlayer(player, roll);

            Category category = this.getCurrentCategory(newPlayerPosition);

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

    private int movePlayer(Player player, int roll) {
        int currentPlayerLocation = player.getLocation();
        int newPlayerLocation = currentPlayerLocation + roll;

        if (newPlayerLocation >= TOTAL_SQUARES) {
            newPlayerLocation = newPlayerLocation - TOTAL_SQUARES;
        }

        player.setLocation(newPlayerLocation);

        log.info("{}'s new location is {}.", player, player.getLocation());

        return player.getLocation();
    }

    private void askQuestion(Category category) {
        List<String> currentCategoryQuestions = switch (category) {
            case POP -> this.popQuestions;
            case SCIENCE -> this.scienceQuestions;
            case SPORTS -> this.sportsQuestions;
            case ROCK -> this.rockQuestions;
        };

        String currentQuestion = currentCategoryQuestions.removeFirst();

        log.info(currentQuestion);
    }

    private Category getCurrentCategory(int location) {
        return switch (location) {
            case 0, 4, 8 -> Category.POP;
            case 1, 5, 9 -> Category.SCIENCE;
            case 2, 6, 10 -> Category.SPORTS;
            default -> Category.ROCK;
        };
    }

    public boolean wasCorrectlyAnswered() {
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

        boolean winner = this.didPlayerWin(player);

        this.playersList.getNextPlayer();

        return winner;
    }

    public boolean wrongAnswer() {
        Player player = this.playersList.getCurrentPlayer();

        log.info("{} incorrectly answered question and has been sent to penalty box.", player);
        player.setInPenaltyBox(true);

        this.playersList.getNextPlayer();

        return true;
    }

    private boolean didPlayerWin(Player player) {
        return player.getCoins() == REQUIRED_COINS_FOR_WINNING;
    }

}
