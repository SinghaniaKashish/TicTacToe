package client1;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class TicTacToeClient {
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;

    public TicTacToeClient(String serverAddress, int port) {
        try {
            socket = new Socket(serverAddress, port);
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
            playGame();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
    }

    private void playGame() {
        try {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String message = input.readUTF();
                if (message.startsWith("BOARD|")) {
                    displayBoard(message.substring(6));
                } else if (message.equals("YOUR_TURN")) {
                    int move = getMoveFromPlayer(scanner);
                    output.writeUTF(String.valueOf(move));
                } else if (message.equals("INVALID_MOVE")) {
                    System.out.println("Invalid move. Please try again.");
                } else if (message.equals("WIN")) {
                    System.out.println("Congratulations! You won!");
                    break;
                } else if (message.equals("TIE")) {
                    System.out.println("It's a tie! The game is over.");
                    break;
                } else {
                    System.out.println("Game result: " + message);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void displayBoard(String boardString) {
        System.out.println("Current Board Player 1:");
        System.out.println(" " + boardString.charAt(0) + " | " + boardString.charAt(1) + " | " + boardString.charAt(2));
        System.out.println("-----------");
        System.out.println(" " + boardString.charAt(3) + " | " + boardString.charAt(4) + " | " + boardString.charAt(5));
        System.out.println("-----------");
        System.out.println(" " + boardString.charAt(6) + " | " + boardString.charAt(7) + " | " + boardString.charAt(8));
        System.out.println();
    }

    private int getMoveFromPlayer(Scanner scanner) {
        while (true) {
            try {
                System.out.println("Enter your move (1-9): ");
                int move = Integer.parseInt(scanner.nextLine().trim());
                if (move >= 1 && move <= 9) {
                    return move;
                } else {
                    System.out.println("Invalid input. Please enter a number between 1 and 9.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number between 1 and 9.");
            }
        }
    }

    private void closeConnections() {
        try {
            if (socket != null) socket.close();
            if (input != null) input.close();
            if (output != null) output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new TicTacToeClient("localhost", 5555);
    }
}
