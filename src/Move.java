public class Move {

    private int fromRow;
    private int fromCol;
    private int toRow;
    private int toCol;

    public Move(int fromRow, int fromCol, int toRow, int toCol) {
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
    }

    public int getFromRow() {
        return fromRow;
    }

    public int getFromCol() {
        return fromCol;
    }

    public int getToRow() {
        return toRow;
    }

    public int getToCol() {
        return toCol;
    }


    @Override
    public String toString() {
        return convertNumberToLetter(fromCol) + (8-fromRow) + "-" + convertNumberToLetter(toCol) + (8-toRow);
    }

    private String convertNumberToLetter(int i) {
        switch (i) {
            case 0:
                return "A";
            case 1:
                return "B";
            case 2:
                return "C";
            case 3:
                return "D";
            case 4:
                return "E";
            case 5:
                return "F";
            case 6:
                return "G";
            case 7:
                return "H";
            default:
                return "";
        }
    }

    public static Move getMoveFromString(String s) {
        s = s.trim().toUpperCase().replaceAll("[\\s\\-]", ""); // retire espaces et tirets
        int fromCol = s.charAt(0) - 'A';
        int fromRow = 8 - Character.getNumericValue(s.charAt(1));
        int toCol = s.charAt(2) - 'A';
        int toRow = 8 - Character.getNumericValue(s.charAt(3));
        return new Move(fromRow, fromCol, toRow, toCol);
    }
}
