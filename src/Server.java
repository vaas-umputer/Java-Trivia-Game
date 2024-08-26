import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Server {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(5555);
            System.out.println("Server started. Waiting for players to connect...");

            Game game = new Game();

            // Wait for two players to connect
            Socket player1Socket = serverSocket.accept();
            System.out.println("Player 1 connected.");
            Socket player2Socket = serverSocket.accept();
            System.out.println("Player 2 connected.");
            Socket player3Socket = serverSocket.accept();
            System.out.println("Player 3 connected.");
            Socket player4Socket = serverSocket.accept();
            System.out.println("Player 4 connected.");

            // Start a new thread for each player
            Thread player1Thread = new PlayerThread(player1Socket, "Player 1", game);
            Thread player2Thread = new PlayerThread(player2Socket, "Player 2", game);
            Thread player3Thread = new PlayerThread(player3Socket, "Player 3", game);
            Thread player4Thread = new PlayerThread(player4Socket, "Player 4", game);

            player1Thread.start();
            player2Thread.start();
            player3Thread.start();
            player4Thread.start();
            player1Thread.join();
            player2Thread.join();
            player3Thread.join();
            player4Thread.join();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class PlayerThread extends Thread {
    private Socket socket;
    private String playerName;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private Game game;
    private List<String> questions;
    private List<List<String>> options;
    private List<String> correctAnswers;
    String[] questionsA;

    public PlayerThread(Socket socket, String playerName, Game game) {
        this.socket = socket;
        this.playerName = playerName;
        this.game = game;

        try {
            this.output = new ObjectOutputStream(socket.getOutputStream());
            this.input = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        initializeQuestions();
    }

    private void initializeQuestions() {
        questions = new ArrayList<>();
        options = new ArrayList<>();
        correctAnswers = new ArrayList<>();

        this.questionsA = new String[]{
                "What is the default value of an instance variable in Java if it is not explicitly initialized?",
                "Which keyword is used to prevent a method from being overridden in Java?",
                "What is the purpose of the `break` statement in a loop?",
                "Which data type is used to store a single character in Java?",
                "What does the `public static void main(String[] args)` method indicate in Java?",
                "In Java, what is the purpose of the `super` keyword?",
                "Which statement is used to handle exceptions in Java?",
                "What is the difference between `ArrayList` and `LinkedList` in Java?",
                "Which operator is used to compare two values for equality in Java?",
                "What is the purpose of the `this` keyword in Java?"};

        questions.addAll(Arrays.asList(questionsA));

        String[][] optionsA = {
                {"0", "null", "Depends on the data type", "Compiler error"},
                {"final", "static", "abstract", "private"},
                {"To exit the loop", "To skip the current iteration", "To continue to the next iteration", "To terminate the program"},
                {"int", "char", "String", "byte"},
                {"It is the entry point of the program", "It is a standard method for printing to the console", "It is used to declare variables", "It is a reserved method for mathematical operations"},
                {"Refers to the current instance", "Calls the superclass method", "Creates a new object", "Declares a constant"},
                {"try-catch", "if-else", "switch", "for"},
                {"`ArrayList` is synchronized, and `LinkedList` is not.", "`LinkedList` allows faster random access.", "`ArrayList` is more memory-efficient.", "`LinkedList` is implemented as a resizable array."},
                {"==", "=", "!=", "==="},
                {"Refers to the superclass object", "Calls a method in the superclass", "Refers to the current object", "Declares a constant"}
        };

        for (String[] option : optionsA) {
            List<String> optionsList = Arrays.asList(option);
            options.add(optionsList);
        }

        String[] correct = {"c", "a", "a", "b", "a", "b", "a", "c", "a", "c"};
        correctAnswers.addAll(Arrays.asList(correct));



        // Add questions, options, and correct answers
        /*questions.add("What is the capital of France?");
        options.add(List.of("Berlin", "Paris", "London", "Madrid"));
        correctAnswers.add(0);

        questions.add("Which planet is known as the Red Planet?");
        options.add(List.of("Mars", "Jupiter", "Earth", "Venus"));
        correctAnswers.add(1);

        questions.addA

        // Add more questions as needed*/
    }

    @Override
    public void run() {
        try {
            output.writeObject("Welcome, " + playerName + "!");
            output.flush();

            while (!game.isGameOver()) {
                synchronized (game) {
                    while (!playerName.equals(game.getCurrentPlayer())) {
                        System.out.println("waiting");
                        game.wait();
                    }

                    if (!game.isGameOver()) {
                        takeTurn();
                        game.switchPlayer();
                        game.notify();
                    }
                }
            }

            output.writeObject("Game Over");
            output.flush();
            socket.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private synchronized void takeTurn() throws IOException {
        //boolean playerCanMove = false;
        try {
            int randomIndex = new Random().nextInt(questions.size());
            //System.out.println(randomIndex);
            String question = questions.get(randomIndex);
            List<String> questionOptions = options.get(randomIndex);
            String correctAnswerIndex = correctAnswers.get(randomIndex);



            Question questionObj = new Question(question, questionOptions);
            output.writeObject(questionObj);
            output.flush();

            String selectedOption = (String)input.readObject();

            if (selectedOption.equals(correctAnswerIndex)){
                output.writeObject("Correct! Well done.");
            } else {
                output.writeObject("Incorrect. The correct answer is: " + correctAnswerIndex);
            }

            output.writeObject("TurnComplete");
            output.flush();
            //return;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }


}

class Game {
    private String currentPlayer;
    private int turnCount;

    private final int maxTurnsPerPlayer = 2;

    public Game() {
        this.currentPlayer = "Player 1";
        this.turnCount = 0;
    }

    public synchronized String getCurrentPlayer() {
        return currentPlayer;
    }

    public synchronized void switchPlayer() {

        if (currentPlayer.equals("Player 1")) {
            currentPlayer = "Player 2";
        } else if(currentPlayer.equals("Player 2")) {
            currentPlayer = "Player 3";
        }else if(currentPlayer.equals("Player 3")) {
            currentPlayer = "Player 4";
        }else {
            currentPlayer = "Player 1";
            turnCount++;
        }

    }

    public synchronized boolean isGameOver() {
        return turnCount >= maxTurnsPerPlayer * 2; // 2 players, 2 turns each
    }

    public synchronized int getTurnCount() {
        return turnCount;
    }
}
