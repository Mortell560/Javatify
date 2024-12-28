package Interface.Menu;

import DatabaseAPI.AccountAPI;
import DatabaseAPI.NotificationAPI;
import Entities.Account;
import Entities.Notification;
import Interface.NotificationRelated.AddFamily;
import Interface.NotificationRelated.AddFriend;
import Interface.Operation;
import Utils.Safeguards;
import jakarta.persistence.EntityManagerFactory;

import java.util.*;
import java.util.logging.Logger;

public class SettingsMenu implements Operation {
    private final Logger logger = Logger.getLogger(SettingsMenu.class.getName());
    private Operation nextOperation;
    private Account account;
    private AccountAPI accountAPI;
    private NotificationAPI notificationAPI;

    public SettingsMenu(Account account) {
        this.account = account;
    }

    public void run(EntityManagerFactory emf) {
        accountAPI = new AccountAPI(logger, emf);
        notificationAPI = new NotificationAPI(logger, emf);
        System.out.println("Account settings");
        System.out.println("1. Change password");
        System.out.println("2. Change surname and name");
        System.out.println("3. Add/Remove child account");
        System.out.println("4. Add/Remove Family account");
        System.out.println("5. Add/Remove Friend account");
        System.out.println("6. Show account information");
        System.out.println("7. Exit");

        int choice = Safeguards.getInputInterval(1, 7);

        switch (choice) {
            case 1 -> changePassword();
            case 2 -> changeName();
            case 3 -> AddOrRemoveChild();
            case 4 -> AddOrRemoveFamilyAccount();
            case 5 -> AddOrRemoveFriendAccount();
            case 6 -> showAccountInformation();
        }
        setNextOperation(new MainMenu(getCurrentAccount()));
    }


    public Account getCurrentAccount() {
        return account;
    }

    private void setCurrentAccount(Account account) {
        this.account = account;
    }

    private void AddOrRemoveFriendAccount() {
        System.out.println("Do you want to add some account to your friends? Enter null to exit");
        System.out.println("1. Add to friends");
        System.out.println("2. Remove from friends");

        Scanner scanner = new Scanner(System.in);
        String choice = scanner.nextLine();
        if (choice.equalsIgnoreCase("1")) {
            AddFriend();
        }
        else if (choice.equalsIgnoreCase("2")) {
            RemoveFriend();
        }
    }

    private void AddFriend(){
        System.out.println("Enter the username of the friend you want to add");
        Scanner scanner = new Scanner(System.in);
        String username = scanner.nextLine().trim();
        Account friend;
        try{
            friend = accountAPI.getAccountByUsername(username);
        } catch (NoSuchElementException e) {
            System.out.println("That username does not exist");
            return;
        }
        if (username.equalsIgnoreCase(account.getUsername())) {
            System.out.println("Nice try");
        }
        else if (friend.getAge() >= 18 && account.getAge() >= 18 || friend.getAge() < 18 && account.getAge() < 18) {
            Notification notification = new Notification();
            notification.setRead(false);
            notification.setTime(Calendar.getInstance());
            notification.setArgs(account.getId()+";"+friend.getId());
            notification.setAccount(friend);
            notification.setMessage("You have a friend request from " + account.getUsername());
            notification.setOperation(1);
            notificationAPI.createNotification(notification);
            System.out.println("Request sent");
        }
        else {
            System.out.println("Illegal request.");
        }

    }

    private void RemoveFriend(){
        Set<Account> friends = account.getFriends();
        if (friends.isEmpty()){
            System.out.println("No friends to remove");
            return;
        }
        System.out.println("Choose friend to remove or 0 to exit: ");
        Scanner scanner = new Scanner(System.in);
        int i = 0;
        for (Account friend : friends) {
            i++;
            System.out.println(i + ". - " + friend.getUsername());
        }
        int choice = Safeguards.getInputInterval(0, i);
        if (choice == 0){
            System.out.println("Returning");
            return;
        }
        Account friend = friends.stream().toList().get(choice - 1);
        Set<Account> ffriends = friend.getFriends();
        ffriends.removeIf(f -> f.getUsername().equals(account.getUsername()));
        friends.removeIf(f -> f.getUsername().equals(friend.getUsername()));
        accountAPI.updateAccountFriends(account, friends);
        accountAPI.updateAccountFriends(friend, ffriends);
        System.out.println("Removed " + friend.getUsername() + " from friends");
    }

    private void AddOrRemoveFamilyAccount() {
        if (account.getAge() < 18) {
            System.out.println("Haha good one");
            return;
        }
        System.out.println("Do you want to add some account to your family? Enter null to exit");
        System.out.println("1. Add to family");
        System.out.println("2. Remove from family");

        Scanner scanner = new Scanner(System.in);
        String answer = scanner.nextLine();
        if (answer.equalsIgnoreCase("1")) {
            AddFamilyAccount();
        } else if (answer.equalsIgnoreCase("2")) {
            RemoveFamilyAccount();
        }
    }

    private void RemoveFamilyAccount() {
        Set<Account> family = new HashSet<>(account.getFamily());
        family.removeIf(account -> account.getAge() < 18);
        if (family.isEmpty()) {
            System.out.println("Haha good one but you need to add some account to your family (other than your own children)");
            return;
        }
        int i = 0;
        System.out.println("Choose family to remove or 0 to exit: ");
        for (Account familyAccount : family) {
            i++;
            System.out.println(i + ". - " + familyAccount.getUsername());
        }
        int choice = Safeguards.getInputInterval(0, i);
        if (choice == 0){
            System.out.println("Returning");
            return;
        }
        Account f = family.stream().toList().get(choice - 1);
        Set<Account> familyAccounts = f.getFamily();
        Set<Account> aAccounts = account.getFamily();
        aAccounts.removeIf(x -> x.getUsername().equals(f.getUsername()));
        familyAccounts.removeIf(x -> x.getUsername().equals(account.getUsername()));
        accountAPI.updateAccountFamily(account, aAccounts);
        accountAPI.updateAccountFamily(f, familyAccounts);
        System.out.println("Removed " + f.getUsername() + " from family");


    }

