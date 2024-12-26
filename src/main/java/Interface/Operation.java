package Interface;

import jakarta.persistence.EntityManagerFactory;

/**
 * This class can be interpreted as a state machine and as we run implementations of it <br/>
 * This allows us to keep a pretty simple main loop inside the
 * {@link App#run()}
 * method while having deeper logic within the app
 */
public interface Operation {
    void run(EntityManagerFactory emf);

    Operation getNextOperation();
}
