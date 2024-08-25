package shticellui.command.impl;

import dto.LoadDto;
import command.api.Engine;
import shticellui.command.api.Command;

import java.io.FileNotFoundException;
import java.util.Scanner;

public class LoadFile implements Command {
    @Override
    public boolean execute(Engine engine) {
        Scanner scanner = new Scanner(System.in);
        LoadDto loadDetails;

        do {
            System.out.println("Please enter a file path (or back to return to main menu):");
            String path = scanner.nextLine();

            if (path.equals("back") | path.equals("BACK")) {
                return false;
            }

            System.out.println("Loading file...");

            // Load file and handle the result through LoadDto
            loadDetails = engine.loadFile(path);

            if (loadDetails.isSucceeded()) {
                System.out.println("File loaded successfully!");
            } else {
                System.out.println("File loading failed!");
                System.out.println(loadDetails.getMessage());
            }

        } while (!loadDetails.isSucceeded());// Continue looping until the file is loaded successfully
        return true;
    }
}

