package Interface.NotificationRelated;

import DatabaseAPI.AccountAPI;
import Entities.Account;
import Interface.Menu.MainMenu;
import Interface.Operation;
import jakarta.persistence.EntityManagerFactory;

import java.util.Scanner;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Handles the notification 'Add a friend' for both adults and children
 */
public class AddFriend implements Operation {
    private Operation nextOperation;
    private AccountAPI accountAPI;
    private Logger logger = Logger.getLogger(AddFriend.class.getName());
    private Long accId, friendId;
    public AddFriend(String args) {
        parseArgs(args);
    }

    public void run(EntityManagerFactory emf) {
        accountAPI = new AccountAPI(logger, emf);
        Account acc = accountAPI.getAccountById(accId), friend = accountAPI.getAccountById(friendId);
        System.out.println("Do you want to add " + friend.getUsername() + " as your friend to your account? (y/n)");
        Scanner in = new Scanner(System.in);
        String answer = in.nextLine().trim();
        Set<Account> facc = acc.getFriends(), facc2 = friend.getFriends();
        facc.add(friend); facc2.add(acc);
        if (answer.equalsIgnoreCase("y")) {
            acc = accountAPI.updateAccountFriends(acc, facc);
            friend = accountAPI.updateAccountFriends(friend, facc2);
        }
        setNextOperation(new MainMenu(acc));
    }

    private void parseArgs(String args) {
        String[] argParts = args.split(";");
        friendId = Long.parseLong(argParts[0]);
        accId = Long.parseLong(argParts[1]);
    }

    private void setNextOperation(Operation nextOperation) {
        this.nextOperation = nextOperation;
    }
    public Operation getNextOperation() {
        return nextOperation;
    }
}
