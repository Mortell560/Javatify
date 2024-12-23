package Interface.SongRelated;

import Interface.Operation;
import jakarta.persistence.EntityManagerFactory;

public class MusicConsulting implements Operation {
    public MusicConsulting(String args) {}

    public void run(EntityManagerFactory factory) {

    }

    public Operation getNextOperation() {
        return null;
    }
}
