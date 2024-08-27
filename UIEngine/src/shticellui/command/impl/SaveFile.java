package shticellui.command.impl;

import command.api.Engine;
import dto.SaveLoadFileDto;
import shticellui.command.api.Command;

import java.util.Scanner;

public class SaveFile implements Command {
    @Override
    public boolean execute(Engine engine) {
        Scanner scanner = new Scanner(System.in);
        SaveLoadFileDto saveFile;

        while (true) {
            try {
                // Check if a file is loaded before proceeding
                engine.checkIfFileLoaded();
            } catch (IllegalStateException e) {
                System.out.println(e.getMessage());
                return false; // Exit if no file is loaded
            }

            System.out.print("Enter the full path of the file to save without extension (or 'back' to return to the main menu): ");
            String path = scanner.nextLine();

            if (path.equalsIgnoreCase("back")) {
                return false; // Return to the main menu
            }

            path += ".ser";

            System.out.println("Saving system state...");

            saveFile = engine.saveState(path);
            if (saveFile.isSucceeded()) {
                System.out.println("File saved successfully!");
                break; // Exit loop if the file is saved successfully
            } else {
                System.out.println("File saving failed!");
                System.out.println(saveFile.getMessage());
            }
        }

        return true;
    }
}

