import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static GameMoves gm;  // Declare gm as a static variable
    private static final int SERVER_PORT = 5555;

    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);

            // Connect to the server
            Socket socket = new Socket("localhost", SERVER_PORT);
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

            // Receive welcome message
            String welcomeMessage = (String) input.readObject();
            System.out.println(welcomeMessage);

            // Create and start GUI after connecting to the server
            SwingUtilities.invokeLater(() -> {
                GameMoves gm = new GameMoves();
                gm.setFocusable(true);
                //gm.addKeyListener(gm);
                //gm.addMouseListener(gm);
                JFrame jframe = new JFrame();
                jframe.setBounds(10, 10, 1000, 600);
                jframe.setBackground(new Color(0XB2A4FF));
                jframe.setTitle("LUDO");
                jframe.setResizable(false);
                jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                jframe.add(gm);
                jframe.setVisible(true);
            });

            while (true) {
                // Receive a Question object from the server
                Question questionObj = (Question) input.readObject();
                System.out.println(questionObj.getQuestion());
                System.out.println("Options: " + String.join(", ", questionObj.getOptions()));
                // Update GUI with the received question

                // Get user's answer (this logic should be handled in your GUI)
                System.out.print("Your answer (enter the option number): ");
                String answer = scanner.nextLine();

                //Clear the input buffer
                scanner.nextLine();

                output.writeObject(answer);
                output.flush();

                // Receive and display feedback
                String response = (String) input.readObject();
                System.out.println(response);

                // Wait for the server's signal that the turn is complete
                String turnCompleteSignal = (String) input.readObject();
                if (!turnCompleteSignal.equals("TurnComplete")) {
                    System.out.println("Unexpected signal: " + turnCompleteSignal);
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
