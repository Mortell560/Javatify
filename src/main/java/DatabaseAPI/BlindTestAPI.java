package DatabaseAPI;

import Entities.Account;
import Entities.BlindTest;
import Entities.Song;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Class intended to make requests for the <b>BlindTests</b>
 *
 * @see BlindTest
 */
public class BlindTestAPI extends API {
    public BlindTestAPI(Logger logger, EntityManagerFactory factory) {
        super(logger, factory);
    }

    public BlindTest getBlindTestById(Long id) {
        return super.getById(BlindTest.class, id);
    }

    public void addSongToBT(BlindTest blindTest, Song song) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        blindTest = em.merge(blindTest);
        em.refresh(blindTest);
        song = em.merge(song);
        em.refresh(song);
        blindTest.getSongs().add(song);
        try {
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            logger.severe(e.getMessage());
            em.getTransaction().rollback();
        }
        em.close();
    }

    public void deleteSongFromBT(BlindTest blindTest, Song song) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        blindTest = em.merge(blindTest);
        em.refresh(blindTest);
        blindTest.getSongs().removeIf(x -> Objects.equals(x.getId(), song.getId()));
        try {
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            logger.severe(e.getMessage());
            em.getTransaction().rollback();
        }
        em.close();
    }

    public void addInvitedAcc(BlindTest blindTest, Account account) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        blindTest = em.merge(blindTest);
        em.refresh(blindTest);
        account = em.merge(account);
        em.refresh(account);
        blindTest.getInvitedAccounts().add(account);
        try {
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            logger.severe(e.getMessage());
            em.getTransaction().rollback();
        }
        em.close();
    }

    public void createBlindTest(BlindTest blindTest) {
        super.createObject(blindTest);
    }

    public BlindTest deleteBlindTest(BlindTest blindTest) {
        return super.deleteObjectById(BlindTest.class, blindTest.getId());
    }

    public List<BlindTest> getAllBTForAccount(Account account) {
        List<BlindTest> bl = super.getAll(BlindTest.class);
        bl.removeIf(x -> !x.getOwner().getId().equals(account.getId()));
        return bl;
    }

    public List<BlindTest> getAllAvailableToPlay(Account account) {
        List<BlindTest> l1 = getAllInvited(account), l2 = getAllBTForAccount(account);
        Set<BlindTest> set = new HashSet<>(); // no duplicates
        set.addAll(l1);
        set.addAll(l2);
        return set.stream().toList();
    }

    public List<BlindTest> getAllInvited(Account account) {
        List<BlindTest> res = super.getAll(BlindTest.class);
        res.removeIf(x -> x.getInvitedAccounts().stream().noneMatch(y -> y.getId().equals(account.getId())));
        return res;
    }
}
