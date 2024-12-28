package Interface.SongRelated;

import DatabaseAPI.*;
import Entities.*;
import Interface.Menu.MainMenu;
import Interface.Menu.MusicSearchMenu;
import Interface.Operation;
import Utils.Safeguards;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class MusicConsulting implements Operation {
    private final Logger logger = Logger.getLogger(MusicConsulting.class.getName());
    private final String args;
    private Operation nextOperation;
    private Account account;
    private Song song;
    private Playlist playlist;
    private SongAPI songAPI;
    private AccountAPI accountAPI;
    private PlaylistAPI playlistAPI;
    private BlindTestAPI blindTestAPI;
    private StatsAPI statsAPI;
    private NotificationAPI notificationAPI;
    private Long accId, songId;
    private final boolean reco = false;

    public MusicConsulting(String args) {
        this.args = args;
        parseArgs(args);
    }

    public void run(EntityManagerFactory factory) {
        songAPI = new SongAPI(logger, factory);
        accountAPI = new AccountAPI(logger, factory);
        playlistAPI = new PlaylistAPI(logger, factory);
        blindTestAPI = new BlindTestAPI(logger, factory);
        notificationAPI = new NotificationAPI(logger, factory);
        statsAPI = new StatsAPI(logger, factory);
        song = songAPI.getSongById(songId);
        account = accountAPI.getAccountById(accId);
        makeChoice();
    }

    private void makeChoice() {
        System.out.println(song.toMinimalString());
        System.out.println("Choose an operation");
        System.out.println("1. Read the song");
        System.out.println("2. Add to playlist");
        System.out.println("3. Remove from playlist");
        System.out.println("4. Add to BlindTest");
        System.out.println("5. Remove from BlindTest");
        System.out.println("6. Recommend to a friend or family member");
        System.out.println("7. Return back");

        int choice = Safeguards.getInputInterval(1, 7);

        switch (choice) {
            case 1 -> {
                System.out.println(song);
                statsAPI.addView(account, song);
                songAPI.addListening(song);
            }
            case 2 -> addToPlaylist();
            case 3 -> removeFromPlaylist();
            case 4 -> addToBlindTest();
            case 5 -> removeFromBlindTest();
            case 6 -> recommendTo();
        }

        if (reco) {
            setNextOperation(new MainMenu(account));
        } else {
            setNextOperation(new MusicSearchMenu(account));
        }
    }

    private void addToBlindTest() {
        List<BlindTest> b = blindTestAPI.getAllBTForAccount(account);
        if (b.isEmpty()) {
            System.out.println("Please create a blindtest first !");
            return;
        }
        System.out.println("Select a blindtest: ");
        int i = 0;
        for (BlindTest blindTest : b) {
            System.out.println(i + ". - " + blindTest.getName());
            i++;
        }
        int choice = Safeguards.getInputInterval(0, i);

        blindTestAPI.addSongToBT(b.get(choice), song);
    }

    private void removeFromBlindTest() {
        List<BlindTest> b = blindTestAPI.getAllBTForAccount(account);
        b.removeIf(x -> !x.getSongs().contains(song));
        if (b.isEmpty()) {
            System.out.println("No blindtest found with this song !");
            return;
        }
        System.out.println("Select a blindtest: ");
        int i = 0;
        for (BlindTest blindTest : b) {
            System.out.println(i + ". - " + blindTest.getName());
            i++;
        }
        int choice = Safeguards.getInputInterval(0, i);
        blindTestAPI.deleteSongFromBT(b.get(choice), song);
    }

    private void recommendTo() {
        Set<Account> related = account.getFriends();
        related.addAll(account.getFamily());
        List<Account> l = related.stream().toList();
        if (related.isEmpty()) {
            System.out.println("No friend or Family members found ! (Sorry)");
            return;
        }
        System.out.println("Choose who to recommend this song to: ");
        int i = 0;
        for (Account a : related) {
            System.out.println(i + ". - " + a.getUsername());
            i++;
        }
        int choice = Safeguards.getInputInterval(0, i);
        Notification notif = new Notification();
        notif.setMessage("New song recommended by " + account.getUsername() + " !");
        notif.setOperation(2);
        notif.setAccount(l.get(choice));
        String args = l.get(choice).getId() + ";" + songId;
        notif.setArgs(args);
        notificationAPI.createNotification(notif);
        statsAPI.addReco(l.get(choice), song);
    }

    private void addToPlaylist() {
        List<Playlist> p = playlistAPI.getAllPlaylistsForAccount(account);
        if (p.isEmpty()) {
            System.out.println("Please create a playlist first !");
            return;
        }
        System.out.println("Select a playlist: ");
        int i = 0;
        for (Playlist playlist : p) {
            System.out.println(i + ". - " + playlist.getName());
            i++;
        }
        int choice = Safeguards.getInputInterval(0, i);

        playlistAPI.addSongToPlaylist(p.get(choice).getId(), song);
    }

    private void removeFromPlaylist() {
        List<Playlist> p = playlistAPI.getAllPlaylistsForAccount(account);
        p.removeIf(playlist -> !playlist.getSongs().contains(song));
        if (p.isEmpty()) {
            System.out.println("No playlist containing this song found !");
            return;
        }
        System.out.println("Select a playlist: ");
        int i = 0;
        for (Playlist playlist : p) {
            System.out.println(i + ". - " + playlist.getName());
            i++;
        }
        int choice = Safeguards.getInputInterval(0, i);

        playlistAPI.removeSongFromPlaylist(p.get(choice).getId(), song);

    }

    /**
     * Parsed args for music to read are always the same: <br/>
     * accountId;songId;operation;argsForNextOp
     *
     * @param args arguments to be parsed
     */
    private void parseArgs(String args) {
        String[] argParts = args.split(";");
        accId = Long.parseLong(argParts[0]);
        songId = Long.parseLong(argParts[1]);
        if (argParts.length > 2) {

        }

    }

    public Operation getNextOperation() {
        return nextOperation;
    }

    private void setNextOperation(Operation nextOperation) {
        this.nextOperation = nextOperation;
    }
}
