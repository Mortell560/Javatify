package Interface.Menu;

import DatabaseAPI.NotificationAPI;
import Entities.Account;
import Entities.Notification;
import Interface.Operation;
import Utils.Safeguards;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.logging.Logger;

/**
 * Handles all the notifications
 *
 * @see Notification
 */
public class NotificationMenu implements Operation {
    private Operation nextOperation;
    private final Account account;
    private NotificationAPI notificationAPI;
    private final Logger logger = Logger.getLogger(NotificationMenu.class.getName());

    public NotificationMenu(Account account) {
        this.account = account;
    }

    public void run(EntityManagerFactory emf) {
        notificationAPI = new NotificationAPI(logger, emf);
        System.out.println("Choose an option: ");
        System.out.println("1. Read and act on current Notifications");
        System.out.println("2. Read old Notifications");
        System.out.println("3. Return to main menu");

        int choice = Safeguards.getInputInterval(1, 3);

        switch (choice) {
            case 1 -> showAndActCurrentNotifications();
            case 2 -> showOldNotifs();
            case 3 -> setNextOperation(new MainMenu(account));
        }
    }

    private void showAndActCurrentNotifications() {
        List<Notification> notifs = notificationAPI.getAllUnreadNotifForAcc(account);
        if (notifs.isEmpty()) {
            System.out.println("No notifications found");
            setNextOperation(new NotificationMenu(account));
            return;
        }

        System.out.println("Choose an option: ");
        int i = 0;
        for (Notification notif : notifs) {
            i++;
            System.out.println("Notification " + i + ": " + notif.getMessage());
        }
        int choice = Safeguards.getInputInterval(0, i);
        if (choice == 0) {
            setNextOperation(new NotificationMenu(account));
            return;
        }
        Notification notif = notifs.get(choice - 1);
        Operation nextOperation = notif.convertToOperation();
        setNextOperation(nextOperation);
        notificationAPI.readNotification(notif);
    }

    private void showOldNotifs() {
        List<Notification> notifs = notificationAPI.getAllNotifForAcc(account);
        notifs.removeAll(notificationAPI.getAllUnreadNotifForAcc(account));
        System.out.println("Old notifications: ");
        notifs.forEach(System.out::println);
        setNextOperation(new NotificationMenu(account));
    }

    public Operation getNextOperation() {
        return nextOperation;
    }

    private void setNextOperation(Operation nextOperation) {
        this.nextOperation = nextOperation;
    }
}
