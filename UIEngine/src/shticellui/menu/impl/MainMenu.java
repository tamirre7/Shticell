package shticellui.menu.impl;

import shticellui.command.impl.*;
import command.api.Engine;
import shticellui.command.api.Command;
import shticellui.menu.api.Menu;

import java.util.Scanner;

public class MainMenu implements Menu {
    private final Engine engine;

    public MainMenu(Engine engine) {
        this.engine = engine;
    }

    @Override
    public void display(){
        System.out.println("==== Main Menu ====");
        System.out.println("1. Load File");
        System.out.println("2. Display Current SpreadSheet");
        System.out.println("3. Display a Single Cell Details");
        System.out.println("4. Update Cell Value");
        System.out.println("5. Display Spreadsheet Versions");
        System.out.println("6. Save System State");
        System.out.println("7. Load System State");
        System.out.println("8. Exit Application");
    }

    @Override
    public void handleSelection(int option) {
        try {
            Command command;
            switch (option) {
                case 1:
                    command = new LoadFile();
                    break;
                case 2:
                    command = new DisplayCurrentSheet();
                    break;
                case 3:
                    command = new DisplayCellValue();
                    break;
                case 4:
                    command = new UpdateCellValue();
                    break;
                case 5:
                    command = new DisplaySheetVersions();
                    break;
                case 6:
                    command = new SaveFile();
                    break;
                case 7:
                    command = new LoadSavedFile();
                    break;
                case 8:
                    command = new ExitApp();
                    break;
                default:
                    System.out.println("Invalid option: Please enter a number between 1 and 8. You have entered:" + option);
                    System.out.println();
                    return;
            }

            boolean continueExecution = command.execute(engine);
            if (!continueExecution) {
                System.out.println("Returning to main menu...");
            }
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
            System.out.println();
        }
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("Welcome to Shticell!");
        System.out.println();

        while (running) {
            display();
            System.out.print("Please select an option: ");
            System.out.println();
            int option;

            // Validate input
            while (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a number between 1 and 6.");
                scanner.next(); // Clear the invalid input
            }
            option = scanner.nextInt();

            if (option == 8)  // Exit option
                running = false;
            handleSelection(option);
        }
        scanner.close();
    }
}
