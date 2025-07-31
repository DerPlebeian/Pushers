import java.util.ArrayList;
import java.util.Stack;

public class Node {

    private Node parent;
    private Move move;
    private int score;
    private ArrayList<Node> children;
    private Color color;
    private boolean isMaximizing;

    public Node(Node parent, Move move, Color color, boolean isMaximizing) {
        this.parent = parent;
        this.move = move;
        this.color = color;
        this.isMaximizing = isMaximizing;
        this.children = new ArrayList<>();
        this.score = -11111; // On considère ce score comme le score par défaut, si le score = -11111 alors il n'a pas été défini
    }

    public ArrayList<Node> getChildren() {
        return children;
    }

    public Node getParent() {
        return parent;
    }

    public Move getMove() {
        return move;
    }

    public Color getColor() {
        return color;
    }

    public boolean isMaximizing() {
        return isMaximizing;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Stack<Move> getPathFromRoot() {
        Stack<Move> path = new Stack<>();
        Node current = this;
        while (current != null && current.getMove() != null) {
            path.push(current.getMove());
            current = current.getParent();
        }
        return path;
    }

    public void addChild(Node child) {
        children.add(child);
    }
}


