// RMIT University Vietnam
// Course: COSC2658 - Data Structures & Algorithms
// Semester: 2021C
// Assignment: Group Project
// Authors: Quach Gia Vi (3757317), Bui Manh Dai Duong (s3757278), Nguyen Bao Tri (s3749560)

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Collections;

public class TheGreedyGnomeTri {
    private String[][] map;
    private int row_count;
    private int col_count;
    private boolean hasGold;

    // initialize data structures to record the value
    private String[][] pathTo;
    private int[][] goldPath;

    // hash table to record the collectable amount of gold (as key), and the shortest way to get it (as value)
    public HashMap<Integer, String> recordTable = new HashMap<>();

    public static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public static boolean isInteger(char c) {
        return c >= '0' && c <= '9';
    }

    // display mine map
    public void displayMap(String[][] map) {
        for (String[] rows : map) {
            for (String col : rows)
                System.out.print(col + " ");
            System.out.println();
        }
    }

    private void init() {
        this.pathTo = new String[row_count][col_count];
        this.goldPath = new int[row_count][col_count];
        for (String[] rows : this.pathTo) Arrays.fill(rows, "");
        for (int[] rows : this.goldPath) Arrays.fill(rows, 0);
    }

    // constructor for testing
    public TheGreedyGnomeTri(String[][] map, final int row_count, final int col_count) {
        this.map = map;
        this.row_count = row_count;
        this.col_count = col_count;
        this.init();
    }

    // actual constructor
    public TheGreedyGnomeTri(String filename) {
        this.readMap(filename);
        if (this.hasGold) {
            this.init();
            this.getBestPath();
        } else {
            System.out.println("No gold found in map!");
        }
    }

