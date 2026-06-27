import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class NumberGuessingGame {

    private static final Random RANDOM = new Random();
    private static final Scanner IN = new Scanner(System.in);
    private static final Set<Integer> GUESSES = new HashSet<>();

    private static int lowestAttemptInEasy = 11;
    private static int lowestAttemptInMed  = 6;
    private static int lowestAttemptInHard = 4;
    private static boolean gameIsRunning = true;

    private static void showMenu() {
        System.out.println("1. Easy");
        System.out.println("2. Medium");
        System.out.println("3. Hard");
        System.out.println("4. Quit game");
    }

    private static void handleChoice(String choice) {
        switch (choice) {
            case "1" -> startGame(10);
            case "2" -> startGame(5);
            case "3" -> startGame(3);
            case "4" -> quit();
            default -> System.out.println("Wrong input!");
        }
    }

    private static Thread timer(AtomicInteger timeConsumed) {
        return new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Thread.sleep(1000);
                    timeConsumed.incrementAndGet();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    private static void startGame(int attempts) {
        int initialAttempts = attempts;
        int numberToGuess   = RANDOM.nextInt(1, 31);
        boolean guessed     = false;

        AtomicInteger seconds = new AtomicInteger();
        Thread timeConsumed   = timer(seconds);

        System.out.println(numberToGuess);

        timeConsumed.start();

        while (true) {
            System.out.print("Enter number guess here: ");
            String guess = IN.nextLine();

            boolean hasNonNumber = guess.chars()
                    .mapToObj(ch -> (char) ch)
                    .anyMatch(ch -> !Character.isDigit(ch));

            if (guess.equalsIgnoreCase("hint")) {
                System.out.println("Hint: " + (numberToGuess % 2 == 0 ? "An even number" : "An odd number"));
                continue;
            }

            if (hasNonNumber) {
                System.out.println("Enter numerical characters only!");
                continue;
            }

            int intGuess = Integer.parseInt(guess);

            if (!GUESSES.add(intGuess) && intGuess != numberToGuess) {
                System.out.println("You already guessed this number! Try again!\n Attempts left: " + --attempts);
                continue;
            }

            if (intGuess != numberToGuess) {
                System.out.println("Oops! Wrong number! Attempts left: " + --attempts);
            }

            if (intGuess == numberToGuess) {
                guessed = true;
                break;
            }

            if (attempts == 0) break;
        }

        timeConsumed.interrupt();

        int attemptsItTook = Math.abs(attempts - initialAttempts) + 1;

        if (guessed) {
            System.out.println("Congrats! You guessed the right number!");

            if (attemptsItTook < lowestAttemptInEasy && initialAttempts == 10) {
                lowestAttemptInEasy = attemptsItTook;
                System.out.println("New fewest attempts (" + lowestAttemptInEasy + ") in Easy mode! Congrats!");
            } else if (attemptsItTook < lowestAttemptInMed && initialAttempts == 5) {
                lowestAttemptInMed = attemptsItTook;
                System.out.println("New fewest attempts (" + lowestAttemptInMed + ") in Medium mode! Congrats!");
            } else if (attemptsItTook < lowestAttemptInHard && initialAttempts == 3) {
                lowestAttemptInHard = attemptsItTook;
                System.out.println("New fewest attempts (" + lowestAttemptInHard + ") in Hard mode! Congrats!");
            }

            System.out.println("Total attempts: " + attemptsItTook);
            System.out.println("Total time it takes to guess: " + seconds.get() + "s");
        } else {
            System.out.println("Oops! You did not guessed the right number! Right number + " + numberToGuess);
            System.out.println("Try Again! ;)");
        }

        System.out.print("\nDo you want to try again (Y/N) ? ");
        String decision = IN.nextLine();

        if (decision.equalsIgnoreCase("N")) {
            System.out.println("Thanks for playing my game! ;)\n");
            System.exit(0);
        }

        System.out.println();
    }

    public static void quit() {
        System.out.println("Thanks for playing my game! ;)");
        gameIsRunning = false;
    }

    public static void main(String[] args) {
        while (gameIsRunning) {
            System.out.println("""
                    Welcome to the Number Guessing Game, Player!
                    I will pick a number between 1-30. GUESS IT!""");
            showMenu();
            System.out.print("Enter choice here: ");
            handleChoice(IN.nextLine());
        }
    }
}
