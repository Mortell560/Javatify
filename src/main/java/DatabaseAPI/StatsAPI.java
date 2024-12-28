package DatabaseAPI;

import Entities.Account;
import Entities.PersonalStats;
import Entities.Song;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.logging.Logger;


/**
 * Class used for the database calls for the PersonalStats
 *
 * @see PersonalStats
 */
public class StatsAPI extends API {
    public StatsAPI(Logger logger, EntityManagerFactory factory) {
        super(logger, factory);
    }

    private void createStat(Account account, Song song) {
        PersonalStats personalStats = new PersonalStats();
        personalStats.setAccount(account);
        personalStats.setSong(song);
        personalStats.setNbReco(0);
        personalStats.setViews(0);
        super.createObject(personalStats);
    }

    public void deleteStatsForAccount(Account account) {
        List<PersonalStats> p = super.getAll(PersonalStats.class);
        p.removeIf(personal -> personal.getAccount().getId().equals(account.getId()));
        p.forEach(x -> super.deleteObjectById(PersonalStats.class, x.getId()));
    }

    public void addView(Account account, Song song) {
        PersonalStats p = getPersonalStats(account, song);
        if (p == null) {
            createStat(account, song);
        }
        PersonalStats personalStats = getPersonalStats(account, song); // Should be non-null by then
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        personalStats = em.merge(personalStats);
        em.refresh(personalStats);
        personalStats.setViews(personalStats.getViews() + 1);
        try {
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            logger.severe(e.getMessage());
            em.getTransaction().rollback();
        }
        em.close();
    }

    public void addReco(Account account, Song song) {
        PersonalStats p = getPersonalStats(account, song);
        if (p == null) {
            createStat(account, song);
            p = getPersonalStats(account, song);
        }
        PersonalStats personalStats = p; // Should be non-null by then
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        personalStats = em.merge(personalStats);
        em.refresh(personalStats);
        personalStats.setNbReco(personalStats.getNbReco() + 1);
        try {
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            logger.severe(e.getMessage());
            em.getTransaction().rollback();
        }
        em.close();
    }

    public PersonalStats getPersonalStats(Account account, Song song) {
        List<PersonalStats> p = getAll(PersonalStats.class);
        p.removeIf(personal -> !personal.getAccount().getId().equals(account.getId()) && !personal.getSong().getId().equals(song.getId()));
        return p.isEmpty() ? null : p.getFirst();
    }
}
