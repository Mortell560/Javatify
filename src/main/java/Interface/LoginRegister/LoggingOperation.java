package Interface.LoginRegister;

import DatabaseAPI.AccountAPI;
import Entities.Account;
import Interface.Operation;
import jakarta.persistence.EntityManagerFactory;

import java.util.Arrays;
import java.util.Calendar;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.Logger;

public class LoggingOperation implements Operation {
    private Account currUser = null;
    private Operation nextOperation = null;
    private AccountAPI accountAPI;
    private Logger logger = Logger.getLogger(this.getClass().getName());
    public LoggingOperation(String args) {}

    public void run(EntityManagerFactory factory) {
        System.out.println("Welcome to Javatify\nPlease enter an username to start (Case sensitive): ");
        accountAPI = new AccountAPI(logger, factory);
        Scanner scanner = new Scanner(System.in);
        String username = scanner.nextLine().trim();
        try{
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
            setNextOperation(new LoggingOperation(null));
            setCurrUser(null);
            return;
        }
        System.out.println("Welcome back to Javatify " + getCurrUser().getUsername() + " !\n");
    }

    private void register(String username){
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
        int[] date;
        do {
            System.out.println("Please enter a valid date of birth (i.e: dd-mm-yyyy): ");
            date = Arrays.stream(scanner.nextLine().split("-")).mapToInt(Integer::parseInt).toArray();
        } while (date.length != 3);
        System.out.println(date[0] + " " + date[1] + " " + date[2]);
        account.setBirthday(new Calendar.Builder().setDate(date[2], date[1]-1, date[0]).build());
        account.setDateCreation(Calendar.getInstance());
        System.out.println(account.getDateCreation().getTime());
        System.out.println(account.getBirthday().getTime());
        System.out.println(account.getAge());

        accountAPI.addAccount(account);
        setCurrUser(account);
        setNextOperation(null);
    }

    public Operation getNextOperation() {
        return nextOperation;
    }

    public Account getCurrUser() {
        return currUser;
    }

    public void setCurrUser(Account currUser) {
        this.currUser = currUser;
    }

    private void setNextOperation(Operation nextOperation) {
        this.nextOperation = nextOperation;
    }
}
