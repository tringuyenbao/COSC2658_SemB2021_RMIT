public class Coordinate {
    private final int x;
    private final int y;
    public Coordinate parent;

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
        this.parent = null;
    }

    public Coordinate(int x, int y, Coordinate parent) {
        this.x = x;
        this.y = y;
        this.parent = parent;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Coordinate getParent() {
        return parent;
    }

    public boolean equals(Coordinate coord) {
        return coord.getX() == this.x && coord.getY() == this.y;
    }

//    @Override
//    public String toString() {
//        return "Coordinate X, Y: " + this.x + ", " + this.y;
//    }

    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }
}