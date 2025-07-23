import java.util.Objects;

public class Move {

    private byte fromRow;
    private byte fromCol;
    private byte toRow;
    private byte toCol;

    public Move(byte fromRow, byte fromCol, byte toRow, byte toCol) {
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
    }

    public byte getFromRow() {
        return fromRow;
    }

    public byte getFromCol() {
        return fromCol;
    }

    public byte getToRow() {
        return toRow;
    }

    public byte getToCol() {
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
        byte fromCol = (byte) (s.charAt(0) - 'A');
        byte fromRow = (byte) (8 - Character.getNumericValue(s.charAt(1)));
        byte toCol = (byte) (s.charAt(2) - 'A');
        byte toRow = (byte) (8 - Character.getNumericValue(s.charAt(3)));
        return new Move(fromRow, fromCol, toRow, toCol);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Move move = (Move) obj;
        return fromRow == move.getFromRow() && fromCol == move.getFromCol() && toRow == move.getToRow() && toCol == move.getToCol();
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromRow, fromCol, toRow, toCol);
    }
}