    // open mine map file, validate the data, then create array of the map
    private void readMap(String filename) {
        try {
            File myFile = new File(filename);
            Scanner file = new Scanner(myFile);

            // reads first line
            String row_col = file.nextLine();
            if (!isInteger(row_col.split(" ")[0]) && !isInteger(row_col.split(" ")[1])) {
                System.out.println("Invalid row and column values.");
                file.close();
                return;
            }

            this.row_count = Integer.parseInt(row_col.split(" ")[0]);
            this.col_count = Integer.parseInt(row_col.split(" ")[1]);
            this.map = new String[this.row_count][this.col_count];

            this.hasGold = false;
            int row = 0;
            while (file.hasNext()) {
                String[] data = file.nextLine().split(" ");
                for (int i = 0; i < data.length; i++) {
                    if (isInteger(data[i])) this.hasGold = true;
                    this.map[row][i] = String.valueOf(data[i]).toUpperCase();
                }
                row++;
            }
            file.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getBestPath() {
        for (int i = 0; i < this.row_count; i++) {
            for (int j = 0; j < this.col_count; j++) {
                // if the cell is blocked
                if (this.map[i][j].equals("X")) {
                    this.goldPath[i][j] = -1;
                    this.pathTo[i][j] = "X";
                    continue;
                }

                // checking the first column --> only need to check the upper cell
                if (j == 0) {
                    // cell is at starting position
                    if (i == 0) {
                        if (this.map[i][j].equals(".")) {
                            this.goldPath[i][j] = 0;
                        } else {
                            this.goldPath[i][j] = Integer.parseInt(this.map[i][j]);
                        }
                    }
                    // not at starting position
                    else {
                        // if the upper cell is blocked
                        if (this.map[i-1][j].equals("X") || this.pathTo[i - 1][j].equals("X")) {
                            this.pathTo[i][j] = "X";
                        }
                        // if the upper cell is not blocked
                        else {
                            // record the path --> go down
                            this.pathTo[i][j] = this.pathTo[i-1][j].concat("D");

                            // if the cell is just the way
                            if (map[i][j].equals(".")) {
                                this.goldPath[i][j] = this.goldPath[i - 1][j];
                            }
                            // if the cell is gold
                            else {
                                this.goldPath[i][j] = this.goldPath[i-1][j] + Integer.parseInt(map[i][j]);
                            }
                        }
                    }
                }
                // checking other columns
                else {
                    if (i == 0) {
                        if (this.map[i][j-1].equals("X") || this.pathTo[i][j-1].equals("X")) {
                            this.pathTo[i][j] = "X";
                            continue;
                        }

                        this.pathTo[i][j] = this.pathTo[i][j-1].concat("R");

                        // if the cell is a path
                        if (this.map[i][j].equals(".")) {
                            this.goldPath[i][j] = this.goldPath[i][j - 1];
                        }
                        // if the cell contains gold
                        else {
                            this.goldPath[i][j] = this.goldPath[i][j-1] + Integer.parseInt(this.map[i][j]);
                        }
                    }
                    // checking other cells
                    else {
                        // if the cell is not blocked off by rocks
                        if (!this.map[i][j-1].equals("X") && !this.map[i-1][j].equals("X")) {
                            if (this.map[i][j].equals(".")) {
                                this.goldPath[i][j] = Math.max(this.goldPath[i][j-1], this.goldPath[i-1][j]);
                                if (this.goldPath[i][j-1] < this.goldPath[i-1][j]) {
                                    this.pathTo[i][j] = this.pathTo[i-1][j].concat("D");
                                } else {
                                    this.pathTo[i][j] = this.pathTo[i][j-1].concat("R");
                                }
                            } else {
                                this.goldPath[i][j] = Math.max(this.goldPath[i][j-1], this.goldPath[i-1][j]) + Integer.parseInt(this.map[i][j]);
                                if (this.goldPath[i][j-1] < this.goldPath[i-1][j]) {
                                    this.pathTo[i][j] = this.pathTo[i-1][j].concat("D");
                                } else {
                                    this.pathTo[i][j] = this.pathTo[i][j-1].concat("R");
                                }
                            }
                        }
                        // the cell is blocked from the upper and left --> unreachable
                        else if (this.map[i][j-1].equals("X") && this.map[i-1][j].equals("X")) {
                            this.pathTo[i][j] = "X";
                        }
                        // the cell is blocked only from the left --> get the upper cell value
                        else if (this.map[i][j-1].equals("X")) {
                            this.pathTo[i][j] = this.pathTo[i-1][j].concat("D");
                            if (this.map[i][j].equals(".")) {
                                this.goldPath[i][j] = this.goldPath[i-1][j];
                            } else {
                                this.goldPath[i][j] = this.goldPath[i-1][j] + Integer.parseInt(this.map[i][j]);
                            }
                        }
                        // the cell is blocked only from the upper --> get the left cell value
                        else {
                            this.pathTo[i][j] = this.pathTo[i][j-1].concat("R");
                            if (map[i][j].equals(".")) {
                                this.goldPath[i][j] = this.goldPath[i][j-1];
                            } else {
                                this.goldPath[i][j] = this.goldPath[i][j-1] + Integer.parseInt(this.map[i][j]);
                            }
                        }
                    }
                }

                // record the maxiumum amount of collectable gold as reaching
                // a certain cell and the shortest path to that amount of gold
                if (!recordTable.containsKey(this.goldPath[i][j])) {
                    this.recordTable.put(this.goldPath[i][j], this.pathTo[i][j]);
                } else {
                    // if there is new path that has shorter length to get that same amount of gold
                    if (this.recordTable.get(this.goldPath[i][j]).length() > this.pathTo[i][j].length()) {
                        this.recordTable.put(this.goldPath[i][j], this.pathTo[i][j]);
                    }
                }
            }
        }

        // get the maximum amount of collectable gold and the shortest way to collect it
        int maxGold = Collections.max(this.recordTable.keySet());
        String minPath = this.recordTable.get(maxGold);
        System.out.printf("Steps: %d, gold: %d, path: %s\n", minPath.length(), maxGold, minPath);
    }


    public static void main(String[] arg) {
        // get start time to calculate processing time
        long start = System.nanoTime();

        long beforeUsedMem=Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        try {
            new TheGreedyGnomeTri("map1.txt");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // get finish time and calculate processing time
        long finish = System.nanoTime();
        long timeElapsed = finish - start;
        System.out.println("Processing time: " + timeElapsed + " nanoseconds.");
//        System.out.println("Processing time: " + timeElapsed/1000000 + " milliseconds.");

        long afterUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long actualMemUsed = afterUsedMem - beforeUsedMem;

        System.out.printf("Memory used: %d kB\n", actualMemUsed/1000);
    }
}
