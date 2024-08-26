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
        do {
            System.out.print("Enter the full path of the file to save without extension (or back to return to main menu): ");
            String path = scanner.nextLine();

            if (path.equals("back") | path.equals("BACK")) {
                return false;
            }

            path += ".ser";

            System.out.println("Saving system state...");

             saveFile = engine.saveState(path);
            if (saveFile.isSucceeded()) {
                System.out.println("File saved successfully!");
            } else {
                System.out.println("File saving failed!");
                System.out.println(saveFile.getMessage());
            }
        } while (!saveFile.isSucceeded());

        return true;
    }
}
