import Entities.Account;
import Entities.BlindTest;
import Entities.Song;
import Entities.SongDAO;
import com.opencsv.bean.CsvToBeanBuilder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

/**
 * Main class for the App Javatify
 * Use the run() method to start
 */
public class App {
    private static final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("Javatify");
    private final Logger logger = Logger.getLogger(App.class.getName());
    private final String dataPath = "src/main/resources/spotify_millsongdata.csv"; // TODO: Change before handing out

    public App() {
        logger.info(App.class.getName() + ": Constructor Started");
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        loadData(em);
        em.close();
        logger.info(App.class.getName() + ": Constructor Finished");
        showLogo();

    }

    /**
     * Main loop method
     */
    public void run(){

    }

    /**
     * Shows the logo of our app
     */
    private void showLogo() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n\n\n\n\n"); // I know that flush() exists but it doesn't work for all cases
        for (int i = 0; i < 20; i++) {
            sb.append("*");
        }
        sb.append("\n");
        sb.append("*").append("                  ").append("*").append("\n");
        sb.append("*").append("     Javatify     ").append("*").append("\n");
        sb.append("*").append("                  ").append("*").append("\n");
        for (int i = 0; i < 20; i++) {
            sb.append("*");
        }
        sb.append("\n");
        System.out.println(sb);
    }

    /**
     * This method will load Songs into the database if they are not inside the database
     *
     * @param em Entity Manager used for the transactions
     */
    private void loadData(EntityManager em) {
        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<Song> query = builder.createQuery(Song.class);
        Root<Song> i = query.from(Song.class);
        query.select(i);

        List<Song> Songs = em.createQuery(query).getResultList();

        // If we have 0 songs, we have somewhat of a big problem => We need to load the CSV
        if (Songs.isEmpty()) {
            List<SongDAO> beans = null;
            try {
                beans = new CsvToBeanBuilder<SongDAO>(new FileReader(dataPath))
                        .withType(SongDAO.class)
                        .build()
                        .parse();
            } catch (FileNotFoundException e) { // Missing csv
                logger.severe(e.getMessage());
                System.exit(404);
            }
            beans.forEach(x -> em.persist(x.toSong()));
        }

        Account acc = new Account();
        acc.setUsername("test");
        acc.setBirthday(new Calendar.Builder().setDate(2004, 1,1).build());
        acc.setDateCreation(Calendar.getInstance());
        acc.setPassword("test");
        em.persist(acc);
        BlindTest test = new BlindTest();
        test.setMODE(2);
        test.setOwner(acc);
        em.persist(test);

        em.getTransaction().commit();
    }
}
