import java.util.ArrayList;
import java.util.Arrays;

public class Gameboard {

    // Pousseur : peut avancer devant et en diagonale, peut attaquer un ennemi uniquement en diagonale
    // Poussé : peut avant devant et en diagonale uniquement si un pousseur se trouve derrière lui dans la même direction, peut attaquer un ennemi uniquement en diagonale si un pousseur se trouve derrière lui

    static final byte EMPTY = 0;
    static final byte BLACK_PUSHED = 1;
    static final byte BLACK_PUSHER = 2;
    static final byte RED_PUSHED = 3;
    static final byte RED_PUSHER = 4;
    static final byte BOARD_SIZE = 8;

    private byte[][] board;

    public Gameboard(byte[][] board) {
        this.board = board;
    }

    public Gameboard() {
        board = new byte[BOARD_SIZE][BOARD_SIZE];
        for (byte col=0; col<BOARD_SIZE; col++) {
            board[0][col] = BLACK_PUSHER;
        }
        for (byte col=0; col<BOARD_SIZE; col++) {
            board[1][col] = BLACK_PUSHED;
        }
        for (byte row=2; row<BOARD_SIZE-2; row++) {
            for (int col=0; col<BOARD_SIZE; col++) {
                board[row][col] = EMPTY;
            }
        }
        for (byte col=0; col<BOARD_SIZE; col++) {
            board[BOARD_SIZE-2][col] = RED_PUSHED;
        }
        for (byte col=0; col<BOARD_SIZE; col++) {
            board[BOARD_SIZE-1][col] = RED_PUSHER;
        }
    }

    // Essayer de faire en sorte que lorsqu'on fait getAllPossibleMove pour les noir par exemple,
    // on joue les noirs, on joue les rouges, et faire en sorte que pas besoin de recalculer tous
    // les coups possibles mais juste ajouter les nouveaux coups issues des coups qu'on vient de jouer
    public ArrayList<Move> getAllPossibleMove(Color color) {
        ArrayList<Move> moves = new ArrayList<>();

        byte pusher = color == Color.RED ? RED_PUSHER : BLACK_PUSHER;
        byte pushed = color == Color.RED ? RED_PUSHED : BLACK_PUSHED;
        byte direction = (byte) (color == Color.RED ? -1 : 1);

        for(byte row = 0; row<BOARD_SIZE; row++) {
            for(byte col = 0; col<BOARD_SIZE; col++) {
                byte piece = board[row][col];
                // Si la pièce est un pousseur de notre couleur
                if(piece == pusher) {
                    byte newRow = (byte) (row+direction);
                    byte newCol;
                    // On peut toujours aller à gauche ou à droite sauf si c'est des alliés
                    // On regarde à gauche
                    newCol = (byte) (col-1);
                    if(isInBoard(newRow, newCol) && board[newRow][newCol] != pusher && board[newRow][newCol] != pushed) {
                        moves.add(new Move(row, col, newRow, newCol));
                    }
                    // On regarde à droite
                    newCol = (byte) (col+1);
                    if(isInBoard(newRow, newCol) && board[newRow][newCol] != pusher && board[newRow][newCol] != pushed) {
                        moves.add(new Move(row, col, newRow, newCol));
                    }
                    // On peut aller devant uniquement si la case est vide
                    // On regarde just devant
                    if(isInBoard(newRow, col) && board[newRow][col] == EMPTY) {
                        moves.add(new Move(row, col, newRow, col));
                    }
                } else if (piece == pushed) {
                    byte rowPusher = (byte) (row-direction);
                    byte newRow = (byte) (row+direction);
                    byte colPusher, newCol;
                    // On regarde derrière le poussé pour voir si il y a un pousseur
                    // Si il y a un pousseur, on regarde les cases devant voir si il y a soit un ennemi en diagonale, soit rien devant
                    // On regarde si il y a un pousseur à gauche pour aller à droite
                    colPusher = (byte) (col-1);
                    newCol = (byte) (col+1);
                    if(isInBoard(rowPusher, colPusher) && isInBoard(newRow, newCol) && board[rowPusher][colPusher] == pusher && board[newRow][newCol] != pusher && board[newRow][newCol] != pushed) {
                        moves.add(new Move(row, col, newRow, newCol));
                    }
                    // On regarde si il y a un pousseur à droite pour aller à gauche
                    colPusher = (byte) (col+1);
                    newCol = (byte) (col-1);
                    if(isInBoard(rowPusher, colPusher) && isInBoard(newRow, newCol) && board[rowPusher][colPusher] == pusher && board[newRow][newCol] != pusher && board[newRow][newCol] != pushed) {
                        moves.add(new Move(row, col, newRow, newCol));
                    }
                    // On regarde si il y a un pousseur derrière pour aller devant
                    if( isInBoard(rowPusher, col) && isInBoard(newRow, col) && board[rowPusher][col] == pusher && board[newRow][col] == EMPTY) {
                        moves.add(new Move(row, col, newRow, col));
                    }
                }
            }
        }
        return moves;
    }

