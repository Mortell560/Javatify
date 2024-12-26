package DatabaseAPI;

import Entities.Account;
import Entities.PersonalStats;
import Entities.Song;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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

    public void addView(Account account, Song song) {
        Optional<PersonalStats> p = getPersonalStats(account, song);
        if (p.isEmpty()){
            createStat(account, song);
            p = getPersonalStats(account, song);
        }
        PersonalStats personalStats = p.get(); // Should be non-null by then
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
        Optional<PersonalStats> p = getPersonalStats(account, song);
        if (p.isEmpty()){
            createStat(account, song);
            p = getPersonalStats(account, song);
        }
        PersonalStats personalStats = p.get(); // Should be non-null by then
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

    public Optional<PersonalStats> getPersonalStats(Account account, Song song) {
        List<PersonalStats> l1 = getAllWhere(PersonalStats.class, "account", account.getId());
        List<PersonalStats> l2 = getAllWhere(PersonalStats.class, "song", song.getId());
        Set<PersonalStats> set = l1.stream().distinct().filter(l2::contains).collect(Collectors.toSet());
        return set.stream().findFirst();
    }
}
