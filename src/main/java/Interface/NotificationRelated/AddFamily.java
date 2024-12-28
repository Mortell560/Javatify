package Interface.NotificationRelated;

import DatabaseAPI.AccountAPI;
import Entities.Account;
import Interface.Menu.MainMenu;
import Interface.Operation;
import jakarta.persistence.EntityManagerFactory;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Logger;

public class AddFamily implements Operation {
    private Long accId, familyId;
    private Logger logger = Logger.getLogger(AddFamily.class.getName());
    private AccountAPI accountAPI;
    private Operation nextOperation;

    public AddFamily(String args) {
        parseArgs(args);
    }

    private void parseArgs(String args) {
        String[] parts = args.split(";");
        familyId = Long.parseLong(parts[0]);
        accId = Long.parseLong(parts[1]);
    }

    public void run(EntityManagerFactory emf) {
        accountAPI = new AccountAPI(logger, emf);
        Account acc = accountAPI.getAccountById(accId), family = accountAPI.getAccountById(familyId);
        Scanner scanner = new Scanner(System.in);
        System.out.println("Do you want to add " + family.getUsername() + " as a family member? (y/n)");
        String answer = scanner.nextLine().trim();
        Set<Account> facc = acc.getFamily(), facc2 = family.getFamily();
        facc.add(family); facc2.add(acc);
        if (answer.equalsIgnoreCase("y")) {
            acc = accountAPI.updateAccountFamily(acc, facc);
            family = accountAPI.updateAccountFamily(family, facc2);
        }
        setNextOperation(new MainMenu(acc));
    }

    private void setNextOperation(Operation nextOperation) {
        this.nextOperation = nextOperation;
    }

    public Operation getNextOperation() {
        return nextOperation;
    }
}
