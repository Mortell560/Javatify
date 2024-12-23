package DatabaseAPI;

import Entities.Account;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.logging.Logger;

public class AccountAPI extends API {
    public AccountAPI(Logger logger, EntityManagerFactory entityManagerFactory) {
        super(logger, entityManagerFactory);
    }

    public Account getAccountById(Long id){
        return super.getById(Account.class, id);
    }

    public Account getAccountByUsername(String username) throws NoSuchElementException {
        return super.getAllWhere(Account.class, "username", username).getFirst();
    }

    public void addAccount(Account account){
        super.createObject(account);
    }

    private void updateAccountForSets(Account account, Set<Account> toUpdate, String type){
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        account = em.merge(account);
        em.refresh(account);
        if (type.equals("family")){
            account.setFamily(toUpdate);
        }
        else {
            account.setFamily(toUpdate);
        }

        try {
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            logger.severe(e.getMessage());
            em.getTransaction().rollback();
        }
        em.close();
    }

    public void updateAccountFriends(Account account, Set<Account> friends){
        updateAccountForSets(account, friends, "friends");
    }

    public void updateAccountFamily(Account account, Set<Account> family){
        updateAccountForSets(account, family, "family");
    }
}
