package Interface.LoginRegister;

import DatabaseAPI.AccountAPI;
import Entities.Account;
import Interface.Menu.MainMenu;
import Interface.Operation;
import jakarta.persistence.EntityManagerFactory;

import java.util.Arrays;
import java.util.Calendar;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * Handles every login or register attempt for adults
 */
public class LoggingOperation implements Operation {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private Account currUser = null;
    private Operation nextOperation = null;
    private AccountAPI accountAPI;

    public LoggingOperation() {
    }

    public void run(EntityManagerFactory factory) {
        System.out.println("Welcome to Javatify\nPlease enter an username to start (Case sensitive): ");
        accountAPI = new AccountAPI(logger, factory);
        Scanner scanner = new Scanner(System.in); // NB: Never close it, it closes also the buffer underlying
        String username = scanner.nextLine().trim();
        try {
            setCurrUser(accountAPI.getAccountByUsername(username));
        } catch (NoSuchElementException e) {
            System.out.println("Creating a new account for this username\n");
            register(username);
            return;
        }
        String password;
        do {
            System.out.println("Please enter your password or null to change of account: ");
            password = scanner.nextLine().trim();
        } while (!password.equals(currUser.getPassword()) && !password.equals("null"));

        if (password.equals("null")) {
            setNextOperation(new LoggingOperation());
            setCurrUser(null);
            return;
        }

        setNextOperation(new MainMenu(currUser));
        setCurrUser(currUser);
        System.out.println("Welcome back to Javatify " + getCurrUser().getUsername() + " !\n");
    }

    private void register(String username) {
        Account account = new Account();
        account.setUsername(username);
        String password, confirmPassword;
        Scanner scanner = new Scanner(System.in);

        do {
            System.out.println("Please enter your password: ");
            password = scanner.nextLine().trim();
            System.out.println("Please confirm your password: ");
            confirmPassword = scanner.nextLine().trim();
        } while (!password.equals(confirmPassword));

        account.setPassword(password);
        System.out.println("Please enter a name (nullable): ");
        account.setName(scanner.nextLine().trim());
        System.out.println("Please enter a surname (nullable): ");
        account.setSurname(scanner.nextLine().trim());
        int[] date = new int[0];
        do {
            System.out.println("Please enter a valid date of birth (i.e: dd-mm-yyyy): ");
            try {
                date = Arrays.stream(scanner.nextLine().split("-")).mapToInt(Integer::parseInt).toArray();
            } catch (Exception ignored) {
            }
        } while (date.length != 3);
        account.setBirthday(new Calendar.Builder().setDate(date[2], date[1] - 1, date[0]).build()); // offset by 1 for months
        account.setDateCreation(Calendar.getInstance());
        if (account.getAge() < 18) {
            System.out.println("You need to be at least 18 years old to register an account yourself !\n");
            setCurrUser(null);
            setNextOperation(new LoggingOperation());
            return;
        }

        accountAPI.addAccount(account);
        setCurrUser(account);
        setNextOperation(new MainMenu(account));
        System.out.println("Welcome back to Javatify " + getCurrUser().getUsername() + " !\n");
    }

    public Operation getNextOperation() {
        return nextOperation;
    }

    private void setNextOperation(Operation nextOperation) {
        this.nextOperation = nextOperation;
    }

    public Account getCurrUser() {
        return currUser;
    }

    public void setCurrUser(Account currUser) {
        this.currUser = currUser;
    }
}
