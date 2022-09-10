package maze;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static int nodes; // nodes of a spanning tree
    private static int widthInput; // input width
    private static int lengthInput; // input length
    private static int width; // width
    private static int length; // length
    private static int step;

    private static int[][] currentMaze;

    static void generateNewMaze() {
        System.out.println("Please, enter the size of a maze");

        lengthInput = Integer.parseInt(scanner.nextLine());
        widthInput = lengthInput; // in test maze is a square, but it doesn't matter

        int[][] adjacencyMatrix = generateAdjacencyMatrix();
        int[][] spanningTreeMatrix = primAlgorithm(adjacencyMatrix);
        int[][] mazeInner = createMazeFromSpanningTreeMatrix(spanningTreeMatrix);
        currentMaze = createMaze(mazeInner);
        displayMaze(currentMaze);
    }

    static void loadMaze() {
        String fileName = scanner.nextLine();

        File file = new File("/" + fileName);

        int indexLength = 0;
        int width = 0;

        String line;
        ArrayList<Integer> row;
        HashMap<Integer, ArrayList<Integer>> map = new HashMap<>();

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNext()) {

                line = scanner.nextLine();

                if (indexLength == 0) {
                    width = line.length();
                }

                row = new ArrayList<>();
                for (int i = 0; i < width; i++) {
                    if (line.charAt(i) == '1') {
                        row.add(1);
                    } else {
                        row.add(0);
                    }
                }

                map.put(indexLength, row);
                indexLength++;
            }
        } catch (FileNotFoundException e) {
            System.out.println("The file " + fileName + " does not exist");
        }

        if (indexLength <= 3 || width <= 3) {
            System.out.println("Cannot load the maze. It has an invalid format");
        } else {

            currentMaze = new int[indexLength][width];


            for (int i = 0; i < currentMaze.length; i++) {
                for (int j = 0; j < currentMaze[0].length; j++) {
                    if (map.get(i).get(j) == 1) {
                        currentMaze[i][j] = 1;
                    }
                }
            }
        }
    }

    static void saveMaze() {
        String fileName = scanner.nextLine();
        File file = new File("/" + fileName);

        try (PrintWriter printWriter = new PrintWriter(file)) {
            for (int i = 0; i < currentMaze.length; i++) {
                for (int j = 0; j < currentMaze[0].length; j++) {
                    if (currentMaze[i][j] == 1) {
                        printWriter.print(1);
                    } else {
                        printWriter.print(0);
                    }

                    if (j == currentMaze[0].length - 1 && i != currentMaze.length - 1) {
                        printWriter.println();
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("The file " + fileName + " does not exist");
        }
    }

    static void displayMaze(int[][] maze) {
        if (maze.length == 0) {
            maze = currentMaze;
        }

        String space = "  ";
        String block = "\u2588\u2588";
        String escape = "//";

        for (int[] ints : maze) {
            for (int j = 0; j < maze[0].length; j++) {
                if (ints[j] == 1) {
                    System.out.print(block);
                } else if (ints[j] == 4) {
                    System.out.print(escape);
                }
                else {
                    System.out.print(space);
                }

                if (j == maze[0].length - 1) {
                    System.out.println();
                }
            }
        }
    }

    // generate adjacency matrix with random weight
    private static int[][] generateAdjacencyMatrix() {
        if (widthInput % 2 == 0) {
            width = widthInput - 1;
        } else {
            width = widthInput;
        }

        if (lengthInput % 2 == 0) {
            length = lengthInput - 1;
        } else {
            length = lengthInput;
        }

        nodes = width / 2 * (length / 2); // 4 * 3
        step = length / 2;

        int[][] adjacencyMatrix = new int[nodes][nodes];

        // generate adjacency matrix
        for (int i = 0; i < nodes; i++) {
            if (i + 1 == nodes) {
                break;
            }

            if ((i + 1) % step != 0) {
                adjacencyMatrix[i][i + 1] = (int) (Math.random() * 4 + 1);
            }

            if (i + step < nodes) {
                adjacencyMatrix[i][i + step] = (int) (Math.random() * 4 + 1);
            }
        }

        return adjacencyMatrix;
    }

    // make spanning tree using Prim algorithm from adjacency matrix
    private static int[][] primAlgorithm(int[][] adjacencyMatrix) {
        int[] nodesInSpanningTree = new int[nodes];
        nodesInSpanningTree[0] = 1;

        // if 0 is always an entrance and 11 is always an exit
        // use Prim algorithm to find Minimum Spanning Tree (aka free cells)
        int min;
        int[] minEdge = new int[2];
        while (!treeIsFinished(nodesInSpanningTree)) {

            // find minimum edge
            min = 5;
            for (int i = 0; i < nodes; i++) {
                if (nodesInSpanningTree[i] == 1) {

                    if (i + 1 == nodes) {
                        break;
                    }

                    // also check if the added node is new
                    if ((i + 1) % step != 0 && nodesInSpanningTree[i + 1] == 0 && adjacencyMatrix[i][i + 1] < min) {
                        min = adjacencyMatrix[i][i + 1];
                        minEdge[0] = i;
                        minEdge[1] = i + 1;
                    }

                    // also check if the added node is new
                    if (i + step < nodes && nodesInSpanningTree[i + step] == 0 && adjacencyMatrix[i][i + step] < min) {
                        min = adjacencyMatrix[i][i + step];
                        minEdge[0] = i;
                        minEdge[1] = i + step;
                    }
                }
            }

            // add node to the tree
            nodesInSpanningTree[minEdge[1]] = 1;

            // mark edge as added in the matrix and set weight to 5 (max weight in a matrix is 4)
            adjacencyMatrix[minEdge[0]][minEdge[1]] = 5;
        }

        return adjacencyMatrix;
    }

    // create maze from spanning tree but without external walls
    private static int[][] createMazeFromSpanningTreeMatrix(int[][] spanningTreeMatrix) {

        int[][] maze = new int[length / 2 + length / 2 - 1][width / 2 + width / 2 - 1];
        maze[0][0] = -1; // means empty

        int indexLength = 0;
        int indexWidth = 0;
        for (int i = 0; i < nodes; i += 1) {

            if (i + 1 == nodes) {
                break;
            }

            if (i > 0 && i % step == 0) {
                indexLength = 0;
                indexWidth += 2;
            }

            if (spanningTreeMatrix[i][i + 1] == 5) {
                maze[indexLength][indexWidth] = -1;
                maze[++indexLength][indexWidth] = -1;
                maze[++indexLength][indexWidth] = -1;
            } else {
                indexLength += 2;
            }
        }

        indexLength = 0;
        indexWidth = 0;
        for (int i = 0; i < nodes; i += 1) {

            if (i + step >= nodes) {
                break;
            }

            if (i > 0 && i % step == 0) {
                indexLength = 0;
                indexWidth += 2;
            }

            if (spanningTreeMatrix[i][i + step] == 5) {
                maze[indexLength][indexWidth] = -1;
                maze[indexLength][++indexWidth] = -1;
                maze[indexLength][++indexWidth] = -1;
                // set back
                indexWidth -=2;
            }
            // set forward
            indexLength += 2;
        }

        return maze;
    }

    // make maze: 1 is a wall, 0 is empty cell
    private static int[][] createMaze(int[][] maze){
        int[][] mazeToSave = new int[lengthInput][widthInput];

        saveLine(mazeToSave, 0);
        mazeToSave[0][widthInput - 2] = 1;
        mazeToSave[0][widthInput - 1] = 1;

        int indexLength;
        int indexWidth;

        for (int i = 0; i < maze.length; i++) {

            indexLength = i + 1;
            for (int j = 0; j < maze[0].length; j++) {

                if (j == 0) {
                    indexWidth = j;
                } else {
                    indexWidth = j + 1;
                }

                // drawing
                if (j == 0 && i != 0) {
                    mazeToSave[indexLength][indexWidth++] = 1;
                }

                if (j == 0 && i == 0) {
                    indexWidth++;
                }

                if (maze[i][j] == 0) {
                    mazeToSave[indexLength][indexWidth++] = 1;
                } else {
                    indexWidth++;
                }

                if (j == maze[0].length - 1) {
                    if (width != widthInput) {
                        mazeToSave[indexLength][indexWidth + 1] = 1;
                        indexWidth++;
                    }
                    mazeToSave[indexLength][indexWidth] = 1;
                }
            }
        }
        saveLine(mazeToSave, length - 1);
        mazeToSave[length - 1][widthInput - 1] = 1;

        if (length != lengthInput) {
            saveLine(mazeToSave, lengthInput - 1);
            mazeToSave[lengthInput - 1][widthInput - 1] = 1;
        }

        return mazeToSave;
    }

    private static void saveLine(int[][] mazeToSave, int row) {
        for (int i = 0; i < widthInput - 2; i++) {
            mazeToSave[row][i] = 1;
        }
    }

    private static boolean treeIsFinished(int[] nodesInSpanningTree) {
        for (int j : nodesInSpanningTree) {
            if (j == 0) {
                return false;
            }
        }
        return true;
    }

    // add escape path to the maze
    static void findEscape() {

        int[][] mazeWithPath = Arrays.stream(currentMaze).map(int[]::clone).toArray(int[][]::new);
        // 4 is a final way
        // 3 is a dead end
        mazeWithPath[1][0] = 4;
        mazeWithPath[1][1] = 4;

        int indexLength = 1;
        int indexWidth = 1;

        boolean stopRight = false;
        boolean stopDown = false;

        while (indexLength != mazeWithPath.length - 1 || indexWidth != mazeWithPath[0].length - 2) {

            if (stopRight && stopDown) {
                mazeWithPath[indexLength][indexWidth] = 3;
                stopRight = false;
                stopDown = false;

                if (mazeWithPath[indexLength - 1][indexWidth] == 1 || mazeWithPath[indexLength - 1][indexWidth] == 3) {
                    indexWidth--;
                } else {
                    indexLength--;
                }
            }

            // check width
            for (int i = indexWidth; i < mazeWithPath[0].length; i ++) {
                if (i + 1 < mazeWithPath[0].length && mazeWithPath[indexLength][i + 1] != 1 && mazeWithPath[indexLength][i + 1] != 3) {
                    mazeWithPath[indexLength][i + 1] = 4;
                } else {
                    if (i != indexWidth) {
                        stopDown = false;
                    }
                    indexWidth = i;
                    stopRight = true;
                    break;
                }
            }

            // check length
            for (int i = indexLength; i < mazeWithPath.length; i ++) {
                if (i + 1 < mazeWithPath.length && mazeWithPath[i + 1][indexWidth] != 1
                        && mazeWithPath[i + 1][indexWidth] != 3) {
                    mazeWithPath[i + 1][indexWidth] = 4;
                } else {
                    if (i != indexLength) {
                        stopRight = false;
                    }
                    indexLength = i;
                    stopDown = true;
                    break;
                }
            }
        }

        displayMaze(mazeWithPath);
    }
}
