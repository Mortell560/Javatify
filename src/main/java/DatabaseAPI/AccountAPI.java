package DatabaseAPI;

import Entities.Account;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.*;
import java.util.logging.Logger;

public class AccountAPI extends API {
    public AccountAPI(Logger logger, EntityManagerFactory entityManagerFactory) {
        super(logger, entityManagerFactory);
    }

    public Account getAccountById(Long id){
        return super.getById(Account.class, id);
    }

    public Account getAccountByUsername(String username) throws NoSuchElementException {
        return super.getAllLike(Account.class, "username", username).getFirst();
    }

    public Set<Account> getAllFriendAccounts(Account account){
        Set<Account> friendAccounts = account.getFriends();
        // if the friendship isn't mutual then it's not a friend
        friendAccounts.removeIf(friendAccount -> !friendAccount.getFriends().contains(account));
        return friendAccounts;
    }

    public Set<Account> getAllFamilyAccounts(Account account){
        Set<Account> familyAccounts = account.getFriends();
        // if the relation isn't mutual then it's not a family member
        familyAccounts.removeIf(familyAccount -> !familyAccount.getFamily().contains(account));
        return familyAccounts;
    }

    public boolean isFriendWithAccount(Account account, Account friendAccount){
        getAllFriendAccounts(account);
        getAllFamilyAccounts(friendAccount);
        return account.getFriends().contains(friendAccount) && friendAccount.getFriends().contains(account);
    }

    public boolean isFamilyWithAccount(Account account, Account familyAccount){
        getAllFriendAccounts(account);
        getAllFamilyAccounts(familyAccount);
        return account.getFamily().contains(familyAccount) && familyAccount.getFamily().contains(account);
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
