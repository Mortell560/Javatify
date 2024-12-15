package DatabaseAPI;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;
import java.util.logging.Logger;

/**
 * Main abstract class that implements standard SQL requests
 * Any special request will be implemented into child classes
 */
abstract class API {
    private static EntityManagerFactory entityManagerFactory; // Static since it is shared
    private final Logger logger;

    public API(Logger logger, EntityManagerFactory entityManagerFactory) {
        this.logger = logger;
        API.entityManagerFactory = entityManagerFactory;
    }

    /**
     * Returns all objects of type <b>E</b>
     *
     * @param c   Class to fetch from
     * @param <E> Type corresponding to the table to fetch
     * @return All objects of type <b>E</b> within the database
     */
    public <E> List<E> getAll(Class<E> c) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<E> query = builder.createQuery(c);
        Root<E> i = query.from(c);
        query.select(i);

        List<E> res = em.createQuery(query).getResultList();

        try {
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            logger.severe(e.getMessage());
            em.getTransaction().rollback();
        }
        em.close();
        return res;
    }


}
