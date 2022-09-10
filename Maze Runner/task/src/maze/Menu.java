package maze;

import java.util.Scanner;

import static maze.Main.*;

public class Menu {
    private final Scanner scanner = new Scanner(System.in);
    private boolean fullMenu = false;

    public static void main(String[] args) {
        Menu menu = new Menu();

        while (true) {
            if (!menu.fullMenu) {
                menu.printPartMenu();
            } else {
                menu.printFullMenu();
            }
            int command = menu.inputCommand();
            menu.doCommand(command);
        }
    }

    void printFullMenu() {
        System.out.println("=== Menu ===");
        System.out.println("1. Generate a new maze");
        System.out.println("2. Load a maze");
        System.out.println("3. Save the maze");
        System.out.println("4. Display the maze");
        System.out.println("5. Find the escape.");
        System.out.println("0. Exit");
    }

    private void printPartMenu() {
        System.out.println("=== Menu ===");
        System.out.println("1. Generate a new maze");
        System.out.println("2. Load a maze");
        System.out.println("0. Exit");
    }

    private int inputCommand() {
        boolean correctCommand = false;
        int command = -1;
        while(!correctCommand) {
            command = Integer.parseInt(scanner.nextLine());

            if (fullMenu && (command < 0 || command > 5)) {
                System.out.println("Incorrect option. Please try again");
            } else if (!fullMenu && (command < 0 || command > 2)) {
                System.out.println("Incorrect option. Please try again");
            } else {
                correctCommand = true;
            }
        }
        return command;
    }

    private void doCommand(int command) {
        switch(command) {
            case 1:
                generateNewMaze();
                fullMenu = true;
                break;
            case 2:
                loadMaze();
                fullMenu = true;
                break;
            case 3:
                saveMaze();
                break;
            case 4:
                displayMaze(new int[0][0]); // to print currentMaze
                break;
            case 5:
                findEscape();
                break;
            case 0:
                System.out.println("Bye!");
                System.exit(0);
                break;
            default:
                break;
        }
    }
}
