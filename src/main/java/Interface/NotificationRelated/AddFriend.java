package Interface.NotificationRelated;

import Interface.Operation;
import jakarta.persistence.EntityManagerFactory;

public class AddFriend implements Operation {
    private Operation nextOperation;
    public AddFriend() {}

    public void run(EntityManagerFactory emf) {

    }

    private void setNextOperation(Operation nextOperation) {
        this.nextOperation = nextOperation;
    }
    public Operation getNextOperation() {
        return nextOperation;
    }
}
