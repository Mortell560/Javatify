package Interface.Menu;

import Interface.Operation;
import jakarta.persistence.EntityManagerFactory;

public class BlindTestMenu implements Operation {
    private Operation nextOperation;
    public BlindTestMenu(){}

    private void setNextOperation(Operation nextOperation) {
        this.nextOperation = nextOperation;
    }

    public Operation getNextOperation() {
        return nextOperation;
    }

    public void run(EntityManagerFactory emf) {

    }
}
