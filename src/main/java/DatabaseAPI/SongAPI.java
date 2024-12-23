package DatabaseAPI;

import Entities.Song;
import Entities.SongDAO;
import com.opencsv.bean.CsvToBeanBuilder;
import jakarta.persistence.EntityManagerFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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

    public List<Song> getAllSongs(){
        return super.getAll(Song.class);
    }

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
