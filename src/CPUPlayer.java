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

    /**
     * Initialise l'arbre lors de la création du gameboard
     * @param gameboard état du jeu
     */
    public void initializeTree(Gameboard gameboard) {
        Color startingPlayer = Color.RED;
        this.root = new Node(null, null, startingPlayer, true);
        generateChildren(root, gameboard);
    }

    /**
     * Permet d'avance dans l'arbre
     * On va chercher parmi les enfants du noeud courant le coup playedMove afin de le définir comme nouveau noeud courant
     * @param playedMove move recherché
     * @param gameboard gameboard courant
     */
    public void advanceTree(Move playedMove, Gameboard gameboard) {
        for (Node child : root.getChildren()) {
            System.out.print(child.getMove() + " | ");
            if (child.getMove().equals(playedMove)) {
                root = child;
                generateChildren(root, gameboard);
                return;
            }
        }
        // Si le coup n'est pas trouvé parmi les enfants, on refait l'arbre à partir de ce noeud
        System.out.println("Move non trouvé parmi les enfants du noeud courant : " + playedMove);
        initializeTree(gameboard);
    }

    /**
     * Génère tous les coups possibles à partir de l'état du gameboard et les attribuent au noeud Node
     * @param node noeud pour lequel on veut générer les enfants
     * @param gameboard état du jeu pour le noeud
     */
    private void generateChildren(Node node, Gameboard gameboard) {
        Color nextPlayer = Color.getEnemyColor(node.getColor());
        for (Move move : gameboard.getAllPossibleMove(nextPlayer)) {
            Node child = new Node(node, move, nextPlayer, !node.isMaximizing());
            node.addChild(child);
        }
    }

    /**
     * Permet de trouver le meilleur coup possible en imposant une limite de temps
     * @param gameboard état du jeu au moment ou on veut chercher le meilleur coup
     * @return meilleur coup
     */
    public Move getBestMoveWithTimeLimit(Gameboard gameboard) {
        // On initialise le temps
        long startTime = System.currentTimeMillis();
        long endTime = startTime + TIMELIMIT;
        Move bestMove = null;
        int depth = 1;

        // Tant que le temps n'est pas écoulé
        while (System.currentTimeMillis() < endTime) {
            try {
                Move move = alphaBetaWithTimeLimit(gameboard, depth, endTime);
                if (move != null) bestMove = move;
            } catch (TimeoutException e) {
                // Catch l'exception lorsque le temps est dépassé pour stopper la recherche
                break;
            }
            depth++;
        }

        // Si aucun coup n'a été trouvé (dû à un bug) retourne le premier coup possible
        if (bestMove == null) {
            System.out.println("Aucun meilleur coup trouvé");
            List<Move> allMoves = gameboard.getAllPossibleMove(color);
            return allMoves.isEmpty() ? null : allMoves.get(0);
        }

        return bestMove;
    }

    /**
     * Fonction alpha beta pour rechercher le meilleur coup avec implémentation de la limite de temps pour stopper la recherche lorsque le temps est dépassé
     * @param gameboard état du jeu courant
     * @param depth profondeur de recherche
     * @param endTime heure de fin de la recherche
     * @return meilleur coup
     * @throws TimeoutException retourne l'exception lorsque le temps est dépassé
     */
    private Move alphaBetaWithTimeLimit(Gameboard gameboard, int depth, long endTime) throws TimeoutException {
        int bestScore = Integer.MIN_VALUE;
        Move bestMove = null;
        List<Move> moves = gameboard.getAllPossibleMove(color);

        // Pour chaque coup possible
        for (Move move : moves) {
            // On copie l'état du jeu actuel
            Gameboard boardClone = gameboard.copy();
            // On joue le coup
            boardClone.play(move);
            // Et on évalue l'état du plateau
            int score = alphaBetaRecu(boardClone, depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, false, endTime);
            // Si le score est supérieur à l'ancien meilleur coup, on définit le nouveau coup comme meilleur coup
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }
        return bestMove;
    }

    /**
     * Fonction récursive de l'algorithme alphabeta pour évaluer l'état du jeu avec élagage
     * @param gameboard état du jeu
     * @param depth profondeur de recherche
     * @param alpha
     * @param beta
     * @param maximizing définit si on cherche le meilleur ou le pire score
     * @param endTime heure de fin de la recherche
     * @return
     * @throws TimeoutException
     */
    private int alphaBetaRecu(Gameboard gameboard, int depth, int alpha, int beta, boolean maximizing, long endTime) throws TimeoutException {
        // Si le temps est dépassé, on déclenche l'exception
        if (System.currentTimeMillis() >= endTime) throw new TimeoutException();

        // Si le jeu est fini, il ne reste aucun coup à joué donc on retourne directement le score du plateau
        if (depth == 0 || gameboard.isGameOver()) {
            return gameboard.evaluate(color);
        }
        // On génère tous les coups possibles
        List<Move> moves = gameboard.getAllPossibleMove(maximizing ? color : Color.getEnemyColor(color));

        // Si on cherche le meilleur score
        if (maximizing) {
            int maxEval = Integer.MIN_VALUE;
            for (Move move : moves) {
                // On va calculer pour chaque coup possible, lequel nous donne le meilleur score
                Gameboard clone = gameboard.copy();
                clone.play(move);
                int eval = alphaBetaRecu(clone, depth - 1, alpha, beta, false, endTime);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                // On élague si alpha est supérieur à beta car on pourra pas trouver un meilleur score dans cette branche
                if (beta <= alpha) break;
            }
            return maxEval;
        } else {
            // On cherche le pire coup pour l'adversaire
            int minEval = Integer.MAX_VALUE;
            for (Move move : moves) {
                // On va calculer pour chaque coup possible, lequel nous donne le pire score
                Gameboard clone = gameboard.copy();
                clone.play(move);
                int eval = alphaBetaRecu(clone, depth - 1, alpha, beta, true, endTime);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                // On élague si alpha est supérieur à beta car on pourra pas trouver un meilleur score dans cette branche
                if (beta <= alpha) break;
            }
            return minEval;
        }
    }
}

