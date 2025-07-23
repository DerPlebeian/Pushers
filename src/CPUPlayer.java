import java.util.*;

class CPUPlayer {

    private final int MAX_DEPTH = 5;

    private int numExploredNodes;
    private Color color;
    private Node root = null;

    public CPUPlayer(Color cpu){
        numExploredNodes = 0;
        color = cpu;
    }

    public Move getBestMove(Gameboard gameboard) {
        ArrayList<Move> nextMoves = getNextMoveABOpti(gameboard);
        Collections.shuffle(nextMoves);
        return nextMoves.getFirst();
    }

    public int  getNumOfExploredNodes(){
        return numExploredNodes;
    }

    public void initializeTree(Gameboard board) {
        Color rootColor = Color.RED; // c’est toujours le rouge qui commence
        this.root = new Node(null, null, rootColor, false);

        Color nextColor = Color.RED; // même si le premier noeud est rouge, ses enfants doivent aussi être rouge car la racine ne représente pas un coup
        boolean isBotStarting = (color == Color.RED); // On check si le bot est rouge ou pas pour savoir si c'est isMaximising ou pas

        for (Move move : board.getAllPossibleMove(nextColor)) {
            Node child = new Node(root, move, nextColor, isBotStarting);
            root.getChildren().add(child);
        }
    }

    public void advanceTree(Move move, Gameboard gameboard) {
        if (root == null || root.getChildren() == null) return;
        for (Node child : root.getChildren()) {
            if (child.getMove().equals(move)) {
                root = child;
                return;
            }
        }
        // Si jamais le move n’est pas dans l’arbre, recréer depuis zéro
        Color nextColor = color; // comme la fct est appellé après le coup de l'adversaire, alors le prochain coup est celui du bot
        boolean isMax = true;
        root = new Node(null, null, Color.getEnemyColor(color), false);
        for (Move m : gameboard.getAllPossibleMove(nextColor)) {
            // On re initialise avec tous les premiers coups possibles que le bot peut faire
            Node child = new Node(root, m, nextColor, isMax);
            root.getChildren().add(child);
        }
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

    private ArrayList<Move> getNextMoveABOpti(Gameboard board){
        numExploredNodes = 0;
        ArrayList<Move> bestNextMoves = new ArrayList<>();
        int bestScore = Integer.MIN_VALUE;
        for (Node node : root.getChildren()) {
            Gameboard copyBoard = board.copy();
            Stack<Move> pathFromRoot = node.getPathFromRoot();
            int pathSize = pathFromRoot.size();
            while (!pathFromRoot.empty()){
                copyBoard.play(pathFromRoot.pop());
            }
            int score = alphaBetaOpti(copyBoard, node, Integer.MIN_VALUE, Integer.MAX_VALUE, MAX_DEPTH - pathSize);
            node.setScore(score);

            if (score > bestScore) {
                bestScore = score;
                bestNextMoves.clear();
                bestNextMoves.add(node.getMove());
            } else if (score == bestScore) {
                bestNextMoves.add(node.getMove());
            }
        }
        return bestNextMoves;
    }

    private int alphaBetaOpti(Gameboard board, Node node, int alpha, int beta, int depth) {
        numExploredNodes++;
        if (board.isGameOver() || depth == 0) {
            int eval = board.evaluate(color);
            node.setScore(eval);
            return eval;
        }
        Color currentColor = node.getColor();
        boolean maximizing = node.isMaximizing();
        int bestEval = maximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        ArrayList<Move> possibleMoves = board.getAllPossibleMove(currentColor);

        for (Move move : possibleMoves) {
            Gameboard childBoard = board.copy();
            childBoard.play(move);
            Node child = new Node(node, move, node.getOpponentColor(), !maximizing);
            node.addChild(child);
            int eval = alphaBetaOpti(childBoard, child, alpha, beta, depth - 1);
            child.setScore(eval);
            if (maximizing) {
                bestEval = Math.max(bestEval, eval);
                alpha = Math.max(alpha, eval);
            } else {
                bestEval = Math.min(bestEval, eval);
                beta = Math.min(beta, eval);
            }
            if (beta <= alpha) break;
        }
        node.setScore(bestEval);
        return bestEval;
    }
}

