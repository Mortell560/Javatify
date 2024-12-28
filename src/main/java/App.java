import DatabaseAPI.NotificationAPI;
import DatabaseAPI.SongAPI;
import Entities.Account;
import Entities.Notification;
import Interface.LoginRegister.LoggingOperation;
import Interface.Menu.SettingsMenu;
import Interface.Operation;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.List;
import java.util.logging.Logger;

/**
 * Main class for the App Javatify <br/>
 * Use the run() method to start
 */
public class App {
    private static final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("Javatify");
    private final Logger logger = Logger.getLogger(App.class.getName());
    private final String dataPath = "src/main/resources/spotify_millsongdata.csv"; // TODO: Change before handing out
    private final SongAPI songAPI;
    private final NotificationAPI notificationAPI;
    private Account currAccount = null;

    public App() {
        logger.info(App.class.getName() + ": Constructor Started");
        songAPI = new SongAPI(logger, entityManagerFactory);
        songAPI.loadSongs(dataPath);
        notificationAPI = new NotificationAPI(logger, entityManagerFactory);
        logger.info(App.class.getName() + ": Constructor Finished");
        showLogo();
    }

    public Account getCurrAccount() {
        return currAccount;
    }

    public void setCurrAccount(Account currAccount) {
        this.currAccount = currAccount;
    }

    /**
     * Main loop method
     */
    public void run() {
        Operation op = new LoggingOperation();
        op.run(entityManagerFactory);
        setCurrAccount(((LoggingOperation) op).getCurrUser());
        if (currAccount != null) {
            showNotifs();
        }

        while (op.getNextOperation() != null) {
            op = op.getNextOperation();
            op.run(entityManagerFactory);
            if (op instanceof LoggingOperation) {
                setCurrAccount(((LoggingOperation) op).getCurrUser());
                if (currAccount != null) {
                    showNotifs();
                }
            }
        }
        leaveScreen();
    }


    private void showNotifs(){
        List<Notification> notifs = notificationAPI.getAllUnreadNotifForAcc(currAccount);
        if (notifs.isEmpty()){
            System.out.println("No new notifications found for account: " + currAccount.getUsername());
            return;
        }
        System.out.println("New notifications (size: " + notifs.size() + "):");
        notifs.forEach(System.out::println);
    }

    /**
     * Shows the logo of our app
     */
    private void showLogo() {
        String sb = "\n".repeat(10) + // I know that flush() exists but it doesn't work for all cases
                "*".repeat(20) + "\n" + "*" + "                  " + "*" + "\n" + "*" + "     Javatify     " + "*" + "\n" + "*" + "                  " + "*" + "\n" + "*".repeat(20) + "\n";
        System.out.println(sb);
    }

    /**
     * Shows the leaving screen of our app
     */
    private void leaveScreen() {
        String sb = "\n".repeat(5) + "Thanks for using Javatify!";
        System.out.println(sb);

    }

}
