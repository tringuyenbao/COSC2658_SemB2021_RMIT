// RMIT University Vietnam
// Course: COSC2658 - Data Structures & Algorithms
// Semester: 2021C
// Assignment: Group Project
// Authors: Quach Gia Vi (3757317), Bui Manh Dai Duong (s3757278), Nguyen Bao Tri (s3749560)

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.List;
import java.util.Collections;

public class ExhaustiveSearch {
    public int x = 0;
    public int y = 0;
    public int goldGathered = 0;
    public int steps = 0;
    public String[][] map;
    public int rowCount;
    public int colCount;
    public String[][] path;
    public boolean[][] visited;
    public String stepsText = "";
//    public boolean isStuck = false;

    // down or right directions only
    private static final int[][] DIRECTIONS = { { 0, 1 }, { 1, 0 } };

    // list of all reachable gold locations
    ArrayList<Coordinate> goldLocations = new ArrayList<>();

    // list of all possible combinations of paths of all gold coordinates
    ArrayList<GnomePath> allPossibleGoldPaths = new ArrayList<>();

    public static boolean isInteger(String str) {
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
            this.path = new String[this.rowCount][this.colCount];
            this.visited = new boolean[this.rowCount][this.colCount];

            for (String[] rows : this.path) Arrays.fill(rows, ".");

            // check if 0,0 position has gold
            if (isInteger(this.map[this.y][this.x])) {
                this.goldGathered += Integer.parseInt(this.map[this.y][this.x]);
                this.path[this.y][this.x] = "G";
            } else {
                this.path[0][0] = "+";
            }
            this.visited[this.y][this.x] = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void reset() {
        this.x = 0;
        this.y = 0;
        this.goldGathered = 0;
        this.steps = 0;
        this.stepsText = "";
//        this.isStuck = false;
        for(String[] rows : this.path) Arrays.fill(rows, ".");
        for(boolean[] rows : this.visited) Arrays.fill(rows, false);

        // check if 0,0 position has gold
        if (isInteger(this.map[this.y][this.x])) {
            this.goldGathered += Integer.parseInt(this.map[this.y][this.x]);
            this.path[this.y][this.x] = "G";
        } else {
            this.path[this.y][this.x] = "+";
        }
        this.visited[this.y][this.x] = true;
    }

//    // constructor for testing
//    public ExhaustiveSearch(String[][] map, final int rowCount, final int colCount) {
//        this.map = map;
//        this.rowCount = rowCount;
//        this.colCount = colCount;
//
//        this.init();
//    }

    // actual constructor
    public ExhaustiveSearch(String filename) {
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
                file.close();
                System.out.println("Invalid row and column values.");
                throw new Exception("Invalid row and column values.");
            } else {
                this.rowCount = Integer.parseInt(row_col.split(" ")[0]);
                this.colCount = Integer.parseInt(row_col.split(" ")[1]);
                this.map = new String[this.rowCount][this.colCount];
            }

            int row = 0;
            while (file.hasNext()) {
                String[] data = file.nextLine().split(" ");
                 for (int col = 0; col < data.length; col++) {
                    this.map[row][col] = String.valueOf(data[col]).toUpperCase();
                }
                row++;
            }
            file.close();

            this.init();
            this.getBestPath();
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Discrepancy between row/col value and actual map row/col count detected, program aborts.");
//            e.printStackTrace();
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
//            e.printStackTrace();
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    // check whether given coordinate (x, y) is a valid coordinate or not.
    private boolean isValidPos(int x, int y) {
        if (x < 0 || x >= colCount || y < 0 || y >= rowCount) {
            return false;
        }
        return !this.map[y][x].equals("X");
    }

    // display mine map
    public void displayMap(String[][] map) {
        for (String[] rows : map) {
            for (String col : rows) System.out.print(col + " ");
            System.out.println();
        }
    }

    // clear visited array
    private void clearAllVisited() {
        for (boolean[] rows : this.visited) Arrays.fill(rows, false);
    }

    // check if coordinate is reachable from an x and y coordinate
    // BFS code modified and used from:
    // Source: https://www.baeldung.com/java-solve-maze
    // Author: Deep Jain
    // Date: July 26, 2020
    public boolean isReachable(final int fromX, final int fromY, final int toX, final int toY) {
        if (fromX > toX) return false;
        if (fromY > toY) return false;
        if (fromX == toX && fromY == toY) return false;
        if (!isValidPos(toX, toY)) return false;

        clearAllVisited();
        LinkedList<Coordinate> nextToVisit = new LinkedList<>();
        Coordinate start = new Coordinate(fromX, fromY);
        nextToVisit.add(start);

        while (!nextToVisit.isEmpty()) {
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

            for (int[] direction : DIRECTIONS) {
                Coordinate coordinate = new Coordinate(cur.getX() + direction[0], cur.getY() + direction[1], cur);
                nextToVisit.add(coordinate);
                this.visited[cur.getY()][cur.getX()] = true;
            }
        }
        return false;
    }

    // travel through a list of coordinates
    public void travel(GnomePath path) {
        for (int i = 0; i < path.size(); i++) {
            this.goTo(path.get(i).getX(), path.get(i).getY());
        }
    }

    // use breadth-first search to go to coordinate from current coordinate
    // BFS code modified and used from:
    // Source: https://www.baeldung.com/java-solve-maze
    // Author: Deep Jain
    // Date: July 26, 2020
    public List<Coordinate> goTo(final int toX, final int toY) {
        if (this.x > toX) return Collections.emptyList();
        if (this.y > toY) return Collections.emptyList();
        if (this.x == toX && this.y == toY) return Collections.emptyList();
        if (!isValidPos(toX, toY)) return Collections.emptyList();

        clearAllVisited();
        LinkedList<Coordinate> nextToVisit = new LinkedList<>();
        Coordinate start = new Coordinate(this.x, this.y);
        nextToVisit.add(start);

        while (!nextToVisit.isEmpty()) {
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

            // destination reached, log the path
            if (cur.getX() == toX && cur.getY() == toY) {
                return backtrackPath(cur);
            }

            for (int[] direction : DIRECTIONS) {
                Coordinate coordinate = new Coordinate(cur.getX() + direction[0], cur.getY() + direction[1], cur);
                nextToVisit.add(coordinate);
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

        while (iter != null) {
            path.add(iter);
            iter = iter.parent;
        }

        for (int i = path.size() - 1; i >= 0; i--) {
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
                    } else {
                        this.path[pathY][pathX] = "+";
                    }
                } else if (path.get(i).getY() > path.get(i + 1).getY()) {
                    this.y++;
                    this.stepsText = this.stepsText.concat("D");
                    this.steps++;

                    if (isInteger(this.map[pathY][pathX])) {
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
//        if (this.y >= rowCount - 1 && this.x >= colCount - 1) {
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
//        for (int i = this.y; i < this.rowCount; i++) {
//            if (!rowHasGold(i)) continue;
//
//            for (int j = this.x; j < this.colCount; j++) {
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

//    // return true if row has gold
//    // return false otherwise
//    private boolean rowHasGold(final int row) {
//        for (int i = 0; i < this.colCount; i++)
//            if (isInteger(this.map[row][i]) && (i != this.x || row != this.y))
//                return true;
//        return false;
//    }

    // display path taken by the gnome
    public void displayPath() {
        for (int i = 0; i < this.rowCount; i++) {
            for (int j = 0; j < this.colCount; j++) {
                if (this.path[i][j].equals("+") || this.path[i][j].equals("G") ) {
                    System.out.print(this.path[i][j] + " ");
                } else {
                    System.out.print(this.map[i][j] + " ");
                }
            }
            System.out.println();
        }
    }

    // add all reachable gold locations
    private void getAllGoldLocations() {
        for (int i = 0; i < this.rowCount; i++) {
            for (int j = 0; j < this.colCount; j++) {
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
                this.goldLocations.add(new Coordinate(j, i));
            }
        }
    }

    public void printResults() {
        System.out.println();
        System.out.println("================");
        System.out.println("RESULTS:");
        // System.out.println("Path taken:");
        // displayPath();
        System.out.println("Steps: " + this.steps);
        System.out.println("Gold gathered: " + this.goldGathered);
        System.out.println(this.stepsText.isEmpty() ? "Steps taken: None" : "Steps taken: " + this.stepsText);
        System.out.println("=====================================");
    }

    // function tha gest best path for gnome
    private void getBestPath() {
        // get all reachable gold locations
        this.getAllGoldLocations();

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
        // store all the possible paths in this.allPossibleGoldPaths array, which is a list
        // of lists of coordinates.
        if (this.goldLocations.size() > 2) {
            this.getAllPossiblePaths();
        } else {
            for (Coordinate goldLocation : this.goldLocations) {
                int x = goldLocation.getX();
                int y = goldLocation.getY();

                if (isReachable(0, 0, x, y)) {
                    GnomePath gnomePath = new GnomePath(x, y);
                    this.allPossibleGoldPaths.add(gnomePath);
                }
            }
        }

        // add all reachable gold locations to possible gold paths
        for (Coordinate goldLocation : this.goldLocations) {
            GnomePath temp = new GnomePath();
            temp.add(goldLocation);
            this.allPossibleGoldPaths.add(temp);
        }

//        // Print possible gold paths list for debugging
//        System.out.println("---------------------");
//        System.out.println("Possible gold path list size: " + this.allPossibleGoldPaths.size());
//        System.out.println("possibleGoldPaths: ");
//        for (int i = 0; i < this.allPossibleGoldPaths.size(); i++) {
//            System.out.println(this.allPossibleGoldPaths.get(i));
//        }
//        System.out.println("---------------------");

        // traverse through each stored combination and store the best total
        // gold with the least steps and index of path in list
        int bestTotalGold = 0;
        int bestTotalSteps = this.rowCount+this.colCount;
        int bestPath = 0;

        // loop through all paths
        for (int i = 0; i < this.allPossibleGoldPaths.size(); i++) {
            // nagivate through the path
            reset();
            this.travel(this.allPossibleGoldPaths.get(i));

//            System.out.printf("Path's gold %d, steps %d, path: %s\n", this.goldGathered, this.steps, this.allPossibleGoldPaths.get(i));

            // if we have path that has more total gold or equal amount of gold but fewer steps needed, update best path
            if (bestTotalGold < this.goldGathered || bestTotalGold == this.goldGathered && bestTotalSteps > this.steps) {
                bestTotalGold = 0;
                bestTotalSteps = this.rowCount+this.colCount;
                bestPath = 0;
                while (bestTotalGold < this.goldGathered) bestTotalGold++;
                while (bestTotalSteps > this.steps) bestTotalSteps--;
                while (bestPath < i) bestPath++;
//                System.out.printf("Best path found, gold %d, steps %d, path: %s\n", bestTotalGold, bestTotalSteps, this.allPossibleGoldPaths.get(bestPath));
            }
//            System.out.println("------------------");
        }

//        System.out.printf("Best path: index %d, gold %d, steps %d, path: %s\n", bestPath, bestTotalGold, bestTotalSteps, this.allPossibleGoldPaths.get(bestPath));

        // reset and traverse through the best path and print the results
        reset();
        this.travel(this.allPossibleGoldPaths.get(bestPath));
        this.printResults();
    }

    private void getAllPossiblePaths() {
        ArrayList<GnomePath> possibleGoldPaths = new ArrayList<>();

        for (int i = 0; i < this.goldLocations.size() ; i++) {
            Coordinate coord1 = this.goldLocations.get(i);
            for (int j = i + 1; j < this.goldLocations.size() ; j++) {
                Coordinate coord2 = this.goldLocations.get(j);

                if (!isReachable(coord1.getX(), coord1.getY(), coord2.getX(), coord2.getY())) {
                    continue;
                }

                GnomePath temp = new GnomePath();
                temp.add(coord1);
                temp.add(coord2);
                possibleGoldPaths.add(temp);
            }
        }

        // // Print possible gold paths list for debugging
        // System.out.println("---------------------");
        // System.out.println("Possible gold path list size: " + possibleGoldPaths.size());
        // System.out.println("possibleGoldPaths: ");
        // for (int i = 0; i < possibleGoldPaths.size(); i++) {
        //     System.out.println(possibleGoldPaths.get(i));
        // }
        // System.out.println("---------------------");

        for (GnomePath path : possibleGoldPaths) {
            ArrayList<GnomePath> tempList = new ArrayList<>();
            GnomePath tempPath = new GnomePath();
            for (int j = 0; j < path.size(); j++) {
                tempPath.add(path.get(j));
            }
            tempList.add(tempPath);

            int currentIndex = 0;
            while (currentIndex != tempList.size()) {
                // get the last coordinate in path
                Coordinate currentCoord = tempList.get(currentIndex).get(tempList.get(currentIndex).size() - 1);
    
                boolean hasExtraPaths = false;
                // loop through all paths
                for (GnomePath possibleGoldPath : possibleGoldPaths) {
                    // if last coordinate has more path options, 
                    // add the new coordinate to current path, add that 
                    // new path to list, and remove old path from list
                    if (possibleGoldPath.get(0).equals(currentCoord)) {
                        hasExtraPaths = true;
                        GnomePath temp = new GnomePath();
                        for (int i = 0; i < tempList.get(currentIndex).size(); i++) {
                            temp.add(tempList.get(currentIndex).get(i));
                        }
                        temp.add(possibleGoldPath.get(1));
                        tempList.add(temp);
                    }
                }

                // // Print possible gold paths list for debugging
                // System.out.println("---------------------");
                // System.out.println("tempList size: " + tempList.size());
                // System.out.println("tempList: ");
                // for (int i = 0; i < tempList.size(); i++) {
                //     System.out.println(tempList.get(i));
                // }
                // System.out.println("---------------------");

                // if (currentIndex == 5) break;
    
                if (hasExtraPaths) tempList.remove(currentIndex);
                else currentIndex++;
            }

            this.allPossibleGoldPaths.addAll(tempList);
        }
    }

    public static void main(String[] args) {
//        String[][] map1 = {
//                {".", "2", "."},
//                {".", "X", "3"},
//                {"6", ".", "X"}
//        };
//
//        ExhaustiveSearch gnome1 = new ExhaustiveSearch(map1, 3, 3);
//        gnome1.displayMap(map1);
//        gnome1.getBestPath();

//        String[][] map2 = {
//                {"3", "X", "."},
//                {".", ".", "9"},
//                {"X", ".", "3"},
//                {"6", "8", "."}
//        };
//
//        ExhaustiveSearch gnome2 = new ExhaustiveSearch(map2, 4, 3);
//        gnome2.displayMap(map2);
//        gnome2.getBestPath();

//        String[][] map3 = {
//                {"3", ".", "2"},
//                {".", ".", "X"},
//                {".", ".", "X"},
//                {"6", "X", "."}
//        };
//
//        ExhaustiveSearch gnome3 = new ExhaustiveSearch(map3, 4, 3);
//        gnome3.displayMap(map3);
//        gnome3.getBestPath();


//        String[][] map4 = {
//                {"3", ".", "X", "."},
//                {".", "X", "1", "X"},
//                {".", "X", "2", "X"},
//                {"12", "X", ".", "X"}
//        };
//
//        ExhaustiveSearch gnome4 = new ExhaustiveSearch(map4, 4, 4);
//        gnome4.displayMap(map4);
//        gnome4.getBestPath();

//        String map5[][]= {
//                {".", ".", ".", "."},
//                {".", ".", "X", "."},
//                {"3", ".", "X", "8"},
//                {"6", "X", ".", "X"}
//        };
//
//        ExhaustiveSearch gnome5 = new ExhaustiveSearch(map5, 4, 4);
//        gnome5.displayMap(map5);
//        gnome5.getBestPath();

//        ExhaustiveSearch gnome6 = new ExhaustiveSearch("map2.txt");
//        gnome6.getBestPath();

        if (args.length != 1) {
            throw new IllegalArgumentException("Require 1 argument.");
        }

        String filename = args[0];

        // check if file extension is .txt
        if ((filename.charAt(filename.length() - 1) != 't' || filename.charAt(filename.length() - 1) != 'T')
                && (filename.charAt(filename.length() - 2) != 'x' || filename.charAt(filename.length() - 1) != 'X')
                && (filename.charAt(filename.length() - 3) != 't' || filename.charAt(filename.length() - 1) != 'T')
                && filename.charAt(filename.length() - 4) != '.') {
            throw new IllegalArgumentException("Invalid file extension.");
        }

        // get start time to calculate processing time
        long start = System.nanoTime();
        long beforeUsedMem=Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        try {
            new ExhaustiveSearch(filename);
//            new ExhaustiveSearch("maps/2_21.txt");
//            new ExhaustiveSearch("maps/3_3.txt");
//            new ExhaustiveSearch("maps/10_10.txt");
//            new ExhaustiveSearch("maps/12_23.txt");
//            new ExhaustiveSearch("maps/17_1.txt");
//            new ExhaustiveSearch("maps/19_13.txt");
//            new ExhaustiveSearch("maps/25_8.txt");
//            new ExhaustiveSearch("maps/26_26.txt");
//            new ExhaustiveSearch("maps/27_27.txt");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // get finish time and calculate processing time
        long finish = System.nanoTime();
        long timeElapsed = finish - start;
//        System.out.println("Processing time: " + timeElapsed + " nanoseconds.");
//        System.out.println("Processing time: " + timeElapsed/1000 + " microseconds.");
        System.out.println("Processing time: " + timeElapsed/1000000 + " milliseconds.");

        long afterUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long actualMemUsed = afterUsedMem - beforeUsedMem;

        System.out.printf("Memory used: %d kB\n", actualMemUsed/1000);
    }
}
