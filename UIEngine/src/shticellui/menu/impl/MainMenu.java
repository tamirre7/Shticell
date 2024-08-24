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
        System.out.println("6. Exit Application");
    }

    @Override
    public void handleSelection(int option) {
        switch (option) {
            case 1:
                Command loadFile = new LoadFile();
                loadFile.execute(engine);
                break;
            case 2:
                Command displaySheet = new DisplayCurrentSheet();
                displaySheet.execute(engine);
                break;
            case 3:
                Command displayCell = new DisplayCellValue();
                displayCell.execute(engine);
                break;
            case 4:
               Command updateCell = new UpdateCellValue();
                updateCell.execute(engine);
               break;
            case 5:
                Command displayVer = new DisplaySheetVersions();
                displayVer.execute(engine);
                break;
            case 6:
                Command exitApp = new ExitApp();
                exitApp.execute(engine);
                break;
            default:
                System.out.println("Invalid option: Please enter a number between 1 and 6. You have entered:" + option);
        }
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            display();
            System.out.print("Please select an option: ");
            int option;

            // Validate input
            while (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a number between 1 and 6.");
                scanner.next(); // Clear the invalid input
            }
            option = scanner.nextInt();

            if (option == 6)  // Exit option
                running = false;
            handleSelection(option);
        }
        scanner.close();
    }
}
