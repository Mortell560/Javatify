package DatabaseAPI;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
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
    final EntityManagerFactory entityManagerFactory; // Static since it is shared
    final Logger logger;

    public API(Logger logger, EntityManagerFactory entityManagerFactory) {
        this.logger = logger;
        this.entityManagerFactory = entityManagerFactory;
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
     * Returns all objects of type <b>E</b> that satisfy the given where clause param=value
     *
     * @param c          Class to fetch from
     * @param paramName  Name of the column inside the database
     * @param paramValue Value to check for. Must be exact
     * @param <E>        Type corresponding to the table to fetch
     * @return All objects of type <b>E</b> within the database respecting the condition
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
     * Returns all objects of type <b>E</b> that satisfy the given clause param LIKE value
     *
     * @param c          Class to fetch from
     * @param paramName  Name of the column inside the database
     * @param paramValue Value to check for. Must be a pattern by SQL standards with the %
     * @param <E>        Type corresponding to the table to fetch
     * @return All objects of type <b>E</b> within the database respecting the condition
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
     * Returns all the objects of type <b>E</b> that satisfy the given Like param value clause
     *
     * @param c          Class to fetch from
     * @param paramName  Name of the column inside the database
     * @param paramValue Value to check for. Must be a pattern by SQL standards with the
     * @param page       Page to return
     * @param pageSize   Pagination size
     * @param <E>        Type corresponding to the table to fetch
     * @return All objects of type <b>E</b> within the database respecting the condition
     */
    <E> List<E> getAllLikePaginated(Class<E> c, String paramName, String paramValue, int page, int pageSize) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<E> query = builder.createQuery(c);
        Root<E> i = query.from(c);
        query.select(i);
        query.where(builder.like(i.get(paramName).as(String.class), paramValue));
        TypedQuery<E> typedQuery = em.createQuery(query);
        typedQuery.setFirstResult((page - 1) * pageSize);
        typedQuery.setMaxResults(pageSize);
        List<E> res = typedQuery.getResultList();

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
     *
     * @param c   Class to fetch from
     * @param id  Id of target object
     * @param <E>
     * @return Object of type <b>E</b> and has the right id or null if there's object with such id
     */
    <E> E getById(Class<E> c, Long id) {
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
        } catch (NoResultException e) {
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

    /**
     * Creates an object within the database
     *
     * @param object Object to save
     * @param <E>    Class of the object to save
     */
    <E> void createObject(E object) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        em.persist(object);
        try {
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            logger.severe(e.getMessage());
            em.getTransaction().rollback();
        }
        em.close();
    }

    /**
     * Creates several objects within the database. Use this method if you need perfs since entity managers are expensive
     *
     * @param objects Objects to save
     * @param <E>     Class of the objects to save
     */
    <E> void createObjects(List<E> objects) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        for (E object : objects) {
            em.persist(object);
        }
        try {
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            logger.severe(e.getMessage());
            em.getTransaction().rollback();
        }
        em.close();
    }

    /**
     * Removes an object from the database
     *
     * @param c      Class of the object
     * @param object Object to remove
     * @param <E>    Class of the object
     * @return The removed object
     */
    @Deprecated(since = "Hibernate broke it")
    <E> E deleteObject(Class<E> c, E object) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        object = em.find(c, object);
        em.remove(object);
        try {
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            logger.severe(e.getMessage());
            em.getTransaction().rollback();
        }
        em.close();
        return object;
    }

    /**
     * Removes an object from the database
     *
     * @param c   Class of the object
     * @param id  Id of the object to remove
     * @param <E> Class of the object
     * @return The removed object
     */
    <E> E deleteObjectById(Class<E> c, Long id) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        E object = em.find(c, id);
        em.remove(object);
        try {
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            logger.severe(e.getMessage());
            em.getTransaction().rollback();
        }
        em.close();
        return object;
    }

}
