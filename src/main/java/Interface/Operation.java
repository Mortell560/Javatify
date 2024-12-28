package Interface;

import jakarta.persistence.EntityManagerFactory;

/**
 * This class can be interpreted as a state machine and as we run implementations of it <br/>
 * This allows us to keep a pretty simple main loop inside the
 * {@link App#run()}
 * method while having deeper logic within the app
 */
public interface Operation {
    /**
     * Launch the current operation. This will affect the nextOperation that will be fetched by {@link Operation#getNextOperation()}
     * @param emf the EntityManagerFactory is crucial to connect to the database and as such only 1 instance should be used throughout the app
     */
    void run(EntityManagerFactory emf);

    /**
     * Returns the next operation to execute in the state machine. If it is null then we consider that the app needs to be closed
     * @return the next Operation
     */
    Operation getNextOperation();
}
