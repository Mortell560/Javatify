package Interface.NotificationRelated;

import DatabaseAPI.AccountAPI;
import DatabaseAPI.BlindTestAPI;
import Entities.Account;
import Entities.BlindTest;
import Interface.Menu.MainMenu;
import Interface.Operation;
import jakarta.persistence.EntityManagerFactory;

import java.util.Scanner;
import java.util.logging.Logger;

public class AddBlindtest implements Operation {
    private Operation nextOperation;
    private Long accId, btId;
    private AccountAPI accountAPI;
    private BlindTestAPI blindTestAPI;
    private Logger logger = Logger.getLogger(AddBlindtest.class.getName());
    public AddBlindtest(String args) {
        parseArgs(args);
    }


    public void run(EntityManagerFactory emf) {
        accountAPI = new AccountAPI(logger, emf);
        blindTestAPI = new BlindTestAPI(logger, emf);
        Account acc = accountAPI.getAccountById(accId);
        BlindTest bt = blindTestAPI.getBlindTestById(btId);
        System.out.println("Do you want to add the blindtest " + bt.getName() + " to your account? (y/n)");
        Scanner in = new Scanner(System.in);
        String answer = in.nextLine().trim();
        if (answer.equalsIgnoreCase("y")) {
            blindTestAPI.addInvitedAcc(bt, acc);
        }
        setNextOperation(new MainMenu(acc));
    }

    private void parseArgs(String args) {
        String[] parts = args.split(";");
        accId = Long.parseLong(parts[0]);
        btId = Long.parseLong(parts[1]);
    }

    private void setNextOperation(Operation nextOperation) {
        this.nextOperation = nextOperation;
    }

    public Operation getNextOperation() {
        return nextOperation;
    }
}