    private boolean isInBoard(int row, int col) {
        return row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE;
    }

    // Fonction d'évaluation
    // +10 pour chaque pousseur et +5 pour chaque poussé
    // Plus une pièce est proche de l'arrivé plus on ajoute un score elevé
    // On vérifie aussi si le jeu est terminé (plus de pièce ennemi ou pièce chez l'adversaire)
    public int evaluate(Color color) {
        // On vérifie qu'il y a pas une pièce dans la zone adverse
        if(pieceArrived() != null) {
            return pieceArrived() == color ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        }

        int score = 0;
        int pusher = color == Color.RED ? RED_PUSHER : BLACK_PUSHER;
        int pushed = color == Color.RED ? RED_PUSHED : BLACK_PUSHED;
        int enemyPusher = color == Color.RED ? BLACK_PUSHER : RED_PUSHER;
        int enemyPushed = color == Color.RED ? BLACK_PUSHED : RED_PUSHED;
        boolean noEnemyLeft = true;

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                int piece = board[row][col];
                if (piece == pusher) {
                    score += 10; // base
                    score += (color == Color.RED ? 7 - row : row); // proximité
                    score += evaluateCaptures(row, col, pusher, pushed, enemyPusher, enemyPushed);
                }
                else if (piece == pushed) {
                    score += 5;
                    score += (color == Color.RED ? 7 - row : row);
                    if (isPushedBlocked(row, col, color)) score -= 2;
                    score += evaluateCaptures(row, col, pusher, pushed, enemyPusher, enemyPushed);
                }
                else if (piece == enemyPusher) {
                    noEnemyLeft = false;
                    score -= 10;
                    score -= (color == Color.RED ? row : 7 - row);
                    score -= evaluateCaptures(row, col, enemyPusher, enemyPushed, pusher, pushed);
                }
                else if (piece == enemyPushed) {
                    noEnemyLeft = false;
                    score -= 5;
                    score -= (color == Color.RED ? row : 7 - row);
                    if (isPushedBlocked(row, col, Color.getEnemyColor(color))) score += 2;
                    score -= evaluateCaptures(row, col, enemyPusher, enemyPushed, pusher, pushed);
                }
            }
        }
        return noEnemyLeft ? Integer.MAX_VALUE : score;
    }

    private int evaluateCaptures(int row, int col , int pusher, int pushed, int enemyPusher, int enemyPushed) {
        int PUSHER_POINT = 20;
        int PUSHED_POINT = 10;

        int piece = board[row][col];
        int direction = (pusher == RED_PUSHER) ? -1 : 1;
        int captureScore = 0;

        if(piece == pusher) {
            if(isInBoard(row+direction, col-1)) {
                if(board[row+direction][col-1]==enemyPushed) captureScore+=PUSHED_POINT;
                if(board[row+direction][col-1]==enemyPusher) captureScore+=PUSHER_POINT;
            }
            if(isInBoard(row+direction, col+1)) {
                if(board[row+direction][col+1]==enemyPushed) captureScore+=PUSHED_POINT;
                if(board[row+direction][col+1]==enemyPusher) captureScore+=PUSHER_POINT;
            }
        } else if (piece == pushed) {
            if(isInBoard(row-direction, col-1) && isInBoard(row+direction, col+1) && board[row-direction][col-1]==pusher) {
                if(board[row+direction][col+1]==enemyPushed) captureScore+=PUSHED_POINT;
                if(board[row+direction][col+1]==enemyPusher) captureScore+=PUSHER_POINT;
            }
            if(isInBoard(row-direction, col+1) && isInBoard(row+direction, col-1) && board[row-direction][col+1]==pusher) {
                if(board[row+direction][col-1]==enemyPushed) captureScore+=PUSHED_POINT;
                if(board[row+direction][col-1]==enemyPusher) captureScore+=PUSHER_POINT;
            }
        }
        return captureScore;
    }

    // Pour une position donnée, regarde si la pièce à cette position est bloqué
    // On appelle cette fonction uniquement pour les pushed car un pusher n'est jamais bloqué
    private boolean isPushedBlocked(int row, int col, Color color) {
        int direction = (color == Color.RED) ? -1 : 1;
        byte pusher = (color == Color.RED) ? RED_PUSHER : BLACK_PUSHER;
        ArrayList<Byte> freeDiagonalCase = (color == Color.RED) ?  new ArrayList<>(Arrays.asList(BLACK_PUSHED,BLACK_PUSHER, EMPTY)) : new ArrayList<>(Arrays.asList(RED_PUSHED,RED_PUSHER, EMPTY));
        // 1 - On check devant
        if(isInBoard(row-direction, col) && isInBoard(row+direction, col) && board[row-direction][col]==pusher && board[row+direction][col]==EMPTY) {
            return false;
        } else if (isInBoard(row-direction, col-1) && isInBoard(row+direction, col+1) && board[row-direction][col-1]==pusher && freeDiagonalCase.contains(board[row+direction][col-1])) {
            // 2 - On check d'un coté
            return false;
        } else if (isInBoard(row-direction, col+1) && isInBoard(row+direction, col-1) && board[row-direction][col+1]==pusher && freeDiagonalCase.contains(board[row+direction][col+1])) {
            // 3 - On check de l'autre coté
            return false;
        }
        return true;
    }


    public Color pieceArrived() {
        for(int col=0; col<BOARD_SIZE; col++) {
            if(board[0][col] == RED_PUSHER || board[0][col] == RED_PUSHED) {
                return Color.RED;
            }
            if(board[BOARD_SIZE-1][col] == BLACK_PUSHER || board[BOARD_SIZE-1][col] == BLACK_PUSHED) {
                return Color.BLACK;
            }
        }
        return null;
    }

    public boolean isGameOver() {
        // On vérifie si un des deux joueurs a perdu tous ses pousseurs
        boolean redPusher = false;
        boolean blackPusher = false;

        int row = 0;
        while (row < BOARD_SIZE && !(redPusher && blackPusher)) {
            int col = 0;
            while (col < BOARD_SIZE && !(redPusher && blackPusher)) {
                int value = board[row][col];
                if (value == RED_PUSHER) redPusher = true;
                if (value == BLACK_PUSHER) blackPusher = true;
                col++;
            }
            row++;
        }
        return pieceArrived() != null || !redPusher || !blackPusher;
    }

    public void play(Move move) {
        byte piece = this.board[move.getFromRow()][move.getFromCol()];
        this.board[move.getFromRow()][move.getFromCol()] = EMPTY;
        this.board[move.getToRow()][move.getToCol()] = piece;
    }

    public Gameboard copy() {
        byte[][] newBoard = new byte[8][8];
        for (int row=0; row<BOARD_SIZE; row++) {
            System.arraycopy(board[row], 0, newBoard[row], 0, BOARD_SIZE);
        }
        return new Gameboard(newBoard);
    }

    public byte[][] getBoard() {
        return board;
    }

    public static Gameboard syncGameboard(String boardString) {
        String[] boardValues = boardString.trim().split(" ");
        byte[][] board = new byte[8][8];
        byte x = 0, y = 0;
        for (String value : boardValues) {
            board[y][x] = Byte.parseByte(value);
            x++;
            if (x == BOARD_SIZE) {
                x = 0;
                y++;
            }
        }
        return new Gameboard(board);
    }

    public void printBoard() {
        for (byte i = 0; i < BOARD_SIZE; i++) {
            for (byte j = 0; j < BOARD_SIZE; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }
}

