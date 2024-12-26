package Interface.NotificationRelated;

import Interface.Operation;
import jakarta.persistence.EntityManagerFactory;

public class AddFamily implements Operation {
    private Operation nextOperation;

    public AddFamily() {
    }

    public void run(EntityManagerFactory emf) {

    }

    public Operation getNextOperation() {
        return nextOperation;
    }
}
