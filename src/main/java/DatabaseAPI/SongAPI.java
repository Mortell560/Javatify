package DatabaseAPI;

import jakarta.persistence.EntityManagerFactory;

import java.util.logging.Logger;

public class SongAPI extends API {
    public SongAPI(Logger logger, EntityManagerFactory entityManagerFactory) {
        super(logger, entityManagerFactory);
    }
}
