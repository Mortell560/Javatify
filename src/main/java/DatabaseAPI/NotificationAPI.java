package DatabaseAPI;

import Entities.Account;
import Entities.Notification;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.logging.Logger;

public class NotificationAPI extends API {
    public NotificationAPI(Logger logger, EntityManagerFactory factory) {
        super(logger, factory);
    }

    public List<Notification> getAllNotifForAcc(Account account){
        return super.getAllWhere(Notification.class, "account", account);
    }

    public List<Notification> getAllUnreadNotifForAcc(Account account){
        List<Notification> res = super.getAllWhere(Notification.class, "account", account);
        res.removeIf(Notification::isRead);
        return res;
    }
}
