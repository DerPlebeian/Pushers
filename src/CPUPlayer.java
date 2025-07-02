import java.util.ArrayList;

class CPUPlayer {

    private final int MAX_DEPTH = 8;

    private int numExploredNodes;
    private Color color;

    public CPUPlayer(Color cpu){
        numExploredNodes = 0;
        color = cpu;
    }

    public int  getNumOfExploredNodes(){
        return numExploredNodes;
    }

    public ArrayList<Move> getNextMoveMinMax(Gameboard board) {
        numExploredNodes = 0;
        ArrayList<Move> bestNextMoves = new ArrayList<>();
        int bestScore = Integer.MIN_VALUE;
        // On parcourt tous les coups possibles de la grille
        for (Move move : board.getAllPossibleMove(color)) {
            // Pour chaque coup, on copie la grille
            Gameboard copyBoard = board.copy();
            // Et on joue le coup en question
            copyBoard.play(move);
            // On calcule le score de la grille après avoir joué ce coup en inversant le joueur
            int score = minMax(copyBoard, false, MAX_DEPTH);
            // Si notre nouveau score est meilleur que notre ancien meilleur score
            if (score > bestScore) {
                // On remplace notre ancien meilleur score par le nouveau score
                bestScore = score;
                // On vide le tableau contenant les coups permettant de réaliser l'ancien meilleur score
                bestNextMoves.clear();
                // On ajoute le coup permettant de réaliser le nouveau meilleur score
                bestNextMoves.add(move);
            } else if (score == bestScore) {
                // On a trouver un coup permettant d'arriver au même score que notre meilleur score donc on ajoute just ce coup dans la liste des meilleurs coups
                bestNextMoves.add(move);
            }
        }
        return bestNextMoves;
    }

    private int minMax(Gameboard board, boolean isMaximum, int depth) {
        numExploredNodes++;
        // Si la partie est finit après avoir joué ce coup
        if(board.isGameOver() || depth == 0) {
            // On retourne le score de la grille
            return board.evaluate(color);
        }
        // On définit bestScore au minimum ou au maximum en fonction de si on simule le coup de la machine ou du joueur adverse
        int bestScore = isMaximum ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        // On définit le symbole en fonction de si on simule le coup de la machine ou du joueur adverse
        Color currentColor = isMaximum ? color : (color == Color.RED ? Color.BLACK : Color.RED);
        // Pour chaque nouveau coup possible
        for (Move move : board.getAllPossibleMove(currentColor)) {
            // Pour chaque nouveau coup, on copie la grille à nouveau
            Gameboard copyBoard = board.copy();
            // On joue le coup en question
            copyBoard.play(move);
            // Et on recalcule son score
            int score = minMax(copyBoard, !isMaximum, depth-1);
            // On garde le score ou pas en fonction de quelle joueur est en train de gagner
            bestScore = isMaximum ? Math.max(bestScore, score) : Math.min(bestScore, score);
        }
        return bestScore;
    }

    // Retourne la liste des coups possibles.  Cette liste contient
    // plusieurs coups possibles si et seuleument si plusieurs coups
    // ont le même score.
    public ArrayList<Move> getNextMoveAB(Gameboard board){
        numExploredNodes = 0;
        ArrayList<Move> bestNextMoves = new ArrayList<>();
        int bestScore = Integer.MIN_VALUE;
        // On parcourt tous les coups possibles de la grille
        for (Move move : board.getAllPossibleMove(color)) {
            // Pour chaque coup, on copie la grille
            Gameboard copyBoard = board.copy();
            // Et on joue le coup en question
            copyBoard.play(move);
            // On calcule le score de la grille après avoir joué ce coup en inversant le joueur
            int score = alphaBeta(copyBoard, false, Integer.MIN_VALUE, Integer.MAX_VALUE, MAX_DEPTH);
            // Si notre nouveau score est meilleur que notre ancien meilleur score
            if (score > bestScore) {
                // On remplace notre ancien meilleur score par le nouveau score
                bestScore = score;
                // On vide le tableau contenant les coups permettant de réaliser l'ancien meilleur score
                bestNextMoves.clear();
                // On ajoute le coup permettant de réaliser le nouveau meilleur score
                bestNextMoves.add(move);
            } else if (score == bestScore) {
                // On a trouver un coup permettant d'arriver au même score que notre meilleur score donc on ajoute just ce coup dans la liste des meilleurs coups
                bestNextMoves.add(move);
            }
        }
        return bestNextMoves;
    }

    private int alphaBeta(Gameboard board, boolean isMaximum, int alpha, int beta, int depth) {
        numExploredNodes++;
        // Si la partie est déjà finit
        if (board.isGameOver() || depth == 0) {
            // On retourne le score de la grille
            return board.evaluate(color);
        }
        // On définit la couleur en fonction de si on simule le coup de la machine ou du joueur adverse
        Color currentColor = isMaximum ? color : (color == Color.RED ? Color.BLACK : Color.RED);
        // Si on cherche le maximum
        if (isMaximum) {
            // On met le score à la plus petite valeur possible
            int maxEval = Integer.MIN_VALUE;
            // Pour chaque nouveau coup possible
            for (Move move : board.getAllPossibleMove(currentColor)) {
                // Pour chaque nouveau coup, on copie la grille à nouveau
                Gameboard copy = board.copy();
                // Et on joue le coup en question
                copy.play(move);
                // Et on recalcule son score
                int eval = alphaBeta(copy, false, alpha, beta, depth-1);
                // On prend le score le plus grand entre le nouveau score et celui déjà trouvé
                maxEval = Math.max(maxEval, eval);
                // On met à jour alpha avec le meilleur score trouvé
                alpha = Math.max(alpha, eval);
                // Si beta <= alpha, on arrete car on arrivera pas jusque là
                if (beta <= alpha) break; // Coupe
            }
            return maxEval;
        } else {
            // On met le score à la plus grande valeur possible
            int minEval = Integer.MAX_VALUE;
            for (Move move : board.getAllPossibleMove(currentColor)) {
                // Pour chaque nouveau coup, on copie la grille à nouveau
                Gameboard copy = board.copy();
                // Et on joue le coup en question
                copy.play(move);
                // Et on recalcule son score
                int eval = alphaBeta(copy, true, alpha, beta, depth-1);
                // On prend le score le plus petit entre le nouveau score et celui déjà trouvé
                minEval = Math.min(minEval, eval);
                // On met à jour beta avec le plus petit score trouvé
                beta = Math.min(beta, eval);
                // Si beta <= alpha, on arrete car on arrivera pas jusque là
                if (beta <= alpha) break; // Coupe
            }
            return minEval;
        }
    }
}

