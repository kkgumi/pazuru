package pazuru;


import pazuru.exception.NoMatchRuleException;
import pazuru.exception.NoResourceException;
import pazuru.exception.UnexpectedResponseException;
import pazuru.gui.GUIMain;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    public static void main(String[] args) {
        Logger logger = Logger.getLogger("pazuru");
        logger.setLevel(Level.FINE);
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.FINE);
        logger.addHandler(consoleHandler);
        if (args.length != 4 && args.length != 6) {
            GUIMain.main(null);
            return;
        }
        String url = args[0];
        String username = args[1];
        String password = args[2];
        String savingPath = args[3];
        int startPage = 1;
        int endPage = -1;
        if (args.length == 6) {
            startPage = Integer.parseInt(args[4]);
            endPage = Integer.parseInt(args[5]);
        }
        try {
            Core core = new Core(url, username, password, savingPath);
            if (!core.authenticate()) logger.info("fail to authenticate");
            else logger.config("authentication success");
            logger.info("list success; " + core.list().size() + " image(s)");
            core.save(startPage, endPage, savingPath);
        } catch (NoMatchRuleException e) {
            logger.severe("no Match Rule.");
        } catch (IOException | UnexpectedResponseException e) {
            logger.severe("Network error.");
            e.printStackTrace();
        } catch (NoResourceException e) {
            logger.severe("No resource. Consider to check your credential.");
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }
}
