import java.util.ArrayList;

public class Gameboard {

    // Pousseur : peut avancer devant et en diagonale, peut attaquer un ennemi uniquement en diagonale
    // Poussé : peut avant devant et en diagonale uniquement si un pousseur se trouve derrière lui dans la même direction, peut attaquer un ennemi uniquement en diagonale si un pousseur se trouve derrière lui

    final int EMPTY = 0;
    final int BLACK_PUSHED = 1;
    final int BLACK_PUSHER = 2;
    final int RED_PUSHED = 3;
    final int RED_PUSHER = 4;
    final int BOARD_SIZE = 8;

    private int[][] board;

    public Gameboard(int[][] board) {
        this.board = board;
    }

    public Gameboard() {
        board = new int[BOARD_SIZE][BOARD_SIZE];
        for (int col=0; col<BOARD_SIZE; col++) {
            board[0][col] = BLACK_PUSHER;
        }
        for (int col=0; col<BOARD_SIZE; col++) {
            board[1][col] = BLACK_PUSHED;
        }
        for (int row=2; row<BOARD_SIZE-2; row++) {
            for (int col=0; col<BOARD_SIZE; col++) {
                board[row][col] = EMPTY;
            }
        }
        for (int col=0; col<BOARD_SIZE; col++) {
            board[BOARD_SIZE-2][col] = RED_PUSHED;
        }
        for (int col=0; col<BOARD_SIZE; col++) {
            board[BOARD_SIZE-1][col] = RED_PUSHER;
        }
    }

    // Essayer de faire en sorte que lorsqu'on fait getAllPossibleMove pour les noir par exemple,
    // on joue les noirs, on joue les rouges, et faire en sorte que pas besoin de recalculer tous
    // les coups possibles mais juste ajouter les nouveaux coups issues des coups qu'on vient de jouer
    public ArrayList<Move> getAllPossibleMove(Color color) {
        ArrayList<Move> moves = new ArrayList<>();

        int pusher = color == Color.RED ? RED_PUSHER : BLACK_PUSHER;
        int pushed = color == Color.RED ? RED_PUSHED : BLACK_PUSHED;
        int direction = color == Color.RED ? -1 : 1;

        for(int row = 0; row<BOARD_SIZE; row++) {
            for(int col = 0; col<BOARD_SIZE; col++) {
                int piece = board[row][col];
                // Si la pièce est un pousseur de notre couleur
                if(piece == pusher) {
                    int newRow = row+direction;
                    int newCol;
                    // On peut toujours aller à gauche ou à droite sauf si c'est des alliés
                    // On regarde à gauche
                    newCol = col-1;
                    if(isInBoard(newRow, newCol) && board[newRow][newCol] != pusher && board[newRow][newCol] != pushed) {
                        moves.add(new Move(row, col, newRow, newCol));
                    }
                    // On regarde à droite
                    newCol = col+1;
                    if(isInBoard(newRow, newCol) && board[newRow][newCol] != pusher && board[newRow][newCol] != pushed) {
                        moves.add(new Move(row, col, newRow, newCol));
                    }
                    // On peut aller devant uniquement si la case est vide
                    // On regarde just devant
                    if(isInBoard(newRow, col) && board[newRow][col] == EMPTY) {
                        moves.add(new Move(row, col, newRow, col));
                    }
                } else if (piece == pushed) {
                    int rowPusher = row-direction;
                    int newRow = row+direction;
                    int colPusher, newCol;
                    // On regarde derrière le poussé pour voir si il y a un pousseur
                    // Si il y a un pousseur, on regarde les cases devant voir si il y a soit un ennemi en diagonale, soit rien devant
                    // On regarde si il y a un pousseur à gauche pour aller à droite
                    colPusher = col-1;
                    newCol = col+1;
                    if(isInBoard(rowPusher, colPusher) && isInBoard(newRow, newCol) && board[rowPusher][colPusher] == pusher && board[newRow][newCol] != pusher && board[newRow][newCol] != pushed) {
                        moves.add(new Move(row, col, newRow, newCol));
                    }
                    // On regarde si il y a un pousseur à droite pour aller à gauche
                    colPusher = col+1;
                    newCol = col-1;
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
                    score += 10;
                    score += (color == Color.RED ? 7-row : row);
                } else if (piece == pushed) {
                    score += 5;
                    score += (color == Color.RED ? 7-row : row);
                } else if (piece == enemyPusher) {
                    noEnemyLeft = false;
                    score -= 10;
                    score -= (color == Color.RED ? row : 7-row);
                } else if (piece == enemyPushed) {
                    noEnemyLeft = false;
                    score -= 5;
                    score -= (color == Color.RED ? row : 7-row);
                }
            }
        }
        return noEnemyLeft ? Integer.MAX_VALUE : score;
    }

    public Color pieceArrived() {
        for(int col=0; col<BOARD_SIZE; col++) {
            if(board[0][col] == BLACK_PUSHER || board[0][col] == BLACK_PUSHED) {
                return Color.BLACK;
            }
            if(board[BOARD_SIZE-1][col] == RED_PUSHER || board[BOARD_SIZE-1][col] == RED_PUSHED) {
                return Color.RED;
            }
        }
        return null;
    }

    public boolean isGameOver() {
        // On vérifie si un des deux joueurs a perdu tous ses pousseurs
        boolean redPusher = false;
        boolean blackPusher = false;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (board[row][col] == RED_PUSHER) redPusher = true;
                if (board[row][col] == BLACK_PUSHER) blackPusher = true;
            }
        }
        return pieceArrived() != null || !redPusher || !blackPusher;
    }

    public void play(Move move) {
        int piece = this.board[move.getFromRow()][move.getFromCol()];
        this.board[move.getFromRow()][move.getFromCol()] = EMPTY;
        this.board[move.getToRow()][move.getToCol()] = piece;
    }

    public Gameboard copy() {
        int[][] newBoard = new int[8][8];
        for (int row=0; row<BOARD_SIZE; row++) {
            System.arraycopy(board[row], 0, newBoard[row], 0, BOARD_SIZE);
        }
        return new Gameboard(newBoard);
    }
}

