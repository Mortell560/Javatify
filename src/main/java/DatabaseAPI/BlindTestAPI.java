package DatabaseAPI;

import Entities.Account;
import Entities.BlindTest;
import Entities.Song;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class BlindTestAPI extends API {
    public BlindTestAPI(Logger logger, EntityManagerFactory factory){
        super(logger, factory);
    }

    public BlindTest getBlindTestById(Long id){
        return super.getById(BlindTest.class, id);
    }

    public void addSongToBT(BlindTest blindTest, Song song){
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        blindTest = em.merge(blindTest);
        em.refresh(blindTest);
        blindTest.getSongs().add(song);
        try {
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            logger.severe(e.getMessage());
            em.getTransaction().rollback();
        }
        em.close();
    }

    public void deleteSongFromBT(BlindTest blindTest, Song song){
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

    public void addInvitedAcc(BlindTest blindTest, Account account){
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        blindTest = em.merge(blindTest);
        em.refresh(blindTest);
        blindTest.getInvitedAccounts().add(account);
        try {
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            logger.severe(e.getMessage());
            em.getTransaction().rollback();
        }
        em.close();
    }

    public void createBlindTest(BlindTest blindTest){
        super.createObject(blindTest);
    }

    public BlindTest deleteBlindTest(BlindTest blindTest){
        return super.deleteObjectById(BlindTest.class, blindTest.getId());
    }

    public List<BlindTest> getAllBTForAccount(Account account){
        return super.getAllWhere(BlindTest.class, "owner", account);
    }

    public List<BlindTest> getAllInvited(Account account){
        List<BlindTest> res = super.getAll(BlindTest.class);
        res.removeIf(x -> !x.getInvitedAccounts().contains(account));
        return res;
    }
}
