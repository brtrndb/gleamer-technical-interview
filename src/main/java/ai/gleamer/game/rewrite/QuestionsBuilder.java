package ai.gleamer.game.rewrite;

final class QuestionsBuilder {

    private QuestionsBuilder() {
    }

    public static String createPopQuestion(int index) {
        return "Pop Question " + index;
    }

    public static String createScienceQuestion(int index) {
        return "Science Question " + index;
    }

    public static String createSportsQuestion(int index) {
        return "Sports Question " + index;
    }

    public static String createRockQuestion(int index) {
        return "Rock Question " + index;
    }

}
