package DatabaseAPI;

import Entities.Song;
import Entities.SongDAO;
import com.opencsv.bean.CsvToBeanBuilder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.SessionFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.logging.Logger;

/**
 * Class used to fetch songs
 */
public class SongAPI extends API {
    public static final int MAX_SONGS_PER_PAGE = 20;
    public SongAPI(Logger logger, EntityManagerFactory entityManagerFactory) {
        super(logger, entityManagerFactory);
    }

    public List<Song> getAllByTitle(String title) {
        return super.getAllLike(Song.class, "title", "%"+title+"%");
    }

    public List<Song> getAllByArtist(String artist) {
        return super.getAllLike(Song.class, "artist", "%"+artist+"%");
    }

    public List<Song> getAllByText(String lyrics) {
        return super.getAllLike(Song.class, "text", "%"+lyrics+"%");
    }

    public Song getSongById(Long id) {
        return super.getById(Song.class, id);
    }

    public List<Song> getAllSongs(){
        return super.getAll(Song.class);
    }

    public List<Song> getAllByArtistPaginated(String artist, int page) {
        return super.getAllLikePaginated(Song.class, "artist", "%"+artist+"%", page, MAX_SONGS_PER_PAGE);
    }

    public List<Song> getAllByTitlePaginated(String title, int page) {
        return super.getAllLikePaginated(Song.class, "title", "%"+title+"%", page, MAX_SONGS_PER_PAGE);
    }

    public List<Song> getAllByTextPaginated(String text, int page) {
        return super.getAllLikePaginated(Song.class, "text", "%"+text+"%", page, MAX_SONGS_PER_PAGE);
    }
    
    public List<Song> getAllByPopularityPaginated(String arg, int page) {
        EntityManager em = entityManagerFactory.createEntityManager();

        em.getTransaction().begin();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Song> query = builder.createQuery(Song.class);
        Root<Song> i = query.from(Song.class);
        query.select(i);
        query.orderBy(builder.desc(i.get("listeningCount")));

        TypedQuery<Song> typedQuery = em.createQuery(query);
        typedQuery.setFirstResult((page - 1) * MAX_SONGS_PER_PAGE);
        typedQuery.setMaxResults(MAX_SONGS_PER_PAGE);

        List<Song> songs = typedQuery.getResultList();

        try {
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            logger.severe(e.getMessage());
            em.getTransaction().rollback();
        }
        em.close();
        return songs;
    }

    public Song addListening(Song song) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        song = em.merge(song);
        em.refresh(song);
        song.setListeningCount(song.getListeningCount()+1);
        try {
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            logger.severe(e.getMessage());
            em.getTransaction().rollback();
        }
        em.close();
        return song;
    }

    /**
     * Load all the songs into the database if they are not present already
     * @param dataPath path to the CSV file
     */
    public void loadSongs(String dataPath){
        List<Song> songs = getAllSongs();
        // If we have 0 songs, we have somewhat of a big problem => We need to load the CSV
        if (songs.isEmpty()) {
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
            songs = beans.stream().map(SongDAO::toSong).toList();
            createObjects(songs);
        }
    }
}
