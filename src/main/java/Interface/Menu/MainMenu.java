package Interface.Menu;

import Entities.Account;
import Interface.LoginRegister.LoggingOperation;
import Interface.Operation;
import Utils.Safeguards;
import jakarta.persistence.EntityManagerFactory;

public class MainMenu implements Operation {
    private Operation nextOperation;
    private final Account account;

    public MainMenu(Account account) {
        this.account = account;
    }

    public void run(EntityManagerFactory factory) {
        System.out.println("Please select what you want to do: ");
        System.out.println("1. Account Settings");
        System.out.println("2. Search for a music");
        System.out.println("3. Manage playlists");
        System.out.println("4. Manage blind tests");
        System.out.println("5. Read notifications");
        System.out.println("6. Logout");
        System.out.println("7. Exit");

        int choice = Safeguards.getInputInterval(1, 7);

        switch (choice) {
            case 1 -> setNextOperation(new SettingsMenu(account));
            case 2 -> setNextOperation(new MusicSearchMenu(account));
            case 3 -> setNextOperation(new PlaylistMenu(account));
            case 4 -> setNextOperation(new BlindTestMenu(account));
            case 5 -> setNextOperation(new NotificationMenu(account));
            case 6 -> setNextOperation(new LoggingOperation());
            case 7 -> setNextOperation(null);
            default -> setNextOperation(new MainMenu(account));
        }
    }

    public Operation getNextOperation() {
        return nextOperation;
    }

    private void setNextOperation(Operation nextOperation) {
        this.nextOperation = nextOperation;
    }
}
