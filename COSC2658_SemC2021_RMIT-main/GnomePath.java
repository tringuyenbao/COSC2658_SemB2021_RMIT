import java.util.ArrayList;

public class GnomePath {
    private ArrayList<Coordinate> path;

    public GnomePath() {
        this.path = new ArrayList<>();
    }

    public GnomePath(int x, int y) {
        this.path = new ArrayList<>();
        this.path.add(new Coordinate(x, y));
    }

    public void add(int x, int y) {
        this.path.add(new Coordinate(x, y));
    }

    public void add(Coordinate coordinate) {
        this.path.add(coordinate);
    }

    public void removePath(int index) {
        this.path.remove(index);
    }

    public ArrayList<Coordinate> getPath() {
        return path;
    }

    public void setPath(ArrayList<Coordinate> path) {
        this.path = path;
    }

    public int size() {
        return this.path.size();
    }

    public Coordinate get(int index) {
        return this.path.get(index);
    }

//    @Override
//    public String toString() {
//        return "GnomePath: { " + path + " }";
//    }
    @Override
    public String toString() {
        return path.toString();
    }
}
