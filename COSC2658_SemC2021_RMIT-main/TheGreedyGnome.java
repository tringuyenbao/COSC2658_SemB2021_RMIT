// RMIT University Vietnam
// Course: COSC2658 - Data Structures & Algorithms
// Semester: 2021B
// Assignment: Group Project
// Authors: Quach Gia Vi (3757317), Bui Manh Dai Duong (s3757278), Nguyen Bao Tri (s3749560)
// Created date: 30/11/2021
// Last modified date: 21/12/2021


import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;


public class TheGreedyGnome {
    public int x = 0;
    public int y = 0;
    public int goldGathered = 0;
    public int steps = 0;
    public String[][] map;
    public int row_count;
    public int col_count;
    public String[][] path;
    public boolean[][] visited;
    public String stepsText = "";
//    public boolean isStuck = false;
    public GnomePath gnomePath;

    // down or right directions only
    private static final int[][] DIRECTIONS = { { 0, 1 }, { 1, 0 } };

    // list of all reachable gold locations
    ArrayList<Coordinate> goldLocations = new ArrayList<>();

    // list of all possible combinations of paths of all gold coordinates
    ArrayList<GnomePath> possibleGoldPaths = new ArrayList<>();


    // validation if value is integer
    public static boolean isInteger(String str) { // o(1)
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    // initiate arrays and other stuff
    private void init() {
        try {
            this.path = new String[this.row_count][this.col_count];
            this.visited = new boolean[this.row_count][this.col_count];
            this.gnomePath = new GnomePath();


            // Duong's comment
            //  Time complexity: 0(n)
            for (String[] rows : this.path) Arrays.fill(rows, ".");

            // check if 0,0 position has gold
            // Duong's comment
            //  Time complexity: 0(n log n)
            if (isInteger(this.map[this.y][this.x])) {
                this.goldGathered += Integer.parseInt(this.map[this.y][this.x]);
                this.path[this.y][this.x] = "G";
            } else { // O(1)
                this.path[0][0] = "+";
            }
            this.visited[this.y][this.x] = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // o(n)
    private void reset() {
        this.x = 0;
        this.y = 0;
        this.goldGathered = 0;
        this.steps = 0;
        this.stepsText = "";
//        this.isStuck = false;
        for(String[] rows : this.path) Arrays.fill(rows, ".");  // O(n)
        for(boolean[] rows : this.visited) Arrays.fill(rows, false);  // 0(n)

        // check if 0,0 position has gold
        if (isInteger(this.map[this.y][this.x])) {
            this.goldGathered += Integer.parseInt(this.map[this.y][this.x]); // O(1)
            this.path[this.y][this.x] = "G";
        } else { //0(1)
            this.path[this.y][this.x] = "+";
        }
        this.visited[this.y][this.x] = true;
    }

    // constructor for testing
    public TheGreedyGnome(String[][] map, final int row_count, final int col_count) {
        this.map = map;
        this.row_count = row_count;
        this.col_count = col_count;

        this.init();
    }

    // actual constructor
    public TheGreedyGnome(String filename) {
        this.readMap(filename);
    }

    // open mine map file, validate the data, then create array of the map
    private void readMap(String filename) {
        try {
            File myFile = new File(filename);
            Scanner file = new Scanner(myFile);

            // reads first line
            String row_col = file.nextLine();
            if (!isInteger(row_col.split(" ")[0]) && !isInteger(row_col.split(" ")[1])) {
                throw new Exception("Invalid row and column values.");
            } else { // 0(1)
                this.row_count = Integer.parseInt(row_col.split(" ")[0]);
                this.col_count = Integer.parseInt(row_col.split(" ")[1]);
                this.map = new String[this.row_count][this.col_count];
            }

            int row = 0;
            while (file.hasNext()) { //o(n)
                //  String data = file.nextLine();
                String[] data = file.nextLine().split(" ");
                for (int i = 0; i < data.length; i++) {
                    //   this.map[row][i] = String.valueOf(data.charAt(i)).toUpperCase();
                    this.map[row][i] = String.valueOf(data[i]).toUpperCase();
                }
                row++;
            }
            file.close();

            this.init();
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // check whether given coordinate (x, y) is a valid coordinate or not. o(1)
    private boolean isValidPos(int x, int y) {
        if (x < 0 || x >= col_count || y < 0 || y >= row_count) { // O(n log n )
            return false;
        }
        return !this.map[this.y][this.x].equals("X");
    }

    // display mine map
    public void displayMap(String[][] map) { //o(n*m)
        for (String[] rows : map) { // O(n)
            for (String col : rows) System.out.print(col + " "); // O(M)
            System.out.println();
        }
    }

    // clear visited array
    private void clearAllVisited() {
        for (boolean[] rows : this.visited) Arrays.fill(rows, false);
    } // O(N)

    // check if coordinate is reachable from an x and y coordinate
    // BFS code modified and used from:
    // Source: https://www.baeldung.com/java-solve-maze
    // Author: Deep Jain
    // Date: July 26, 2020
    public boolean isReachable(final int fromX, final int fromY, final int toX, final int toY) {
        clearAllVisited();
        if (fromX > toX) return false; 
        if (fromY > toY) return false; 
        if (fromX == toX && fromY == toY) return false; 
        if (!isValidPos(toX, toY)) return false; 

        LinkedList<Coordinate> nextToVisit = new LinkedList<>();
        Coordinate start = new Coordinate(fromX, fromY);
        nextToVisit.add(start);

        while (!nextToVisit.isEmpty()) { // O(N)
            Coordinate cur = nextToVisit.remove();

            // not valid pos
            if (!this.isValidPos(cur.getX(), cur.getY())) { 
//                System.out.printf("Coordinate %d %d not valid position\n", cur.getX(), cur.getY());
                continue;
            }
            // visited pos
            if (cur.getX() != 0 && cur.getY() != 0 && this.visited[cur.getY()][cur.getX()]) {  
//                System.out.printf("Coordinate %d %d visited position\n", cur.getX(), cur.getY());
                continue;
            }
            // position is a rock, skip
            if (this.map[cur.getY()][cur.getX()].equals("X")) { 
//                System.out.println("is rock");
                this.visited[cur.getY()][cur.getX()] = true;
                continue;
            }

            if (cur.getX() == toX && cur.getY() == toY) { 
                return true;
            }

            for (int[] direction : DIRECTIONS) { // O(N)
                Coordinate coordinate = new Coordinate(cur.getX() + direction[0], cur.getY() + direction[1], cur);
                nextToVisit.add(coordinate); //o(1)
                this.visited[cur.getY()][cur.getX()] = true;
            }
        }
        return false;
    }

    // use breadth-first search to go to coordinate from current coordinate
    // BFS code modified and used from:
    // Source: https://www.baeldung.com/java-solve-maze
    // Author: Deep Jain
    // Date: July 26, 2020
    public List<Coordinate> goTo(final int toX, final int toY) {
        clearAllVisited(); //o(n)
        if (this.x > toX) return Collections.emptyList();
        if (this.y > toY) return Collections.emptyList();
        if (this.x == toX && this.y == toY) return Collections.emptyList();
        if (!isValidPos(toX, toY)) return Collections.emptyList();

        LinkedList<Coordinate> nextToVisit = new LinkedList<>();
        Coordinate start = new Coordinate(this.x, this.y);
        nextToVisit.add(start); //o(1)

        while (!nextToVisit.isEmpty()) { // O(N)
            Coordinate cur = nextToVisit.remove(); //o(1)

            // not valid pos
            if (!this.isValidPos(cur.getX(), cur.getY())) {
//                System.out.printf("Coordinate %d %d not valid position\n", cur.getX(), cur.getY());
                continue;
            }
            // visited pos
            if (cur.getX() != 0 && cur.getY() != 0 && this.visited[cur.getY()][cur.getX()]) {
//                System.out.printf("Coordinate %d %d visited position\n", cur.getX(), cur.getY());
                continue;
            }
            // position is a rock, skip
            if (this.map[cur.getY()][cur.getX()].equals("X")) {
//                System.out.println("is rock");
                this.visited[cur.getY()][cur.getX()] = true;
                continue;
            }

            // destination reached, log the path
            if (cur.getX() == toX && cur.getY() == toY) { 
                return backtrackPath(cur);
            }

            for (int[] direction : DIRECTIONS) {  // O(N)
                Coordinate coordinate = new Coordinate(cur.getX() + direction[0], cur.getY() + direction[1], cur);
                nextToVisit.add(coordinate); //o(1)
                this.visited[cur.getY()][cur.getX()] = true;
            }
        }
        return Collections.emptyList();
    }

    // backtrack and get the path
    // BFS code modified and used from:
    // Source: https://www.baeldung.com/java-solve-maze
    // Author: Deep Jain
    // Date: July 26, 2020
    private List<Coordinate> backtrackPath(Coordinate cur) {
        List<Coordinate> path = new ArrayList<>();
        Coordinate iter = cur;

        while (iter != null) { // O(N)
            path.add(iter); //o(1)
            iter = iter.parent;
        }

        for (int i = path.size() - 1; i >= 0; i--) { // O(N)
            int pathX = path.get(i).getX();
            int pathY = path.get(i).getY();

            if (i < path.size() - 1) {
                if (path.get(i).getX() > path.get(i + 1).getX()) {
                    this.x++;
                    this.stepsText = this.stepsText.concat("R");
                    this.steps++;

                    if (isInteger(this.map[pathY][pathX])) { 
                        this.goldGathered += Integer.parseInt(this.map[pathY][pathX]);
                        this.path[pathY][pathX] = "G";
                    } else { // O(1)
                        this.path[pathY][pathX] = "+";
                    }
                } else if (path.get(i).getY() > path.get(i + 1).getY()) {
                    this.y++;
                    this.stepsText = this.stepsText.concat("D");
                    this.steps++;

                    if (isInteger(this.map[pathY][pathX])) { // O(1)
                        this.goldGathered += Integer.parseInt(this.map[pathY][pathX]);
                        this.path[pathY][pathX] = "G";
                    } else {
                        this.path[pathY][pathX] = "+";
                    }
                }
            }

            clearAllVisited();
        }
        return path;
    }

//    public boolean goRight() {
//        if (!isValidPos(++this.x, this.y)) {
//            this.x--;
//            return false;
//        }
//
//        this.stepsText = this.stepsText.concat("R");
//
//        if (isInteger(this.map[this.y][this.x])) {
//            this.goldGathered += Integer.parseInt(this.map[this.y][this.x]);
//            this.path[this.y][this.x] = "G";
//        } else {
//            this.path[this.y][this.x] = "+";
//        }
//        this.visited[this.y][this.x] = true;
//        this.steps++;
//        return true;
//    }
//
//    public boolean goDown() {
//        if (!isValidPos(this.x, ++this.y)) {
//            this.y--;
//            return false;
//        }
//
//        this.stepsText = this.stepsText.concat("D");
//
//        if (isInteger(this.map[this.y][this.x])) {
//            this.goldGathered += Integer.parseInt(this.map[this.y][this.x]);
//            this.path[this.y][this.x] = "G";
//        } else {
//            this.path[this.y][this.x] = "+";
//        }
//        this.visited[this.y][this.x] = true;
//        this.steps++;
//        return true;
//    }

//    private boolean checkMovable() {
//        String down = null;
//        try {
//            down = this.map[y + 1][x];
//        } catch (Exception ignored) {
//        }
//
//        String right = null;
//        try {
//            right = this.map[y][x + 1];
//        } catch (Exception ignored) {
//        }
//
//        // can't move if gnome is blocked by rocks on both right and bottom
//        if (right != null && right.equals("X") && down != null && down.equals("X")) {
//            this.isStuck = true;
//            return false;
//        }
//
//        // can't move anymore if gnome is at the bottom right of the map
//        if (this.y >= row_count - 1 && this.x >= col_count - 1) {
//            this.isStuck = true;
//            return false;
//        }
//
//        // can't move anymore if gnome is at the bottom of the map and has rock to the right of the gnome
//        if (right != null && right.equals("X") && down == null) {
//            this.isStuck = true;
//            return false;
//        }
//
//        // can't move if gnome is at the far right of the map and has rock below the gnome
//        if (down != null && down.equals("X") && right == null) {
//            this.isStuck = true;
//            return false;
//        }
//        return true;
//    }
//
//
//    public static String removeLastChar(String s) {
//        if (s.isEmpty()) return "";
//        return s.substring(0, s.length() - 1);
//    }
//
//    private void revertTo(final int toX, final int toY) {
//        // reset to original pos
//        while (this.x != toX || this.y != toY) {
//            if (isInteger(this.map[this.y][this.x])) {
//                this.goldGathered -= Integer.parseInt(this.map[this.y][this.x]);
//            }
//            this.path[this.y][this.x] = ".";
//            this.visited[this.y][this.x] = false;
//
//            if (this.stepsText.charAt(this.stepsText.length() - 1) == 'R') {
//                this.x--;
//            } else{
//                this.y--;
//            }
//            this.stepsText = removeLastChar(this.stepsText);
//            this.steps--;
//        }
//
//        if (toX == 0 && toY == 0) {
//            // check if 0,0 position has gold
//            if (isInteger(this.map[this.y][this.x])) {
//                this.path[this.y][this.x] = "G";
//            } else {
//                this.path[0][0] = "+";
//            }
//            this.visited[this.y][this.x] = true;
//        }
//
////        // debug
////        System.out.println();
////        System.out.printf("X and Y after reverting: %d %d\n", this.x, this.y);
////        System.out.println("Current path after revert:");
////        displayPath();
////        System.out.println();
//    }

//    private boolean goTo(final int toX, final int toY) {
//        System.out.printf("Current X and Y: %d %d\n", this.x, this.y);
//        System.out.printf("Going to %d %d\n", toX, toY);
//
//        if (this.x > toX) return false;
//        if (this.y > toY) return false;
//        if (this.x == toX && this.y == toY) return false;
//        if (!isValidPos(toX, toY)) return false;
//
//        // If target is on the same row
//        if (this.x != toX && this.y == toY) {
//            // Path unreachable because there's a rock blocking the way
//            for (int i = this.x; i < toX; i++) {
//                if (this.map[this.y][i].equals("X")) {
//                    this.unreachable[toY][toX] = true;
//                    System.out.printf("Position %d %d (X and Y) is unreachable\n", toX, toY);
//                    return false;
//                }
//            }
//        }
//        // If target is on the same row
//        else {
//            // Path unreachable because there's a rock blocking the way
//            for (int i = this.y; i < toY; i++) {
//                if (this.map[i][this.x].equals("X")) {
//                    this.unreachable[toY][toX] = true;
//                    System.out.printf("Position %d %d (X and Y) is unreachable\n", toX, toY);
//                    return false;
//                }
//            }
//        }
//
//        // path unreachable if it's covered by rocks on both left and top
//        if (toY > 0 && this.map[toY-1][toX].equals("X") && toX > 0 && this.map[toY][toX-1].equals("X")) {
//            this.unreachable[toY][toX] = true;
//            System.out.printf("Position %d %d (X and Y) is unreachable\n", toX, toY);
//            return false;
//        }
//
//        int[] lastPos = { 0, 0 };
//        while (lastPos[0] < this.x) lastPos[0]++;
//        while (lastPos[1] < this.y) lastPos[1]++;
//
//        while (this.x < toX || this.y < toY) {
//            if (this.x < toX) {
//                if (!goRight()) {
//                    if (!goDown()) {
//                        this.unreachable[toY][toX] = true;
//                        System.out.printf("Position %d %d (X and Y) is unreachable\n", toX, toY);
//                        revertTo(lastPos[0], lastPos[1]);
//                        return false;
//                    }
//                }
//
//                // debug
//                System.out.println();
//                System.out.println("Path taken:");
//                displayPath();
//                System.out.println("Steps: " + this.stepsText);
//                continue;
//            }
//
//            if (!goDown()) {
//                if (!goRight()) {
//                    this.unreachable[toY][toX] = true;
//                    System.out.printf("Position %d %d (X and Y) is unreachable\n", toX, toY);
//                    revertTo(lastPos[0], lastPos[1]);
//                    return false;
//                }
//            }
//
//            // debug
//            System.out.println();
//            System.out.println("Path taken:");
//            displayPath();
//            System.out.println("Steps: " + this.stepsText);
//        }
//        for (int i = 0; i < this.goldPath.length; i++) {
//            if (this.goldPath[i][0] == -1 && this.goldPath[i][1] == -1) {
//                this.goldPath[i][0] = toX;
//                this.goldPath[i][1] = toY;
//                break;
//            }
//        }
//        System.out.printf("toX and toY: %d %d\n", toX, toY);
//        System.out.printf("X and Y: %d %d\n", this.x, this.y);
//        return true;
//    }
//
//    private void findAndGoToGold() {
//        boolean noGold = true;
//
//        // scan each row from top to bottom, column left to right
//        for (int i = this.y; i < this.row_count; i++) {
//            if (!rowHasGold(i)) continue;
//
//            for (int j = this.x; j < this.col_count; j++) {
////                System.out.println("Searching x = " + j + ", y = " + i);
//                if (isInteger(this.map[i][j]) && !this.unreachable[i][j] && !(j == this.x && i == this.y)) {
//                    noGold = false;
////                    System.out.println("Found gold: " + this.map[i][j] + " at position " + j + " " + i);
//                    this.goTo(j, i);
//                }
//            }
//        }
//
//        if (noGold) this.isStuck = true;
//    }

    // display path taken by the gnome
    public void displayPath() {
        for (int i = 0; i < this.row_count; i++) { // O(N)
            for (int j = 0; j < this.col_count; j++) {  // O(m)
                if (this.path[i][j].equals("+") || this.path[i][j].equals("G") ) {
                    System.out.print(this.path[i][j] + " ");
                } else { // O(1)
                    System.out.print(this.map[i][j] + " ");
                }
            }
            System.out.println();
        }
    }

    // return true if row has gold
    // return false otherwise
    private boolean rowHasGold(final int row) {
        for (int i = 0; i < this.col_count; i++) // O(N)
            if (isInteger(this.map[row][i]) && (i != this.x || row != this.y))
                return true;
        return false;
    }

    // add all reachable gold locations
    private ArrayList<Coordinate> getAllGoldLocations() { // o(n*m)
        ArrayList<Coordinate> arr = new ArrayList<>();
        for (int i = this.y; i < this.row_count; i++) { // O(N)
            if (!rowHasGold(i)) continue;
            for (int j = this.x; j < this.col_count; j++) { // O(m)
                if (!isInteger(this.map[i][j]) || i == 0 && j == 0) continue;

                // blocked by rocks on both left and upper sides, unreachable, skip gold pos
                if (j > 0 && i > 0 && this.map[i-1][j].equals("X") && this.map[i][j-1].equals("X")) { 
                    continue;
                }
                // in bottom left corner and is blocked by rock, unreachable, skip gold pos
                if (j == 0 && this.map[i-1][j].equals("X")) { 
                    continue;
                }
                // in top right corner and is blocked by rock, unreachable, skip gold pos
                if (i == 0 && this.map[i][j-1].equals("X")) { 
                    continue;
                }

                if (!isReachable(0, 0, j, i)) { 
                    continue;
                }
                arr.add(new Coordinate(j, i)); //o(1)
            }
        }
        return arr;
    }

    public void printResults() {
        System.out.println();
        System.out.println("================");
        System.out.println("RESULTS:");
        System.out.println("Path taken:");
        displayPath();
        System.out.println("Steps: " + this.steps);
        System.out.println("Gold gathered: " + this.goldGathered);
        System.out.println(this.stepsText.isEmpty() ? "Steps taken: None" : "Steps taken: " + this.stepsText);
        System.out.println();
        System.out.println("=====================================");
    }

    // function tha gest best path for gnome
    private void getBestPath() { // 2o(n*m) + o(n*m)^2
        // get all reachable gold locations
        this.goldLocations = getAllGoldLocations(); // o(n*m)

//        // print all gold coordinates for debugging
//        System.out.println("================");
//        System.out.println("All reachable gold locations: " + Arrays.deepToString(this.goldLocations.toArray()));
//        System.out.println("Amount of reachable gold locations: " + this.goldLocations.size());
        
        // no reachable gold locations found, stop program
        if (this.goldLocations.size()  < 1) {
            System.out.println("No unreachable gold locations, no mining needed.");
            return;
        }

        // try all possible combinations of paths with the gold coordinates and
        // store all the possible paths in this.possibleGoldPaths array, which is a list
        // of lists of coordinates.
        if (this.goldLocations.size() > 2) {
            this.getAllPossiblePaths();
        } else {
            for (Coordinate goldLocation : this.goldLocations) { // O(N*m)
                int x = goldLocation.getX();
                int y = goldLocation.getY();

                if (isReachable(0, 0, x, y)) {
                    GnomePath gnomePath = new GnomePath(x, y);
                    this.possibleGoldPaths.add(gnomePath); // o(1)
                }
            }
        }

        // add all reachable gold locations to possible gold paths
        for (Coordinate goldLocation : this.goldLocations) { // O(N*m)
            GnomePath temp = new GnomePath();
            temp.add(goldLocation);
            this.possibleGoldPaths.add(temp);
        }

//        // Print possible gold paths list for debugging
//        System.out.println("---------------------");
//        System.out.println("Possible gold path list size: " + this.possibleGoldPaths.size());
//        System.out.println("possibleGoldPaths: ");
//        for (int i = 0; i < this.possibleGoldPaths.size(); i++) {
//            System.out.println(this.possibleGoldPaths.get(i));
//        }
//        System.out.println("---------------------");

        // traverse through each stored combination and store the best total
        // gold with the least steps and index of path in list
        int bestTotalGold = 0;
        int bestTotalSteps = this.row_count+this.col_count;
        int bestPath = 0;

        // loop through all paths o(n*m)^2
        for (int i = 0; i < this.possibleGoldPaths.size(); i++) {
            // nagivate through the path
            reset();
            for (int j = 0; j < this.possibleGoldPaths.get(i).size(); j++) {
                this.goTo(this.possibleGoldPaths.get(i).get(j).getX(), this.possibleGoldPaths.get(i).get(j).getY());
            }

//            System.out.printf("Path's gold %d, steps %d, path: %s\n", this.goldGathered, this.steps, this.possibleGoldPaths.get(i));

            // if we have path that has more total gold or equal amount of gold but fewer steps needed, update best path
            if (bestTotalGold < this.goldGathered || bestTotalGold == this.goldGathered && bestTotalSteps > this.steps) { 
                bestTotalGold = 0;
                bestTotalSteps = this.row_count+this.col_count;
                bestPath = 0;
                while (bestTotalGold < this.goldGathered) bestTotalGold++; //
                while (bestTotalSteps > this.steps) bestTotalSteps--; //
                while (bestPath < i) bestPath++; //
//                System.out.printf("Best path found, gold %d, steps %d, path: %s\n", bestTotalGold, bestTotalSteps, this.possibleGoldPaths.get(bestPath));
            }
//            System.out.println("------------------");
        }

//        System.out.printf("Best path: index %d, gold %d, steps %d, path: %s\n", bestPath, bestTotalGold, bestTotalSteps, this.possibleGoldPaths.get(bestPath));

        // reset and traverse through the best path and print the results
        reset();
        for (int i = 0; i < this.possibleGoldPaths.get(bestPath).size(); i++) { // O(N*m) * O(N*M)
            for (int j = 0; j < this.possibleGoldPaths.get(bestPath).size(); j++) {
                int x = this.possibleGoldPaths.get(bestPath).get(j).getX();
                int y = this.possibleGoldPaths.get(bestPath).get(j).getY();
                this.goTo(x, y);
            }
        }
        this.printResults();
    }

    private void getAllPossiblePaths() {
        for (int i = 0; i < this.goldLocations.size() ; i++) { 
            int firstCyclePosX = this.goldLocations.get(i).getX();
            int firstCyclePosY = this.goldLocations.get(i).getY();
//            System.out.printf("i: = %d, %d %d\n", i, firstCyclePosX, firstCyclePosY);

            if (!isReachable(0, 0, firstCyclePosX, firstCyclePosY)) { 
//                    System.out.printf("can't go to coordinate j %d %d\n", secondCyclePosX, secondCyclePosY);
                continue;
            }

            for (int j = i + 1; j < this.goldLocations.size() ; j++) { 
                int secondCyclePosX = this.goldLocations.get(j).getX();
                int secondCyclePosY = this.goldLocations.get(j).getY();
//                System.out.printf("j: = %d, %d %d\n", j, secondCyclePosX, secondCyclePosY);

                if (!isReachable(firstCyclePosX, firstCyclePosY, secondCyclePosX, secondCyclePosY)) {
//                    System.out.printf("can't go to coordinate j %d %d\n", secondCyclePosX, secondCyclePosY);
                    continue;
                }

                GnomePath tempList = new GnomePath();
                tempList.add(firstCyclePosX, firstCyclePosY);
                tempList.add(secondCyclePosX, secondCyclePosY);
//                System.out.println("tempList: " + tempList);
                this.possibleGoldPaths.add(tempList);
            }
        }

//        // Print possible gold paths list for debugging
//        System.out.println("---------------------");
//        System.out.println("Possible gold path list size: " + this.possibleGoldPaths.size());
//        System.out.println("possibleGoldPaths: ");
//        for (int i = 0; i < this.possibleGoldPaths.size(); i++) {
//            System.out.println(this.possibleGoldPaths.get(i));
//        }
//        System.out.println("---------------------");


        ArrayList<GnomePath> pathList = new ArrayList<>();

        for (GnomePath possibleGoldPath : this.possibleGoldPaths) { // O(N)
            ArrayList<GnomePath> tempList = new ArrayList<>();
            GnomePath tempPath = new GnomePath();
            for (int j = 0; j < possibleGoldPath.size(); j++) { // O(N)
                tempPath.add(possibleGoldPath.get(j));
            }
            tempList.add(tempPath);
            combineAllPaths(tempList);
            pathList.addAll(tempList);
        }

//        // Print all possible gold paths list for debugging
//        System.out.println("---------------------");
//        System.out.println("pathList size: " + pathList.size());
//        System.out.println("pathList: ");
//        for (int i = 0; i < pathList.size(); i++) {
//            System.out.println(pathList.get(i));
//        }
//        System.out.println("---------------------");

        this.possibleGoldPaths.clear();
        this.possibleGoldPaths.addAll(pathList);
    }

    // combine all possible paths
    private void combineAllPaths(ArrayList<GnomePath> list) {
        // current coordinate is the 2nd coordinate in the list
        // or the coordinate that is reachable from the first coordinate in list
        Coordinate currentCoord = list.get(0).get(list.get(0).size() - 1); //o(1)

        // go through the list and check if the coordinate has
        // coordinates it can go to
        for (GnomePath possibleGoldPath : this.possibleGoldPaths) {
            if (possibleGoldPath.get(0).equals(currentCoord)) {
                // create a new list of coordinate with new
                // reachable coordinate added and repeat for all the
                // found reachable coordinates
                GnomePath tempPath = new GnomePath();
                for (int i = 0; i < list.get(0).size(); i++) {
                    tempPath.add(list.get(0).get(i));
                }
                tempPath.add(possibleGoldPath.get(1));

                // add new path to all-possible-paths list
                list.add(tempPath);
            }
        }

        // finished adding the new paths, remove
        // old path
        list.remove(0);

        // repeat the function above but with the newly added paths from above
        int currentIndex = 0;
        while (currentIndex != list.size()) { //
            currentCoord = list.get(currentIndex).get(list.get(currentIndex).size() - 1);

            boolean hasExtraPaths = false;
            for (GnomePath possibleGoldPath : this.possibleGoldPaths) {
                if (possibleGoldPath.get(0).equals(currentCoord)) { //
                    hasExtraPaths = true;
                    GnomePath tempPath = new GnomePath();
                    for (int i = 0; i < list.get(currentIndex).size(); i++) {
                        tempPath.add(list.get(currentIndex).get(i));
                    }
                    tempPath.add(possibleGoldPath.get(1));
                    list.add(tempPath);
                }
            }

            if (hasExtraPaths) list.remove(currentIndex); 
            else currentIndex++;  //O(1)
        }
    }

    public static void main(String[] arg) {
        // get start time to calculate processing time
        long start = System.nanoTime();

        long beforeUsedMem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();

//        String[][] map1 = {
//                {".", "2", "."},
//                {".", "X", "3"},
//                {"6", ".", "X"}
//        };
//
//        TheGreedyGnome gnome1 = new TheGreedyGnome(map1, 3, 3);
//        gnome1.displayMap(map1);
//        gnome1.getBestPath();

//        String[][] map2 = {
//                {"3", "X", "."},
//                {".", ".", "9"},
//                {"X", ".", "3"},
//                {"6", "8", "."}
//        };
//
//        TheGreedyGnome gnome2 = new TheGreedyGnome(map2, 4, 3);
//        gnome2.displayMap(map2);
//        gnome2.getBestPath();

//        String[][] map3 = {
//                {"3", ".", "2"},
//                {".", ".", "X"},
//                {".", ".", "X"},
//                {"6", "X", "."}
//        };
//
//        TheGreedyGnome gnome3 = new TheGreedyGnome(map3, 4, 3);
//        gnome3.displayMap(map3);
//        gnome3.getBestPath();


//        String[][] map4 = {
//                {"3", ".", "X", "."},
//                {".", "X", "1", "X"},
//                {".", "X", "2", "X"},
//                {"12", "X", ".", "X"}
//        };
//
//        TheGreedyGnome gnome4 = new TheGreedyGnome(map4, 4, 4);
//        gnome4.displayMap(map4);
//        gnome4.getBestPath();

//        String map5[][]= {
//                {".", ".", ".", "."},
//                {".", ".", "X", "."},
//                {"3", ".", "X", "8"},
//                {"6", "X", ".", "X"}
//        };
//
//        TheGreedyGnome gnome5 = new TheGreedyGnome(map5, 4, 4);
//        gnome5.displayMap(map5);
//        gnome5.getBestPath();

        TheGreedyGnome gnome6 = new TheGreedyGnome("map1.txt");
        gnome6.getBestPath();

        // get finish time and calculate processing time
        long finish = System.nanoTime();
        long timeElapsed = finish - start;
        System.out.println("Processing time: " + timeElapsed/1000000 + " milliseconds.");

        long afterUsedMem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
        long actualMemUsed=afterUsedMem-beforeUsedMem;

        System.out.printf("Memory used: %d kB\n", actualMemUsed/1000);
    }
}
