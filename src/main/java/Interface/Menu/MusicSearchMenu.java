package Interface.Menu;

import Entities.Account;
import Interface.Operation;
import Interface.SongRelated.SongSearch;
import Utils.Safeguards;
import jakarta.persistence.EntityManagerFactory;

/**
 * Menu used to select the search mode for the SongSearch
 *
 * @see SongSearch
 */
public class MusicSearchMenu implements Operation {
    private final Account account;
    private Operation nextOperation;

    public MusicSearchMenu(Account account) {
        this.account = account;
    }

    public Operation getNextOperation() {
        return nextOperation;
    }

    private void setNextOperation(Operation nextOperation) {
        this.nextOperation = nextOperation;
    }

    public void run(EntityManagerFactory emf) {
        System.out.println("Music Search Menu, please select an option: ");
        System.out.println("1. Title based search");
        System.out.println("2. Artist based search");
        System.out.println("3. Most popular search");
        System.out.println("4. Lyrics based search");
        System.out.println("5. Return to main menu");

        int choice = Safeguards.getInputInterval(1, 5);

        if (choice <= 4) {
            setNextOperation(new SongSearch(account, choice));
        } else {
            setNextOperation(new MainMenu(account));
        }

    }


}
