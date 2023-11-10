# Gleamer Technical Interview

## Prerequisites

The exercise has been made using:

- IntelliJ.
- Java 21.
- Gradle.

## Build

```shell
$ ./gradlew build
```

## Run tests

```shell
$ ./gradlew check
```

## Problem statement.

Read [this](./technical-interview-challenge.pdf).

## What the code do ?

### Assumptions

- This is a game.
- Minimum players count is 2.
- Maximum players count is 6.
- 4 categories of questions.
- 50 questions per categories.
- 12 squares in the game.
  - There is the same amount of squares for each categories.
  - 3 squares per categories => 3 * 4 = 12.
- 6 gold coins is necessary to win.

### Rules

- Player (probably) throws a die to determine how many squares to go forward.
- If position of the player is equal or greater than 12, the player goes back of 12 squares.
- Each square contains one question about a category.
- When a question is asked:
  - If answer is correct, the player earns one gold coin.
  - If answer is incorrect, the player goes in the penalty box.
- The player stays in the penalty box until he gets an odd.
- In the penalty box, correct answer does not give gold coin.

## Anomalies

### Language

- [x] No package => probably for the test.

- [x] Mixed field instantiation position.
  - Field declaration, constructor, none.
  - Initialization is made in constructor.

- [x] Raw generics.
  - Why ? => Very old version of Java ?

- [ ] Which methods should be `private`/`public` ?
  - Could be clarified if `Game` implements an interface.

- [x] Which properties should be `private`/`public` ?
  - All properties are private, using getters to expose if necessary.

### Behaviour

- [x] Maximum players count ?
  - Let's assume maximum players is 6 (`int[] places = new int[6]`).
  - Mixed usage of `List` and `Array` => could lead to `IndexOutOfBoundsException`.

- [x] Can players have same name ?
  - Let's assume yes.

- [x] Can player name be `null` or empty ?
  - Let's assume no => throw error.

- [x] Does `void roll(int)` allow `0` or negative number ?
  - Let's assume no => throw error.

- [x] Can we call `int roll(int)` if the game is not ready ?
  - Let's assume no => throw error.

- [x] Function `didPlayerWin()` returns always `true` at first turn.
  - L.9: `int[] purses = new int[6]` inits all values to `0`.
  - L.119: `purses[currentPlayer]++` => is now `1`.
  - L.167: `!(purses[currentPlayer] == 6)` => `!(1 == 6)` => `true` => the player is the winner after the first question
    correctly answered.
  - Let's assume a player wins if he has 6 coins.

- [x] How a player can get out of penalty box ?
  - L.43: `inPenaltyBox[howManyPlayers()] = false` inits player not in penalty box.
  - L.158: `inPenaltyBox[currentPlayer] = true` puts player in penalty box.
  - Let's assume a player can get out if he was already in penalty box, is getting out of penalty box, and answers
    correctly to a question.

- [ ] What to do when running out of questions ?

- [ ] How questions are answered ?
  - `boolean wasCorrectlyAnswered()` returns `true` (L.133) or if the player has reach 6 coins (L.125,147). Which is
    unrelated to the validity of the question's answer.
  - `boolean wrongAnswer()` always returns `true`.

- No tests on legacy code.
  - Way harder to understand expected behaviour, to maintain and to fix !

## Suggestion to make it maintainable

### Readability

- Homogenize code style.
  - Spacing.
  - Line breaks.
  - Brackets on single line `if`.

- Simplify conditionals.
  - Use `==` instead of `!=` when possible => invert condition.
  - Use of `switch (...)` instead of multiple `if (...)`.
  - Extract complex conditions into variable with explicit name.

- Better namings.
  - `add(String)` => `addPlayer(String)`.
  - `isPlayable()` => `isReadyToPlay()`.
  - `howManyPlayers()` => `getPlayersCount()`.
  - `currentCategory()` => `getCurrentCategory()`.
  - `place` => `position`.
  - `purse` => `coins`.

- Consistent namings.
  - `boolean wasCorrectlyAnswered()`/`boolean wrongAnswer()`.

- Avoid repeated inlined function calls, it's ok to have local variable.
  - `players.get(currentPlayer)` => `String currentPlayerName = players.get(currentPlayer)`

- Too much inlined function calls make code harder to read.
  - Avoid things
    like `doThis(onThat("Param  " + computeThis(123).andThen(true)), map.computeIfAbsent("key", k -> computeThat(k)))`

- Opt for vertical code over horizontal code.
  - Reduce nested conditionals and `try...catch`.
  - Remove unnecessary conditional branches (i.e. invert conditions when possible).
  - Early return / Fail fast pattern.

- Use [Lombok](https://projectlombok.org/) to avoid writing loggers and accessors.

### Architecture

- Instantiate class properties in one place.
  - Move properties instantiation in constructor.

- Models with clear purpose.
  - `Game`: In charge of the rules.
  - `QuestionBuilder`: Build questions.
  - `Category`: Enum for type of questions.
  - `Player`: Handle players properties (name, location, coins...).
  - `PlayersList`: Handle all players and turn.
  - `QuestionsDeck`: Handle all questions.

- Reduce duplicated code.
  - Create precise and concise functions for repetitive behaviour.
  - Define reusable constants (strings, magic numbers...).
  - Create`enum`.

- Logging.
  - Good log messages may replace comments.
  - Logger framework instead of `System.out`.
  - Avoid string concatenation in log messages.

### Evolution

- How about adding new question categories ?
  - It seems categories follows the same order on game locations.
  - Adding a new category implies to add more squares.
  - Assuming categories in enum `Category` are declared in same order as they are encountered in the
    game, `getCurrentCategory` can be refactored to handle any amount of squares and categories.

- How about allowing more players ?
  - Uses only `List` instead of arrays to handle any number of players.

### Language

- Do not use raw generics on new code.
  - Generics has been introduced since Java 1.5 !

- Without context, prefer usage of interface declaration instead of concrete class.
  - `ArrayList`/`LinkedList` => `List`.

- Migrate regularly to latest Java version (at least latest LTS).
  - `void addLast(E e)`/`E removeFirst()` has been added to `List` interface since Java 21.
