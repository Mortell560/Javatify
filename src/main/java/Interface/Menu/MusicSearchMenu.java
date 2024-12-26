package Interface.Menu;

import Entities.Account;
import Entities.Song;
import Interface.Operation;
import Interface.SongRelated.MusicConsulting;
import Interface.SongRelated.SongSearch;
import Utils.Safeguards;
import jakarta.persistence.EntityManagerFactory;

import java.util.InputMismatchException;
import java.util.Scanner;

public class MusicSearchMenu implements Operation {
    private Operation nextOperation;
    private Account account;
    public MusicSearchMenu(Account account) {
        this.account = account;
    }

    public Operation getNextOperation() {
        return nextOperation;
    }

    public void setNextOperation(Operation nextOperation) {
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
        }
        else {
            setNextOperation(new MainMenu(account));
        }

    }


}
