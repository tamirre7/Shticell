package shticellui;

import engine.api.Engine;
import engine.impl.EngineImpl;
import shticellui.menu.api.Menu;
import shticellui.menu.impl.MainMenu;

public class Main {
    public static void main(String[] args) {
        Engine engine = new EngineImpl();
        Menu menu = new MainMenu(engine);
        menu.start();
    }
}
