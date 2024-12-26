package Interface.Menu;

import Interface.Operation;
import jakarta.persistence.EntityManagerFactory;

public class PlaylistMenu implements Operation {
    private Operation nextOperation;
    public PlaylistMenu() {}

    public void run(EntityManagerFactory emf) {

    }

    public Operation getNextOperation() {
        return nextOperation;
    }

    private void setNextOperation(Operation nextOperation) {
        this.nextOperation = nextOperation;
    }
}
