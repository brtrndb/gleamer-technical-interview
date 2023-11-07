package ai.gleamer.game.rewrite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Game {

    private static final Logger log = LoggerFactory.getLogger(Game.class);

    List<String> players;
    int[] places;
    int[] purses;
    boolean[] inPenaltyBox;

    List<String> popQuestions;
    List<String> scienceQuestions;
    List<String> sportsQuestions;
    List<String> rockQuestions;

    int currentPlayer;
    boolean isGettingOutOfPenaltyBox;

    public Game() {
        this.players = new ArrayList<>();
        this.places = new int[6];
        this.purses = new int[6];
        this.inPenaltyBox = new boolean[6];

        this.popQuestions = new LinkedList<>();
        this.scienceQuestions = new LinkedList<>();
        this.sportsQuestions = new LinkedList<>();
        this.rockQuestions = new LinkedList<>();

        this.currentPlayer = 0;
        this.isGettingOutOfPenaltyBox = false;

        for (int i = 0; i < 50; i++) {
            this.popQuestions.addLast(QuestionsBuilder.createPopQuestion(i));
            this.scienceQuestions.addLast(QuestionsBuilder.createScienceQuestion(i));
            this.sportsQuestions.addLast(QuestionsBuilder.createSportsQuestion(i));
            this.rockQuestions.addLast(QuestionsBuilder.createRockQuestion(i));
        }
    }

    public boolean isPlayable() {
        return (this.howManyPlayers() >= 2);
    }

    public boolean add(String playerName) {
        int playersCount = this.howManyPlayers();

        this.players.add(playerName);
        this.places[playersCount] = 0;
        this.purses[playersCount] = 0;
        this.inPenaltyBox[playersCount] = false;

        log.info("{} was added with player number {}.", playerName, this.players.size());

        return true;
    }

    public int howManyPlayers() {
        return this.players.size();
    }

    public void roll(int roll) {
        String currentPlayerName = this.players.get(this.currentPlayer);

        log.info("Current player {} has rolled a {}.", currentPlayerName, roll);

        boolean isCurrentPlayerOutOfPenaltyBox = !this.inPenaltyBox[this.currentPlayer];
        this.isGettingOutOfPenaltyBox = this.canPlayerGetOutOfPenaltyBox(currentPlayerName, roll);

        if (isCurrentPlayerOutOfPenaltyBox || this.isGettingOutOfPenaltyBox) {
            this.movePlayer(currentPlayerName, roll);

            log.info("The current category is {}.", this.currentCategory());

            this.askQuestion();
        }
    }

    private boolean canPlayerGetOutOfPenaltyBox(String currentPlayerName, int roll) {
        boolean isCurrentPlayerInPenaltyBox = this.inPenaltyBox[this.currentPlayer];
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
        this.places[this.currentPlayer] = this.places[this.currentPlayer] + roll;

        if (this.places[this.currentPlayer] > 11) {
            this.places[this.currentPlayer] = this.places[this.currentPlayer] - 12;
        }

        log.info("{}'s new location is {}.", currentPlayerName, this.places[this.currentPlayer]);
    }

    private void askQuestion() {
        Category currentCategory = this.currentCategory();

        List<String> currentCategoryQuestions = switch (currentCategory) {
            case POP -> this.popQuestions;
            case SCIENCE -> this.scienceQuestions;
            case SPORTS -> this.sportsQuestions;
            case ROCK -> this.rockQuestions;
        };

        String currentQuestion = currentCategoryQuestions.removeFirst();

        log.info(currentQuestion);
    }

    private Category currentCategory() {
        int currentPlayerPlace = this.places[this.currentPlayer];

        return switch (currentPlayerPlace) {
            case 0, 4, 8 -> Category.POP;
            case 1, 5, 9 -> Category.SCIENCE;
            case 2, 6, 10 -> Category.SPORTS;
            default -> Category.ROCK;
        };
    }

    public boolean wasCorrectlyAnswered() {
        String currentPlayerName = this.players.get(this.currentPlayer);
        boolean isCurrentPlayerInPenaltyBox = this.inPenaltyBox[this.currentPlayer];

        if (isCurrentPlayerInPenaltyBox && !this.isGettingOutOfPenaltyBox) {
            this.getNextPlayer();

            return true;
        }

        log.info("Answer was correct !");
        this.purses[this.currentPlayer]++;
        log.info("{} now has {} Gold Coins.", currentPlayerName, this.purses[this.currentPlayer]);

        boolean winner = this.didPlayerWin();

        this.getNextPlayer();

        return winner;
    }

    public boolean wrongAnswer() {
        String currentPlayerName = this.players.get(this.currentPlayer);

        log.info("{} incorrectly answered question and has been sent to penalty box.", currentPlayerName);
        this.inPenaltyBox[this.currentPlayer] = true;

        this.getNextPlayer();

        return true;
    }

    private boolean didPlayerWin() {
        return this.purses[this.currentPlayer] != 6;
    }

    private int getNextPlayer() {
        this.currentPlayer = (this.currentPlayer + 1) % this.players.size();

        return this.currentPlayer;
    }

}
