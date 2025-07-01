public class Move {
    private int row;
    private int col;
    private Direction direction;
    private Jeton jeton;
    private int[][] oldPosition;

    public Move(){}

    public Move(int r, int c, Direction direction, Jeton j, int[][] oP){
        row = r;
        col = c;
        this.direction = direction;
        jeton = j;
        oldPosition = oP;
    }

    public int getRow(){
        return row;
    }

    public int getCol(){
        return col;
    }

    public void setRow(int r){
        row = r;
    }

    public void setCol(int c){
        col = c;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Jeton getJeton() {
        return jeton;
    }

    public void setJeton(Jeton jeton) {
        this.jeton = jeton;
    }

    public int[][] getOldPosition() {
        return oldPosition;
    }

    public void setOldPosition(int[][] oldPosition) {
        this.oldPosition = oldPosition;
    }

    
}
