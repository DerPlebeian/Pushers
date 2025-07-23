public enum Color {
    BLACK,
    RED;

    public static Color getEnemyColor(Color color) {
        return color == RED ? BLACK : RED;
    }
}

