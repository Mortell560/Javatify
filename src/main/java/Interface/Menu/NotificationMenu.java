package Interface.Menu;

import Interface.Operation;
import jakarta.persistence.EntityManagerFactory;

public class NotificationMenu implements Operation {
    private Operation nextOperation;
    public NotificationMenu() {}

    public void run(EntityManagerFactory emf) {

    }

    private void setNextOperation(Operation nextOperation) {
        this.nextOperation = nextOperation;
    }

    public Operation getNextOperation() {
        return nextOperation;
    }
}
