package ai.gleamer.game.rewrite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Game {

    private static final Logger log = LoggerFactory.getLogger(Game.class);

    private static final int MINIMUM_PLAYERS = 2;
    private static final int MAXIMUM_PLAYERS = 6;
    private static final int QUESTION_LIST_SIZE = 50;
    private static final int REQUIRED_COINS_FOR_WINNING = 6;
    private static final int TOTAL_SQUARES = 12;

    List<String> players;
    int[] places;
    int[] purses;
    boolean[] inPenaltyBox;

    List<String> popQuestions;
    List<String> scienceQuestions;
    List<String> sportsQuestions;
    List<String> rockQuestions;

    int currentPlayerIndex;
    boolean isGettingOutOfPenaltyBox;

    public Game() {
        this.players = new ArrayList<>();
        this.places = new int[MAXIMUM_PLAYERS];
        this.purses = new int[MAXIMUM_PLAYERS];
        this.inPenaltyBox = new boolean[MAXIMUM_PLAYERS];

        this.popQuestions = new LinkedList<>();
        this.scienceQuestions = new LinkedList<>();
        this.sportsQuestions = new LinkedList<>();
        this.rockQuestions = new LinkedList<>();

        this.currentPlayerIndex = 0;
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

        this.players.add(playerName);
        this.places[playersCount] = 0;
        this.purses[playersCount] = 0;
        this.inPenaltyBox[playersCount] = false;

        log.info("{} was added with player number {}.", playerName, this.players.size());

        return true;
    }

    public int getPlayersCount() {
        return this.players.size();
    }

    public void roll(int roll) {
        String currentPlayerName = this.players.get(this.currentPlayerIndex);

        log.info("Current player {} has rolled a {}.", currentPlayerName, roll);

        boolean isCurrentPlayerOutOfPenaltyBox = !this.inPenaltyBox[this.currentPlayerIndex];
        this.isGettingOutOfPenaltyBox = this.canPlayerGetOutOfPenaltyBox(currentPlayerName, roll);

        if (isCurrentPlayerOutOfPenaltyBox || this.isGettingOutOfPenaltyBox) {
            this.movePlayer(currentPlayerName, roll);

            Category category = this.getCurrentCategory();

            log.info("The current category is {}.", category);

            this.askQuestion(category);
        }
    }

    private boolean canPlayerGetOutOfPenaltyBox(String currentPlayerName, int roll) {
        boolean isCurrentPlayerInPenaltyBox = this.inPenaltyBox[this.currentPlayerIndex];
        boolean isRollEven = (roll % 2) == 0;

        if (isCurrentPlayerInPenaltyBox && isRollEven) {
            log.info("{} is not getting out of the penalty box.", currentPlayerName);
            return false;
        }

        if (isCurrentPlayerInPenaltyBox) {
            log.info("{} is getting out of the penalty box.", currentPlayerName);
            return true;
        }

        return this.isGettingOutOfPenaltyBox;
    }

    private void movePlayer(String currentPlayerName, int roll) {
        int currentPlayerPlace = this.places[this.currentPlayerIndex];
        int newPlayerPlace = currentPlayerPlace + roll;

        if (newPlayerPlace >= TOTAL_SQUARES) {
            newPlayerPlace = newPlayerPlace - TOTAL_SQUARES;
        }

        this.places[this.currentPlayerIndex] = newPlayerPlace;

        log.info("{}'s new location is {}.", currentPlayerName, this.places[this.currentPlayerIndex]);
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

    private Category getCurrentCategory() {
        int currentPlayerPlace = this.places[this.currentPlayerIndex];

        return switch (currentPlayerPlace) {
            case 0, 4, 8 -> Category.POP;
            case 1, 5, 9 -> Category.SCIENCE;
            case 2, 6, 10 -> Category.SPORTS;
            default -> Category.ROCK;
        };
    }

    public boolean wasCorrectlyAnswered() {
        String currentPlayerName = this.players.get(this.currentPlayerIndex);
        boolean isCurrentPlayerInPenaltyBox = this.inPenaltyBox[this.currentPlayerIndex];

        if (isCurrentPlayerInPenaltyBox && !this.isGettingOutOfPenaltyBox) {
            this.getNextPlayer();

            return true;
        }

        log.info("Answer was correct !");
        this.purses[this.currentPlayerIndex]++;
        log.info("{} now has {} Gold Coins.", currentPlayerName, this.purses[this.currentPlayerIndex]);

        boolean winner = this.didPlayerWin();

        this.getNextPlayer();

        return winner;
    }

    public boolean wrongAnswer() {
        String currentPlayerName = this.players.get(this.currentPlayerIndex);

        log.info("{} incorrectly answered question and has been sent to penalty box.", currentPlayerName);
        this.inPenaltyBox[this.currentPlayerIndex] = true;

        this.getNextPlayer();

        return true;
    }

    private boolean didPlayerWin() {
        return this.purses[this.currentPlayerIndex] != REQUIRED_COINS_FOR_WINNING;
    }

    private int getNextPlayer() {
        this.currentPlayerIndex = (this.currentPlayerIndex + 1) % this.players.size();

        return this.currentPlayerIndex;
    }

}
