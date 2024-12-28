package DatabaseAPI;

import Entities.Account;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Class intended to make all the calls to the database for the Accounts
 *
 * @see Account
 */
public class AccountAPI extends API {
    public AccountAPI(Logger logger, EntityManagerFactory entityManagerFactory) {
        super(logger, entityManagerFactory);
    }

    public Account getAccountById(Long id) {
        return super.getById(Account.class, id);
    }

    public Account getAccountByUsername(String username) throws NoSuchElementException {
        return super.getAllLike(Account.class, "username", username).getFirst();
    }

    public Account deleteAccount(Account account) {
        return super.deleteObjectById(Account.class, account.getId());
    }

    public Account updateAccountPassword(Account account, String newPassword) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        account = em.merge(account);
        em.refresh(account);
        account.setPassword(newPassword);
        try {
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            logger.severe(e.getMessage());
            em.getTransaction().rollback();
        }
        em.close();
        return account;
    }

    public Account updateAccountNames(Account account, String name, String surname) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        account = em.merge(account);
        em.refresh(account);
        account.setName(name);
        account.setSurname(surname);
        try {
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            logger.severe(e.getMessage());
            em.getTransaction().rollback();
        }
        em.close();
        return account;
    }

    public void addAccount(Account account) {
        super.createObject(account);
    }

    private Account updateAccountForSets(Account account, Set<Account> toUpdate, String type) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        account = em.find(Account.class, account.getId());
        em.refresh(account);
        if (type.equals("family")) {
            account.setFamily(toUpdate);
        } else {
            account.setFriends(toUpdate);
        }

        try {
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            logger.severe(e.getMessage());
            em.getTransaction().rollback();
        }
        em.close();
        return account;
    }

    public void removeAccountFromAllFriends(Account account) {
        Set<Account> friends = account.getFriends();
        for (Account friend : friends) {
            Set<Account> ff = friend.getFriends();
            ff.removeIf(x -> Objects.equals(x.getId(), account.getId()));
            updateAccountFriends(friend, ff);
        }
    }

    public Account updateAccountFriends(Account account, Set<Account> friends) {
        return updateAccountForSets(account, friends, "friends");
    }

    public Account updateAccountFamily(Account account, Set<Account> family) {
        return updateAccountForSets(account, family, "family");
    }
}
