package shticellui.command.impl;

import command.api.Engine;
import dto.SaveLoadFileDto;
import shticellui.command.api.Command;

import java.util.Scanner;

public class LoadSavedFile implements Command {
    @Override
    public boolean execute(Engine engine) {
        Scanner scanner = new Scanner(System.in);
        SaveLoadFileDto LoadedFile;
        do {
            System.out.print("Enter the full path of the file to load without extension (or back to return to main menu): ");
            String path = scanner.nextLine();

            if (path.equals("back") | path.equals("BACK")) {
                return false;
            }

            path += ".ser";

            System.out.println("Loading system state...");

            LoadedFile = engine.loadSavedState(path);
            if (LoadedFile.isSucceeded()) {
                System.out.println("File loaded successfully!");
            } else {
                System.out.println("File loading failed!");
                System.out.println(LoadedFile.getMessage());
            }
        } while (!LoadedFile.isSucceeded());

        return true;
    }
}
