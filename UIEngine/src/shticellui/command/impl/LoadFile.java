package shticellui.command.impl;

import dto.LoadDto;
import engine.api.Engine;
import shticellui.command.api.Command;

import java.util.Scanner;

public class LoadFile implements Command {
    @Override
    public void execute(Engine engine) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Please enter a file path:");
        String path = scanner.nextLine();
        System.out.println("Loading file...");
        LoadDto loadDetails = engine.loadFile(path);
       if (loadDetails.isSucceeded())
           System.out.println("File loaded successfully!");
       else{
           System.out.println("File loading failed!");
           System.out.println(loadDetails.getMessage());
       }

    }
}
