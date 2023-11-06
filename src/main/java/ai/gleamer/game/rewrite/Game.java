package ai.gleamer.game.rewrite;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Game {

    List<String> players = new ArrayList<>();
    int[] places = new int[6];
    int[] purses = new int[6];
    boolean[] inPenaltyBox = new boolean[6];

    List<String> popQuestions = new LinkedList<>();
    List<String> scienceQuestions = new LinkedList<>();
    List<String> sportsQuestions = new LinkedList<>();
    List<String> rockQuestions = new LinkedList<>();

    int currentPlayer = 0;
    boolean isGettingOutOfPenaltyBox;

    public Game() {
        for (int i = 0; i < 50; i++) {
            this.popQuestions.addLast("Pop Question " + i);
            this.scienceQuestions.addLast(("Science Question " + i));
            this.sportsQuestions.addLast(("Sports Question " + i));
            this.rockQuestions.addLast(this.createRockQuestion(i));
        }
    }

    public String createRockQuestion(int index) {
        return "Rock Question " + index;
    }

    public boolean isPlayable() {
        return (this.howManyPlayers() >= 2);
    }

    public boolean add(String playerName) {
        this.players.add(playerName);
        this.places[this.howManyPlayers()] = 0;
        this.purses[this.howManyPlayers()] = 0;
        this.inPenaltyBox[this.howManyPlayers()] = false;

        System.out.println(playerName + " was added");
        System.out.println("They are player number " + this.players.size());

        return true;
    }

    public int howManyPlayers() {
        return this.players.size();
    }

    public void roll(int roll) {
        System.out.println(this.players.get(this.currentPlayer) + " is the current player");
        System.out.println("They have rolled a " + roll);

        if (this.inPenaltyBox[this.currentPlayer]) {
            if (roll % 2 != 0) {
                this.isGettingOutOfPenaltyBox = true;

                System.out.println(this.players.get(this.currentPlayer) + " is getting out of the penalty box");
                this.places[this.currentPlayer] = this.places[this.currentPlayer] + roll;
                if (this.places[this.currentPlayer] > 11) {
                    this.places[this.currentPlayer] = this.places[this.currentPlayer] - 12;
                }

                System.out.println(this.players.get(this.currentPlayer)
                        + "'s new location is "
                        + this.places[this.currentPlayer]);
                System.out.println("The category is " + this.currentCategory());
                this.askQuestion();
            } else {
                System.out.println(this.players.get(this.currentPlayer) + " is not getting out of the penalty box");
                this.isGettingOutOfPenaltyBox = false;
            }
        } else {
            this.places[this.currentPlayer] = this.places[this.currentPlayer] + roll;
            if (this.places[this.currentPlayer] > 11) {
                this.places[this.currentPlayer] = this.places[this.currentPlayer] - 12;
            }

            System.out.println(this.players.get(this.currentPlayer)
                    + "'s new location is "
                    + this.places[this.currentPlayer]);
            System.out.println("The category is " + this.currentCategory());
            this.askQuestion();
        }
    }

    private void askQuestion() {
        if (this.currentCategory() == "Pop") {
            System.out.println(this.popQuestions.removeFirst());
        }
        if (this.currentCategory() == "Science") {
            System.out.println(this.scienceQuestions.removeFirst());
        }
        if (this.currentCategory() == "Sports") {
            System.out.println(this.sportsQuestions.removeFirst());
        }
        if (this.currentCategory() == "Rock") {
            System.out.println(this.rockQuestions.removeFirst());
        }
    }

    private String currentCategory() {
        if (this.places[this.currentPlayer] == 0) {
            return "Pop";
        }
        if (this.places[this.currentPlayer] == 4) {
            return "Pop";
        }
        if (this.places[this.currentPlayer] == 8) {
            return "Pop";
        }
        if (this.places[this.currentPlayer] == 1) {
            return "Science";
        }
        if (this.places[this.currentPlayer] == 5) {
            return "Science";
        }
        if (this.places[this.currentPlayer] == 9) {
            return "Science";
        }
        if (this.places[this.currentPlayer] == 2) {
            return "Sports";
        }
        if (this.places[this.currentPlayer] == 6) {
            return "Sports";
        }
        if (this.places[this.currentPlayer] == 10) {
            return "Sports";
        }

        return "Rock";
    }

    public boolean wasCorrectlyAnswered() {
        if (this.inPenaltyBox[this.currentPlayer]) {
            if (this.isGettingOutOfPenaltyBox) {
                System.out.println("Answer was correct!!!!");
                this.purses[this.currentPlayer]++;
                System.out.println(this.players.get(this.currentPlayer)
                        + " now has "
                        + this.purses[this.currentPlayer]
                        + " Gold Coins.");

                boolean winner = this.didPlayerWin();
                this.currentPlayer++;
                if (this.currentPlayer == this.players.size()) {
                    this.currentPlayer = 0;
                }

                return winner;
            } else {
                this.currentPlayer++;
                if (this.currentPlayer == this.players.size()) {
                    this.currentPlayer = 0;
                }

                return true;
            }
        } else {
            System.out.println("Answer was corrent!!!!");
            this.purses[this.currentPlayer]++;
            System.out.println(this.players.get(this.currentPlayer)
                    + " now has "
                    + this.purses[this.currentPlayer]
                    + " Gold Coins.");

            boolean winner = this.didPlayerWin();
            this.currentPlayer++;
            if (this.currentPlayer == this.players.size()) {
                this.currentPlayer = 0;
            }

            return winner;
        }
    }

    public boolean wrongAnswer() {
        System.out.println("Question was incorrectly answered");
        System.out.println(this.players.get(this.currentPlayer) + " was sent to the penalty box");
        this.inPenaltyBox[this.currentPlayer] = true;

        this.currentPlayer++;
        if (this.currentPlayer == this.players.size()) {
            this.currentPlayer = 0;
        }

        return true;
    }

    private boolean didPlayerWin() {
        return !(this.purses[this.currentPlayer] == 6);
    }

}
