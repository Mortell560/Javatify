package DatabaseAPI;

import Entities.Account;
import Entities.Notification;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.logging.Logger;

public class NotificationAPI extends API {
    public NotificationAPI(Logger logger, EntityManagerFactory factory) {
        super(logger, factory);
    }

    public List<Notification> getAllNotifications() {
        return super.getAll(Notification.class);
    }

    public List<Notification> getAllNotifForAcc(Account account){
        List<Notification> l = getAllNotifications();
        l.removeIf(notification -> !notification.getAccount().getId().equals(account.getId()));
        return l;
    }

    public List<Notification> getAllUnreadNotifForAcc(Account account){
        List<Notification> res = getAll(Notification.class);
        res.removeIf(x -> !x.getAccount().getId().equals(account.getId()) || x.isRead());
        return res;
    }

    public Notification readNotification(Notification notification){
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        notification = em.merge(notification);
        em.refresh(notification);
        notification.setRead(true);
        try {
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            logger.severe(e.getMessage());
            em.getTransaction().rollback();
        }
        em.close();
        return notification;
    }

    public void createNotification(Notification notification){
        super.createObject(notification);
    }

    public Notification deleteNotification(Notification notification){
        return super.deleteObjectById(Notification.class, notification.getId());
    }
}
