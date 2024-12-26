package Interface.SongRelated;

import DatabaseAPI.AccountAPI;
import DatabaseAPI.PlaylistAPI;
import DatabaseAPI.SongAPI;
import Entities.Account;
import Entities.Playlist;
import Entities.Song;
import Interface.Menu.MusicSearchMenu;
import Interface.Operation;
import Utils.Safeguards;
import jakarta.persistence.EntityManagerFactory;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

public class MusicConsulting implements Operation {
    private Operation nextOperation;
    private Account account;
    private Song song;
    private SongAPI songAPI;
    private AccountAPI accountAPI;
    private PlaylistAPI playlistAPI;
    private Long accId, songId;
    private final Logger logger = Logger.getLogger(MusicConsulting.class.getName());
    public MusicConsulting(String args) {
        parseArgs(args);
    }

    public void run(EntityManagerFactory factory) {
        songAPI = new SongAPI(logger, factory);
        accountAPI = new AccountAPI(logger, factory);
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
        System.out.println("6. Recommend to a friend");
        System.out.println("7. Return back");

        int choice = Safeguards.getInputInterval(1, 7);

        switch (choice){
            case 1 -> System.out.println(song);
            case 2 -> addToPlaylist();
            case 3 -> removeFromPlaylist();
            case 4 -> addToBlindTest();
            case 5 -> removeFromBlindTest();
            case 6 -> recommendToFriend();
        }

        if (choice != 7){
            makeChoice();
        }
    }

    private void addToBlindTest(){

    }

    private void removeFromBlindTest(){

    }

    private void recommendToFriend(){

    }

    private void addToPlaylist(){
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

    private void removeFromPlaylist(){
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
     * @param args arguments to be parsed
     */
    private void parseArgs(String args) {
        String[] argParts = args.split(";");
        accId = Long.parseLong(argParts[0]);
        songId = Long.parseLong(argParts[1]);
        if (argParts.length == 2) {
            setNextOperation(new MusicSearchMenu(account));
        }

    }

    private void setNextOperation(Operation nextOperation) {
        this.nextOperation = nextOperation;
    }

    public Operation getNextOperation() {
        return nextOperation;
    }
}
