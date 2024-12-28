package Interface.SongRelated;

import DatabaseAPI.PlaylistAPI;
import DatabaseAPI.SongAPI;
import DatabaseAPI.StatsAPI;
import Entities.Account;
import Entities.Playlist;
import Interface.Operation;
import jakarta.persistence.EntityManagerFactory;

public class PlaylistConsulting implements Operation {
    private Operation nextOperation;
    private Playlist playlist;
    private Account account;
    private PlaylistAPI playlistAPI;
    private SongAPI songAPI;
    private StatsAPI statsAPI;

    public PlaylistConsulting(Account account, Playlist playlist) {
        this.account = account;
        this.playlist = playlist;
    }

    public void run(EntityManagerFactory emf) {

    }

    public Operation getNextOperation() {
        return nextOperation;
    }

    private void setNextOperation(Operation nextOperation) {
        this.nextOperation = nextOperation;
    }
}
