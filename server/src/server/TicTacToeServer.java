package server;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TicTacToeServer {
    private ServerSocket serverSocket;
    private Socket player1;
    private Socket player2;
    private DataInputStream inputPlayer1;
    private DataOutputStream outputPlayer1;
    private DataInputStream inputPlayer2;
    private DataOutputStream outputPlayer2;

    private char[] board;
    private int currentPlayer;

    public TicTacToeServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server is running and waiting for players...");
            player1 = serverSocket.accept();
            System.out.println("Player 1 connected.");
            inputPlayer1 = new DataInputStream(player1.getInputStream());
            outputPlayer1 = new DataOutputStream(player1.getOutputStream());

            player2 = serverSocket.accept();
            System.out.println("Player 2 connected.");
            inputPlayer2 = new DataInputStream(player2.getInputStream());
            outputPlayer2 = new DataOutputStream(player2.getOutputStream());

            board = new char[] { '1', '2', '3', '4', '5', '6', '7', '8', '9' };
            currentPlayer = 1;

            playGame();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
    }
private void playGame() {
    try {
        sendBoardToPlayers();

        while (true) {
            int move = getPlayerMove();
            if (isValidMove(move)) {
                updateBoard(move);
                sendBoardToPlayers();

                if (checkWinner()) {
                    sendGameResult("WIN", currentPlayer);
                    break;
                } else if (checkTie()) {
                    sendGameResult("TIE", 0);
                    break;
                }

                switchPlayer();
            } else {
                sendInvalidMove();
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}

private void sendGameResult(String result, int player) throws IOException {
    if (player == 1) {
        outputPlayer1.writeUTF(result);
        outputPlayer2.writeUTF("Sorry, you lose.");
    } else if (player == 2) {
        outputPlayer1.writeUTF("Sorry, you lose.");
        outputPlayer2.writeUTF(result);
    } else {
        outputPlayer1.writeUTF(result);
        outputPlayer2.writeUTF(result);
    }
}


    private void sendBoardToPlayers() throws IOException {
        String boardString = "BOARD|" + new String(board);
        outputPlayer1.writeUTF(boardString);
        outputPlayer2.writeUTF(boardString);
    }

    private int getPlayerMove() throws IOException {
        if (currentPlayer == 1) {
            outputPlayer1.writeUTF("YOUR_TURN");
            return Integer.parseInt(inputPlayer1.readUTF()) - 1; // Convert to 0-based index
        } else {
            outputPlayer2.writeUTF("YOUR_TURN");
            return Integer.parseInt(inputPlayer2.readUTF()) - 1; // Convert to 0-based index
        }
    }

    private boolean isValidMove(int move) {
        return move >= 0 && move < 9 && board[move] == (char) ('1' + move);
    }

    private void updateBoard(int move) {
        char currentPlayerSymbol = (currentPlayer == 1) ? 'X' : 'O';
        board[move] = currentPlayerSymbol;
    }

    private boolean checkWinner() {
        return (checkLine(0, 1, 2) || checkLine(3, 4, 5) || checkLine(6, 7, 8) ||
                checkLine(0, 3, 6) || checkLine(1, 4, 7) || checkLine(2, 5, 8) ||
                checkLine(0, 4, 8) || checkLine(2, 4, 6));
    }

    private boolean checkLine(int pos1, int pos2, int pos3) {
        return (board[pos1] == board[pos2] && board[pos2] == board[pos3]);
    }

    private boolean checkTie() {
        for (int i = 0; i < 9; i++) {
            if (board[i] == (char) ('1' + i)) {
                return false;
            }
        }
        return true;
    }

    private void switchPlayer() {
        currentPlayer = (currentPlayer == 1) ? 2 : 1;
    }

    private void sendInvalidMove() throws IOException {
        if (currentPlayer == 1) {
            outputPlayer1.writeUTF("INVALID_MOVE");
        } else {
            outputPlayer2.writeUTF("INVALID_MOVE");
        }
    }
    private void closeConnections() {
        try {
            if (player1 != null) player1.close();
            if (inputPlayer1 != null) inputPlayer1.close();
            if (outputPlayer1 != null) outputPlayer1.close();

            if (player2 != null) player2.close();
            if (inputPlayer2 != null) inputPlayer2.close();
            if (outputPlayer2 != null) outputPlayer2.close();

            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new TicTacToeServer(5555);
    }
}