    private void AddFamilyAccount() {
        System.out.println("Enter the username of the friend you want to add");
        Scanner scanner = new Scanner(System.in);
        String username = scanner.nextLine().trim();
        Account family;
        try{
            family = accountAPI.getAccountByUsername(username);
        } catch (NoSuchElementException e) {
            System.out.println("That username does not exist");
            return;
        }
        if (family.getAge() < 18) {
            System.out.println("Nice try but you cannot adopt others' children. Use the previous menu to add your own kids");
            return;
        } else if (username.equalsIgnoreCase(account.getUsername())) {
            System.out.println("Nice try");
            return;
        }
        Notification notification = new Notification();
        notification.setRead(false);
        notification.setTime(Calendar.getInstance());
        notification.setArgs(account.getId()+";"+family.getId());
        notification.setAccount(family);
        notification.setMessage("You have a family request from " + account.getUsername());
        notification.setOperation(3);
        notificationAPI.createNotification(notification);
        System.out.println("Request sent");
    }

    private void showAccountInformation() {
        System.out.println("Account information\n");
        System.out.println(account + "\n");
    }

    private void AddOrRemoveChild() {
        if (account.getAge() < 18) {
            System.out.println("Haha good one");
            return;
        }
        System.out.println("Do you want to add child account? Enter null to exit");
        System.out.println("1. Add child account");
        System.out.println("2. Remove child account");

        Scanner scanner = new Scanner(System.in);
        String answer = scanner.nextLine();
        if (answer.equalsIgnoreCase("1")) {
            addChildAcc();
        } else if (answer.equalsIgnoreCase("2")) {
            removeChildAcc();
        }
    }

    private void removeChildAcc() {
        Set<Account> childAcc = account.getFamily();
        childAcc.removeIf(account -> account.getAge() >= 18);
        if (childAcc.isEmpty()) {
            System.out.println("You have no children");
            return;
        }
        int i = 0;
        for (Account acc : childAcc) {
            System.out.println("Child account " + i + ": " + acc.getUsername());
            i++;
        }
        System.out.println("Select the child account: ");
        int choice = Safeguards.getInputInterval(0, i);
        Set<Account> newFamily = account.getFamily();
        Account childToRemove = childAcc.stream().toList().get(choice);
        newFamily.removeIf(x -> Objects.equals(x.getId(), childToRemove.getId()));
        setCurrentAccount(accountAPI.updateAccountFamily(account, newFamily));
        accountAPI.deleteAccount(childToRemove);
    }

    private void addChildAcc() {
        Account childAcc = new Account();
        Scanner scanner = new Scanner(System.in);
        String password, confirmPassword, username = null;
        boolean valid = false;
        while (!valid) {
            try {
                System.out.println("Enter a valid username: ");
                username = scanner.nextLine().trim();
                accountAPI.getAccountByUsername(username); // if it throws an exception that means the username is free
                System.out.println("Username " + username + " already exists");
            } catch (NoSuchElementException e) {
                valid = true;
            }
        }
        childAcc.setUsername(username);

        do {
            System.out.println("Please enter your password: ");
            password = scanner.nextLine().trim();
            System.out.println("Please confirm your password: ");
            confirmPassword = scanner.nextLine().trim();
        } while (!password.equals(confirmPassword));

        childAcc.setPassword(password);
        System.out.println("Please enter a name (nullable): ");
        childAcc.setName(scanner.nextLine().trim());
        System.out.println("Please enter a surname (nullable): ");
        childAcc.setSurname(scanner.nextLine().trim());
        int[] date;
        do {
            System.out.println("Please enter a valid date of birth (i.e: dd-mm-yyyy): ");
            date = Arrays.stream(scanner.nextLine().split("-")).mapToInt(Integer::parseInt).toArray();
        } while (date.length != 3);
        childAcc.setBirthday(new Calendar.Builder().setDate(date[2], date[1] - 1, date[0]).build()); // offset by 1 for months
        childAcc.setDateCreation(Calendar.getInstance());
        if (childAcc.getAge() >= 18) {
            System.out.println("Haha good one but you cannot add an adult account");
            return;
        }

        accountAPI.addAccount(childAcc);
        Set<Account> s = account.getFamily();
        s.add(childAcc);
        setCurrentAccount(accountAPI.updateAccountFamily(account, s));
        s = new HashSet<>();
        s.add(account);
        accountAPI.updateAccountFamily(childAcc, s);
    }

    private void changePassword() {
        String password, confirmPassword;
        Scanner scanner = new Scanner(System.in);
        do {
            System.out.println("Please enter your new password: ");
            password = scanner.nextLine().trim();
            System.out.println("Please confirm your new password: ");
            confirmPassword = scanner.nextLine().trim();
        } while (!password.equals(confirmPassword));
        setCurrentAccount(accountAPI.updateAccountPassword(account, password));
    }

    private void changeName() {
        String name, surname;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter your name: ");
        name = scanner.nextLine().trim();
        System.out.println("Please enter your surname: ");
        surname = scanner.nextLine().trim();
        setCurrentAccount(accountAPI.updateAccountNames(account, name, surname));
    }

    public Operation getNextOperation() {
        return nextOperation;
    }

    private void setNextOperation(Operation nextOperation) {
        this.nextOperation = nextOperation;
    }
}
