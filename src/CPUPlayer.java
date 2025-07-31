import java.util.*;
import java.util.concurrent.TimeoutException;

class CPUPlayer {

    private final int MAX_DEPTH = 5;

    private int numExploredNodes;
    private Color color;
    private Node root = null;
    private final static long TIMELIMIT = 4500; // 4,5 secondes pour reflechir

    public CPUPlayer(Color cpu){
        numExploredNodes = 0;
        color = cpu;
    }

    public Move getBestMove(Gameboard gameboard) {
        Move nextMove = getBestMoveWithTimeLimit(gameboard);
        return nextMove;
    }

    public int  getNumOfExploredNodes(){
        return numExploredNodes;
    }

    private ArrayList<Move> getNextMoveMinMax(Gameboard board) {
        numExploredNodes = 0;
        ArrayList<Move> bestNextMoves = new ArrayList<>();
        int bestScore = Integer.MIN_VALUE;
        for (Move move : board.getAllPossibleMove(color)) {
            Gameboard copyBoard = board.copy();
            copyBoard.play(move);
            int score = minMax(copyBoard, false, MAX_DEPTH);
            if (score > bestScore) {
                bestScore = score;
                bestNextMoves.clear();
                bestNextMoves.add(move);
            } else if (score == bestScore) {
                bestNextMoves.add(move);
            }
        }
        return bestNextMoves;
    }

    private int minMax(Gameboard board, boolean isMaximum, int depth) {
        numExploredNodes++;
        if(board.isGameOver() || depth == 0) {
            return board.evaluate(color);
        }
        int bestScore = isMaximum ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        Color currentColor = isMaximum ? color : (color == Color.RED ? Color.BLACK : Color.RED);
        for (Move move : board.getAllPossibleMove(currentColor)) {
            Gameboard copyBoard = board.copy();
            copyBoard.play(move);
            int score = minMax(copyBoard, !isMaximum, depth-1);
            bestScore = isMaximum ? Math.max(bestScore, score) : Math.min(bestScore, score);
        }
        return bestScore;
    }

    private ArrayList<Move> getNextMoveAB(Gameboard board){
        numExploredNodes = 0;
        ArrayList<Move> bestNextMoves = new ArrayList<>();
        int bestScore = Integer.MIN_VALUE;
        for (Move move : board.getAllPossibleMove(color)) {
            System.out.print(move + " | ");
            Gameboard copyBoard = board.copy();
            copyBoard.play(move);
            int score = alphaBeta(copyBoard, false, Integer.MIN_VALUE, Integer.MAX_VALUE, MAX_DEPTH);
            if (score > bestScore) {
                bestScore = score;
                bestNextMoves.clear();
                bestNextMoves.add(move);
            } else if (score == bestScore) {
                bestNextMoves.add(move);
            }
        }
        return bestNextMoves;
    }

    private int alphaBeta(Gameboard board, boolean isMaximum, int alpha, int beta, int depth) {
        numExploredNodes++;
        if (board.isGameOver() || depth == 0) {
            return board.evaluate(color);
        }
        Color currentColor = isMaximum ? color : (color == Color.RED ? Color.BLACK : Color.RED);
        if (isMaximum) {
            int maxEval = Integer.MIN_VALUE;
            for (Move move : board.getAllPossibleMove(currentColor)) {
                Gameboard copy = board.copy();
                copy.play(move);
                int eval = alphaBeta(copy, false, alpha, beta, depth-1);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) break; // Coupe
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Move move : board.getAllPossibleMove(currentColor)) {
                Gameboard copy = board.copy();
                copy.play(move);
                int eval = alphaBeta(copy, true, alpha, beta, depth-1);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) break; // Coupe
            }
            return minEval;
        }
    }

    public void initializeTree(Gameboard gameboard) {
        Color startingPlayer = Color.RED;
        this.root = new Node(null, null, startingPlayer, true);
        generateChildren(root, gameboard);
    }

    public void advanceTree(Move playedMove, Gameboard gameboard) {
        for (Node child : root.getChildren()) {
            System.out.print(child.getMove() + " | ");
            if (child.getMove().equals(playedMove)) {
                root = child;
                generateChildren(root, gameboard);
                return;
            }
        }
        // Si le coup n'est pas trouvé parmi les enfants, on redémarre l'arbre
        System.out.println("Move non trouvé parmi les enfants du noeud courant : " + playedMove);
        initializeTree(gameboard);
    }

    private void generateChildren(Node node, Gameboard gameboard) {
        Color nextPlayer = Color.getEnemyColor(node.getColor());
        for (Move move : gameboard.getAllPossibleMove(nextPlayer)) {
            Node child = new Node(node, move, nextPlayer, !node.isMaximizing());
            node.addChild(child);
        }
    }

    public Move getBestMoveWithTimeLimit(Gameboard gameboard) {
        long startTime = System.currentTimeMillis();
        long deadline = startTime + TIMELIMIT;
        Move bestMove = null;
        int depth = 1;

        while (System.currentTimeMillis() < deadline) {
            try {
                Move move = alphaBetaWithTimeLimit(gameboard, depth, deadline);
                if (move != null) bestMove = move;
            } catch (TimeoutException e) {
                break;
            }
            depth++;
        }

        if (bestMove == null) {
            System.out.println("Aucun meilleur coup trouvé");
            List<Move> allMoves = gameboard.getAllPossibleMove(color);
            return allMoves.isEmpty() ? null : allMoves.get(0);
        }

        return bestMove;
    }

    private Move alphaBetaWithTimeLimit(Gameboard gameboard, int depth, long deadline) throws TimeoutException {
        int bestScore = Integer.MIN_VALUE;
        Move bestMove = null;
        List<Move> moves = gameboard.getAllPossibleMove(color);

        for (Move move : moves) {
            Gameboard clone = gameboard.copy();
            clone.play(move);
            int score = alphaBetaRecu(clone, depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, false, deadline);

            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }

        return bestMove;
    }

    private int alphaBetaRecu(Gameboard gameboard, int depth, int alpha, int beta, boolean maximizing, long deadline) throws TimeoutException {
        if (System.currentTimeMillis() >= deadline) throw new TimeoutException();

        if (depth == 0 || gameboard.isGameOver()) {
            return gameboard.evaluate(color);
        }

        List<Move> moves = gameboard.getAllPossibleMove(maximizing ? color : Color.getEnemyColor(color));

        if (maximizing) {
            int maxEval = Integer.MIN_VALUE;
            for (Move move : moves) {
                Gameboard clone = gameboard.copy();
                clone.play(move);
                int eval = alphaBetaRecu(clone, depth - 1, alpha, beta, false, deadline);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) break;
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Move move : moves) {
                Gameboard clone = gameboard.copy();
                clone.play(move);
                int eval = alphaBetaRecu(clone, depth - 1, alpha, beta, true, deadline);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) break;
            }
            return minEval;
        }
    }
}

