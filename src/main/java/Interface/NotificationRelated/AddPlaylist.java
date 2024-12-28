package Interface.NotificationRelated;

import DatabaseAPI.AccountAPI;
import DatabaseAPI.PlaylistAPI;
import Entities.Account;
import Entities.Playlist;
import Interface.Menu.MainMenu;
import Interface.Operation;
import jakarta.persistence.EntityManagerFactory;

import java.util.Scanner;
import java.util.logging.Logger;

/**
 * Handles the notification 'add a playlist'
 */
public class AddPlaylist implements Operation {
    private Operation nextOperation;
    private AccountAPI accountAPI;
    private PlaylistAPI playlistAPI;
    private Long accId, playlistId;
    private Account account;
    private Playlist playlist;
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    public AddPlaylist(String args) {
        parseArgs(args);
    }

    private void parseArgs(String args) {
        String[] tokens = args.split(";");
        accId = Long.parseLong(tokens[0]);
        playlistId = Long.parseLong(tokens[1]);
    }

    public void run(EntityManagerFactory emf) {
        playlistAPI = new PlaylistAPI(logger, emf);
        accountAPI = new AccountAPI(logger, emf);
        account = accountAPI.getAccountById(accId);
        playlist = playlistAPI.getPlaylistById(playlistId);

        System.out.println("Do you want to add playlist " + playlist.getName() + "? (y/n): ");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine().trim();
        if (input.equalsIgnoreCase("y")) {
            playlistAPI.addOwnerToPlaylist(playlistId, account);
        }
        setNextOperation(new MainMenu(account));
    }

    public Operation getNextOperation() {
        return nextOperation;
    }

    private void setNextOperation(Operation nextOperation) {
        this.nextOperation = nextOperation;
    }
}
