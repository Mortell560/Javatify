package Interface;

import jakarta.persistence.EntityManagerFactory;

public interface Operation {
    void run(EntityManagerFactory emf);

    Operation getNextOperation();
}
