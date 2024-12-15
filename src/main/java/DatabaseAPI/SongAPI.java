package DatabaseAPI;

import Entities.Song;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.logging.Logger;

/**
 * Class used
 */
public class SongAPI extends API {
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
}
