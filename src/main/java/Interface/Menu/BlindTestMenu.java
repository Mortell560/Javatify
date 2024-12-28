package Interface.Menu;

import DatabaseAPI.BlindTestAPI;
import DatabaseAPI.BlindTestResultAPI;
import DatabaseAPI.NotificationAPI;
import Entities.Account;
import Entities.BlindTest;
import Entities.BlindTestResult;
import Entities.Notification;
import Interface.Operation;
import Interface.SongRelated.PlayBlindTest;
import Utils.Safeguards;
import jakarta.persistence.EntityManagerFactory;

import java.util.Calendar;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Handles and manages blindtests
 *
 * @see BlindTest
 */
public class BlindTestMenu implements Operation {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final Account account;
    private Operation nextOperation;
    private BlindTestAPI blindTestAPI;
    private BlindTestResultAPI resultAPI;
    private NotificationAPI notificationAPI;

    public BlindTestMenu(Account account) {
        this.account = account;
    }

    public Operation getNextOperation() {
        return nextOperation;
    }

    private void setNextOperation(Operation nextOperation) {
        this.nextOperation = nextOperation;
    }

    public void run(EntityManagerFactory emf) {
        blindTestAPI = new BlindTestAPI(logger, emf);
        notificationAPI = new NotificationAPI(logger, emf);
        resultAPI = new BlindTestResultAPI(logger, emf);
        System.out.println("Choose an action: ");
        System.out.println("1. Create a BlindTest");
        System.out.println("2. Delete a BlindTest");
        System.out.println("3. Share a BlindTest");
        System.out.println("4. Play a BlindTest");
        System.out.println("5. Get Results");
        System.out.println("6. Exit");

        int choice = Safeguards.getInputInterval(1, 6);

        switch (choice) {
            case 1 -> createBlindTest();
            case 2 -> deleteABlindTest();
            case 3 -> shareBlindtest();
            case 4 -> playTheBlind();
            case 5 -> showResults();
            case 6 -> setNextOperation(new MainMenu(account));
        }
    }

    private void playTheBlind() {
        List<BlindTest> bl = blindTestAPI.getAllAvailableToPlay(account);
        setNextOperation(new BlindTestMenu(account));
        if (bl.isEmpty()) {
            System.out.println("No playable blindtest found");
            return;
        }
        System.out.println("Choose a playlist you want to play or 0 to exit: ");
        int i = 0;
        for (BlindTest b : bl) {
            i++;
            System.out.println(i + ". - " + b);
        }
        int choice = Safeguards.getInputInterval(0, i);
        if (choice == 0) {
            System.out.println("Returning");
            return;
        }
        BlindTest b = bl.get(choice - 1);
        setNextOperation(new PlayBlindTest(account, b));
    }

    private void createBlindTest() {
        BlindTest b = new BlindTest();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Name of your blindtest: ");
        b.setName(scanner.nextLine().trim());
        b.setOwner(account);
        System.out.println("Choose the mode that you want to play with: ");
        System.out.println("1. Exact title");
        System.out.println("2. Artist");
        System.out.println("3. Multiple answers");

        b.setMODE(Safeguards.getInputInterval(1, 3));
        if (b.getMODE() == 3) {
            System.out.println("How many multiple answers should be diplayed ?: ");
            b.setNbChoices(Safeguards.getInputInteger());
        }
        blindTestAPI.createBlindTest(b);
        System.out.println("Blind Test " + b.getName() + " created");
        setNextOperation(new BlindTestMenu(account));
    }

    private void deleteABlindTest() {
        List<BlindTest> l = blindTestAPI.getAllBTForAccount(account);
        setNextOperation(new BlindTestMenu(account));
        if (l.isEmpty()) {
            System.out.println("No blindtest found");
            return;
        }
        System.out.println("Choose a blindtest you want to delete or 0 to exit: ");
        int i = 0;
        for (BlindTest b : l) {
            i++;
            System.out.println(i + ". - " + b);
        }
        int choice = Safeguards.getInputInterval(0, i);
        if (choice == 0) {
            System.out.println("Returning");
            return;
        }
        BlindTest bt = l.get(choice - 1);
        blindTestAPI.deleteBlindTest(bt);
        System.out.println("Deleted blindtest " + bt.getName());
    }

    private void shareBlindtest() {
        List<BlindTest> l = blindTestAPI.getAllBTForAccount(account);
        setNextOperation(new BlindTestMenu(account));
        if (l.isEmpty()) {
            System.out.println("No blindtest found");
            return;
        }
        System.out.println("Choose a blindtest you want to share or 0 to exit: ");
        int i = 0;
        for (BlindTest b : l) {
            i++;
            System.out.println(i + ". - " + b);
        }
        int choice = Safeguards.getInputInterval(0, i);
        if (choice == 0) {
            System.out.println("Returning");
            return;
        }
        BlindTest bt = l.get(choice - 1);
        System.out.println("Choose someone to share this with: ");
        Set<Account> friendsAndFam = account.getFriends();
        friendsAndFam.addAll(account.getFamily());
        int j = 0;
        for (Account f : friendsAndFam) {
            System.out.println(j + ". - " + f.getUsername());
            j++;
        }
        int choice2 = Safeguards.getInputInterval(0, j);
        Account share = friendsAndFam.stream().toList().get(choice2);

        Notification n = new Notification();
        n.setTime(Calendar.getInstance());
        n.setAccount(share);
        n.setOperation(5);
        n.setMessage(account.getUsername() + " wants to share a blindtest with you !");
        n.setRead(false);
        n.setArgs(share.getId() + ";" + bt.getId());
        notificationAPI.createNotification(n);
        System.out.println("Blindtest shared !");

    }

    private void showResults() {
        List<BlindTest> bl = blindTestAPI.getAllAvailableToPlay(account);
        setNextOperation(new BlindTestMenu(account));
        if (bl.isEmpty()) {
            System.out.println("No blindtest found");
            return;
        }
        System.out.println("Select a blindtest that you want to see the results of :");
        int i = 0;
        for (BlindTest b : bl) {
            System.out.println(i + ". - " + b);
            i++;
        }
        int choice = Safeguards.getInputInterval(0, i);
        BlindTest b = bl.get(choice);
        List<BlindTestResult> brl = resultAPI.getAllBlindTestResultForBT(b);
        System.out.println("Results :");
        System.out.println("Time | BlindTest | Username | Success/Total");
        for (BlindTestResult br : brl) {
            System.out.println(br);
        }
    }

}
