package DatabaseAPI;

import jakarta.persistence.EntityManagerFactory;

import java.util.logging.Logger;

public class BlindTestAPI extends API {
    public BlindTestAPI(Logger logger, EntityManagerFactory factory){
        super(logger, factory);
    }
}
