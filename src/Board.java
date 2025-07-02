import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.net.*;

public class Board {
    Socket MyClient;
    BufferedInputStream input;
    BufferedOutputStream output;
    int[][] board = new int[8][8];

    public Board() {
        try {
            MyClient = new Socket("localhost", 8888);

            input = new BufferedInputStream(MyClient.getInputStream());
            output = new BufferedOutputStream(MyClient.getOutputStream());
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

            char cmd = 0;

            cmd = (char) input.read();
            System.out.println(cmd);
            // Debut de la partie en joueur rouge
            if (cmd == '1') {
                byte[] aBuffer = new byte[1024];

                int size = input.available();
                // System.out.println("size " + size);
                input.read(aBuffer, 0, size);
                String s = new String(aBuffer).trim();
                System.out.println(s);
                String[] boardValues;
                boardValues = s.split(" ");
                int x = 0, y = 0;
                for (int i = 0; i < boardValues.length; i++) {
                    board[x][y] = Integer.parseInt(boardValues[i]);
                    x++;
                    if (x == 8) {
                        x = 0;
                        y++;
                    }
                }
            }

        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void play(Move m) {
        int r = m.getRow();
        int c = m.getCol();

        if (r < 0 || r > 7 || c < 0 || c > 7) {
            throw new IllegalArgumentException("Coordonnées en dehors du plateau: (" + r + "," + c + ").");
        }

        Direction dir = m.getDirection();

        switch (m.getJeton()) {
            case RedPousse -> {
                if (board[r][c] == 1 || board[r][c] == 2) {
                    throw new IllegalArgumentException(
                            "Mouvement illégal (case occupée par un ennemi): (" + r + "," + c + ").");
                }
                if (dir == Direction.LEFT && r + 2 <= 7 && c + 2 <= 7 &&
                        board[r + 1][c + 1] == 3 && board[r + 2][c + 2] == 4 && board[r][c] == 0) {
                    board[r][c] = transformJeton(m.getJeton());
                } else if (dir == Direction.RIGHT && r + 2 <= 7 && c - 2 >= 0 &&
                        board[r + 1][c - 1] == 3 && board[r + 2][c - 2] == 4 && board[r][c] == 0) {
                    board[r][c] = transformJeton(m.getJeton());
                } else if (dir == Direction.FORWARD && r + 2 <= 7 &&
                        board[r + 1][c] == 3 && board[r + 2][c] == 4 && board[r][c] == 0) {
                    board[r][c] = transformJeton(m.getJeton());
                } else {
                    throw new IllegalArgumentException("Déplacement invalide pour RedPousse.");
                }
            }

            case RedPousseur -> {
                if (board[r][c] == 2) {
                    throw new IllegalArgumentException("Case occupée par un pion noir.");
                }
                if (dir == Direction.LEFT && r + 1 <= 7 && c + 1 <= 7 &&
                        board[r + 1][c + 1] == 4 && (board[r][c] == 0 || board[r][c] == 1)) {
                    board[r][c] = transformJeton(m.getJeton());
                } else if (dir == Direction.RIGHT && r + 1 <= 7 && c - 1 >= 0 &&
                        board[r + 1][c - 1] == 4 && (board[r][c] == 0 || board[r][c] == 1)) {
                    board[r][c] = transformJeton(m.getJeton());
                } else if (dir == Direction.FORWARD && r + 1 <= 7 &&
                        board[r + 1][c] == 4 && (board[r][c] == 0 || board[r][c] == 1)) {
                    board[r][c] = transformJeton(m.getJeton());
                } else {
                    throw new IllegalArgumentException("Déplacement invalide pour RedPousseur.");
                }
            }

            case BlackPousse -> {
                if (board[r][c] == 3 || board[r][c] == 4) {
                    throw new IllegalArgumentException("Mouvement illégal (case ennemie): (" + r + "," + c + ").");
                }
                if (dir == Direction.LEFT && r - 2 >= 0 && c + 2 <= 7 &&
                        board[r - 1][c + 1] == 1 && board[r - 2][c + 2] == 2 && board[r][c] == 0) {
                    board[r][c] = transformJeton(m.getJeton());
                } else if (dir == Direction.RIGHT && r - 2 >= 0 && c - 2 >= 0 &&
                        board[r - 1][c - 1] == 1 && board[r - 2][c - 2] == 2 && board[r][c] == 0) {
                    board[r][c] = transformJeton(m.getJeton());
                } else if (dir == Direction.FORWARD && r - 2 >= 0 &&
                        board[r - 1][c] == 1 && board[r - 2][c] == 2 && board[r][c] == 0) {
                    board[r][c] = transformJeton(m.getJeton());
                } else {
                    throw new IllegalArgumentException("Déplacement invalide pour BlackPousse.");
                }
            }

            case BlackPousseur -> {
                if (board[r][c] == 4) {
                    throw new IllegalArgumentException("Case occupée par une pièce rouge.");
                }
                if (dir == Direction.LEFT && r - 1 >= 0 && c + 1 <= 7 &&
                        board[r - 1][c + 1] == 2 && (board[r][c] == 0 || board[r][c] == 3)) {
                    board[r][c] = transformJeton(m.getJeton());
                } else if (dir == Direction.RIGHT && r - 1 >= 0 && c - 1 >= 0 &&
                        board[r - 1][c - 1] == 2 && (board[r][c] == 0 || board[r][c] == 3)) {
                    board[r][c] = transformJeton(m.getJeton());
                } else if (dir == Direction.FORWARD && r - 1 >= 0 &&
                        board[r - 1][c] == 2 && (board[r][c] == 0 || board[r][c] == 3)) {
                    board[r][c] = transformJeton(m.getJeton());
                } else {
                    throw new IllegalArgumentException("Déplacement invalide pour BlackPousseur.");
                }
            }
        }
    }

    public int transformJeton(Jeton jeton) {
        if (jeton == Jeton.BlackPousse) {
            return 1;
        }
        if (jeton == Jeton.BlackPousseur) {
            return 2;
        }
        if (jeton == Jeton.RedPousse) {
            return 3;
        }
        if (jeton == Jeton.RedPousseur) {
            return 4;
        }

        return 0;
    }

    private boolean matchesJeton(int code, Jeton jeton) {
        return switch (jeton) {
            case RedPousseur -> code == 4;
            case RedPousse -> code == 3;
            case BlackPousseur -> code == 2;
            case BlackPousse -> code == 1;
        };
    }

    public int evaluate() {
        if (redIsWinner()) {
            return 100;
        }
        if (blackIsWinner()) {
            return -100;
        }
        return 0;
    }

    public boolean redIsWinner() {
        for (int i = 0; i < 8; i++) {
            if (board[0][i] == 3) {
                return true;
            }
        }

        return false;
    }

    public boolean blackIsWinner() {
        for (int i = 0; i < 8; i++) {
            if (board[7][i] == 1) {
                return true;
            }
        }

        return false;
    }

    public List<Move> generateValidMoves(Jeton jeton) {
        List<Move> validMoves = new ArrayList<>();

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (!matchesJeton(board[r][c], jeton))
                    continue;

                for (Direction dir : Direction.values()) {
                    int[] destination = computeDestination(r, c, dir, jeton);
                    if (destination == null)
                        continue;

                    int destRow = destination[0];
                    int destCol = destination[1];

                    if (destRow < 0 || destRow > 7 || destCol < 0 || destCol > 7)
                        continue;

                    Move move = new Move(destRow, destCol, dir, jeton, cloneBoard(board));

                    try {
                        play(move); // si le coup est valide...
                        validMoves.add(move);
                        board = move.getOldPosition(); // rétablir le plateau original
                    } catch (IllegalArgumentException e) {
                        // coup invalide, on ignore
                    }
                }
            }
        }

        return validMoves;
    }

    private int[] computeDestination(int r, int c, Direction dir, Jeton jeton) {
        int dr = 0, dc = 0;

        switch (dir) {
            case FORWARD -> dr = (jeton == Jeton.RedPousse || jeton == Jeton.RedPousseur) ? 1 : -1;
            case LEFT -> {
                dr = (jeton == Jeton.RedPousse || jeton == Jeton.RedPousseur) ? 1 : -1;
                dc = 1;
            }
            case RIGHT -> {
                dr = (jeton == Jeton.RedPousse || jeton == Jeton.RedPousseur) ? 1 : -1;
                dc = -1;
            }
        }

        return new int[] { r + dr, c + dc };
    }

    private int[][] cloneBoard(int[][] original) {
        int[][] copy = new int[8][8];
        for (int i = 0; i < 8; i++) {
            System.arraycopy(original[i], 0, copy[i], 0, 8);
        }
        return copy;
    }
}
