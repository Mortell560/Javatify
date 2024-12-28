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

    public Account deleteAccount(Account account){
        return super.deleteObjectById(Account.class, account.getId());
    }

    public Account updateAccountPassword(Account account, String newPassword){
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

    public Account updateAccountNames(Account account, String name, String surname){
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

    public Set<Account> getAllFriendAccounts(Account account){
        Set<Account> friendAccounts = account.getFriends();
        // if the friendship isn't mutual then it's not a friend
        friendAccounts.removeIf(friendAccount -> friendAccount.getFriends().stream().noneMatch(x -> x.getUsername().equals(account.getUsername())));
        return friendAccounts;
    }

    public Set<Account> getAllFamilyAccounts(Account account){
        Set<Account> familyAccounts = account.getFriends();
        // if the relation isn't mutual then it's not a family member
        familyAccounts.removeIf(familyAccount -> familyAccount.getFamily().stream().noneMatch(x -> x.getUsername().equals(account.getUsername())));
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

    private Account updateAccountForSets(Account account, Set<Account> toUpdate, String type){
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        account = em.merge(account);
        em.refresh(account);
        if (type.equals("family")){
            account.setFamily(toUpdate);
        }
        else {
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

    public Account updateAccountFriends(Account account, Set<Account> friends){
        return updateAccountForSets(account, friends, "friends");
    }

    public Account updateAccountFamily(Account account, Set<Account> family){
        return updateAccountForSets(account, family, "family");
    }
}
