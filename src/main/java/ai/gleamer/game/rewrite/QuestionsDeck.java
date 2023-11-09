package ai.gleamer.game.rewrite;

import java.util.LinkedList;
import java.util.List;

class QuestionsDeck {

    private final List<String> popQuestions;
    private final List<String> scienceQuestions;
    private final List<String> sportsQuestions;
    private final List<String> rockQuestions;

    QuestionsDeck(int nbCardsPerCategory) {
        this.popQuestions = new LinkedList<>();
        this.scienceQuestions = new LinkedList<>();
        this.sportsQuestions = new LinkedList<>();
        this.rockQuestions = new LinkedList<>();

        for (int i = 0; i < nbCardsPerCategory; i++) {
            this.popQuestions.addLast(QuestionsBuilder.createPopQuestion(i));
            this.scienceQuestions.addLast(QuestionsBuilder.createScienceQuestion(i));
            this.sportsQuestions.addLast(QuestionsBuilder.createSportsQuestion(i));
            this.rockQuestions.addLast(QuestionsBuilder.createRockQuestion(i));
        }
    }

    public String pickQuestion(Category category) {
        List<String> questions = switch (category) {
            case POP -> this.popQuestions;
            case SCIENCE -> this.scienceQuestions;
            case SPORTS -> this.sportsQuestions;
            case ROCK -> this.rockQuestions;
        };

        return questions.removeFirst();
    }

}
