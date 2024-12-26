package Interface.SongRelated;

import Interface.Operation;
import jakarta.persistence.EntityManagerFactory;

public class PlaylistConsulting implements Operation {
    private Operation nextOperation;
    public PlaylistConsulting() {}

    public void run(EntityManagerFactory emf) {

    }

    public Operation getNextOperation() {
        return nextOperation;
    }

    private void setNextOperation(Operation nextOperation) {
        this.nextOperation = nextOperation;
    }
}
