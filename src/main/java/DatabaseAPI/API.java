package DatabaseAPI;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
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
    <E> List<E> getAll(Class<E> c) {
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

    /**
     * Returns all objects of type <b>E</b> that satisfy the given Where param=value clause
     * @param c Class to fetch from
     * @param paramName Name of the column inside the database
     * @param paramValue Value to check for. Must be exact
     * @return All objects of type <b>E</b> within the database respecting the condition
     * @param <E> Type corresponding to the table to fetch
     */
    <E> List<E> getAllWhere(Class<E> c, String paramName, Object paramValue) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<E> query = builder.createQuery(c);
        Root<E> i = query.from(c);
        query.select(i);
        query.where(builder.equal(i.get(paramName).as(Object.class), paramValue));

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

    /**
     * Returns all objects of type <b>E</b> that satisfy the given Where param LIKE value clause
     * @param c Class to fetch from
     * @param paramName Name of the column inside the database
     * @param paramValue Value to check for. Must be a pattern by SQL standards with the %
     * @return All objects of type <b>E</b> within the database respecting the condition
     * @param <E> Type corresponding to the table to fetch
     */
    <E> List<E> getAllLike(Class<E> c, String paramName, String paramValue) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<E> query = builder.createQuery(c);
        Root<E> i = query.from(c);
        query.select(i);
        query.where(builder.like(i.get(paramName).as(String.class), paramValue)); // Since paramNames should be never accessed, no need to handle the exception with try catch

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

    /**
     * Return the object of type <b>E</b> that has the id <b>E</b>
     * @param c Class to fetch from
     * @param id Id of target object
     * @return Object of type <b>E</b> and has the right id or null if there's object with such id
     * @param <E>
     */
    <E> E getById(Class<E> c, Long id){
        E res;
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<E> query = builder.createQuery(c);
        Root<E> i = query.from(c);
        query.select(i);
        query.where(builder.equal(i.get("id").as(Long.class), id));

        try {
            res = em.createQuery(query).getSingleResult();
        }
        catch (NoResultException e){
            res = null;
        }


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
