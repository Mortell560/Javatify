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
        System.out.println("3. Listen to playlist or create playlist");
        System.out.println("4. Create a BlindTest or Play one");
        System.out.println("5. Logout");
        System.out.println("6. Exit");

        int choice = Safeguards.getInputInterval(1, 6);

        switch (choice) {
            case 2 -> setNextOperation(new MusicSearchMenu(account));
            case 5 -> setNextOperation(new LoggingOperation());
            case 6 -> setNextOperation(null);
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
