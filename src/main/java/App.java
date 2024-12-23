import DatabaseAPI.AccountAPI;
import DatabaseAPI.SongAPI;
import Entities.Account;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Main class for the App Javatify
 * Use the run() method to start
 */
public class App {
    private static final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("Javatify");
    private final Logger logger = Logger.getLogger(App.class.getName());
    private final String dataPath = "src/main/resources/spotify_millsongdata.csv"; // TODO: Change before handing out
    private final SongAPI songAPI;
    private final AccountAPI accountAPI;

    public App() {
        logger.info(App.class.getName() + ": Constructor Started");
        songAPI = new SongAPI(logger, entityManagerFactory);
        accountAPI = new AccountAPI(logger, entityManagerFactory);
        songAPI.loadSongs(dataPath);
        logger.info(App.class.getName() + ": Constructor Finished");
        showLogo();

    }

    /**
     * Main loop method
     */
    public void run() {

    }

    /**
     * Shows the logo of our app
     */
    private void showLogo() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n\n\n\n\n"); // I know that flush() exists but it doesn't work for all cases
        for (int i = 0; i < 20; i++) {
            sb.append("*");
        }
        sb.append("\n");
        sb.append("*").append("                  ").append("*").append("\n");
        sb.append("*").append("     Javatify     ").append("*").append("\n");
        sb.append("*").append("                  ").append("*").append("\n");
        for (int i = 0; i < 20; i++) {
            sb.append("*");
        }
        sb.append("\n");
        System.out.println(sb);
    }

}
