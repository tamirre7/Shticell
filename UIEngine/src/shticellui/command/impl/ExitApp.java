package shticellui.command.impl;

import dto.ExitDto;
import command.api.Engine;
import shticellui.command.api.Command;

public class ExitApp implements Command {
    @Override
    public void execute(Engine engine) {
       ExitDto exitDto = engine.exitSystem();
       System.out.println(exitDto.getMessage());
    }
}
