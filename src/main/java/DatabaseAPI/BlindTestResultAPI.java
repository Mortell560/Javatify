package DatabaseAPI;

import Entities.Account;
import Entities.BlindTest;
import Entities.BlindTestResult;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class BlindTestResultAPI extends API {
    public BlindTestResultAPI(Logger logger, EntityManagerFactory factory) {
        super(logger, factory);
    }

    public void createBTResult(BlindTestResult result) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        Account acc = result.getUser();
        BlindTest blindTest = result.getTest();
        acc = em.merge(acc);
        blindTest = em.merge(blindTest);
        em.refresh(acc);
        em.refresh(blindTest);
        result.setUser(acc);
        result.setTest(blindTest);
        em.persist(result);
        try {
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            logger.severe(e.getMessage());
            em.getTransaction().rollback();
        }
        em.close();

    }

    public void removeAllBTResultsForAccount(Account acc) {
        List<BlindTestResult> res = getAllBlindTestResultForAcc(acc);
        res.forEach(x -> super.deleteObjectById(BlindTestResult.class, x.getId()));
    }

    public List<BlindTestResult> getAllBTResult() {
        return super.getAll(BlindTestResult.class);
    }

    public List<BlindTestResult> getAllBlindTestResultForAcc(Account account) {
        List<BlindTestResult> res = getAllBTResult();
        res.removeIf(x -> !Objects.equals(x.getUser().getId(), account.getId()));
        return res;
    }

    public List<BlindTestResult> getAllBlindTestResultForBT(BlindTest blindTest) {
        List<BlindTestResult> res = getAllBTResult();
        res.removeIf(x -> !Objects.equals(x.getTest().getId(), blindTest.getId()));
        return res;
    }
}
