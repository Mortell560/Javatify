package Interface.SongRelated;

import DatabaseAPI.PlaylistAPI;
import DatabaseAPI.SongAPI;
import DatabaseAPI.StatsAPI;
import Entities.Account;
import Entities.PersonalStats;
import Entities.Playlist;
import Entities.Song;
import Interface.Menu.PlaylistMenu;
import Interface.Operation;
import Utils.Safeguards;
import jakarta.persistence.EntityManagerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class PlaylistConsulting implements Operation {
    private Operation nextOperation;
    private Playlist playlist;
    private Account account;
    private SongAPI songAPI;
    private StatsAPI statsAPI;
    private Logger logger = Logger.getLogger(PlaylistConsulting.class.getName());
    private final int MAX_PAGE_SIZE = 20;

    public PlaylistConsulting(Account account, Playlist playlist) {
        this.account = account;
        this.playlist = playlist;
    }

    public void run(EntityManagerFactory emf) {
        songAPI = new SongAPI(logger, emf);
        statsAPI = new StatsAPI(logger, emf);

        makeChoice();
    }

    private void makeChoice() {
        System.out.println("\n*** Playlist Consulting ***\n");
        System.out.println("1. Launch playlist");
        System.out.println("2. Show playlist songs");
        System.out.println("3. Exit");

        int choice = Safeguards.getInputInterval(1, 3);
        switch (choice) {
            case 1 -> playTheList();
            case 2 -> showPlaylist();
            case 3 -> setNextOperation(new PlaylistMenu(account));
        }
    }

    private void showHelp(){
        String s = "";
        s += "To order the responses that you get from the search results, use one of the following modes:\n"
                + "/title to order by titles\n"
                + "/count to order by the number of your own listening counts\n"
                + "/friend to order by the number of recommendations made by your friends\n";
        System.out.println(s);
    }

    /**
     * Important note, the results are already ordered by title and are added in the same order so this takes care of 2 cases.
     * @param songs songs to sort
     * @param sort sorting mode
     * @return sorted songs
     */
    private List<Song> orderBy(List<Song> songs, String sort){
        if (sort.equalsIgnoreCase("help")) {
            showHelp();
        }
        else if (sort.equalsIgnoreCase("count")) {
            HashMap<Song, Integer> songStat = new HashMap<>();
            for (Song song : songs) {
                PersonalStats p = statsAPI.getPersonalStats(account, song);
                if (p != null) {
                    songStat.put(song, p.getViews());
                }
                else{
                    songStat.put(song, 0);
                }
            }
            List<Map.Entry<Song, Integer>> res = songStat.entrySet().stream().sorted(Map.Entry.comparingByValue()).toList();
            songs = res.stream().map(Map.Entry::getKey).collect(Collectors.toList());
        }
        else if (sort.equalsIgnoreCase("friend")) {
            HashMap<Song, Integer> songStat = new HashMap<>();
            for (Song song : songs) {
                PersonalStats p = statsAPI.getPersonalStats(account, song);
                if (p != null) {
                    songStat.put(song, p.getNbReco());
                }
                else{
                    songStat.put(song, 0);
                }
            }
            List<Map.Entry<Song, Integer>> res = songStat.entrySet().stream().sorted(Map.Entry.comparingByValue()).toList();
            songs = res.stream().map(Map.Entry::getKey).collect(Collectors.toList());
        }
        return songs;
    }

    private void showSongs(List<Song> songs){
        System.out.println("Num | Title | Artist | Link | Listening Count");
        int i = 0;
        for (Song song : songs){
            System.out.println(i + " - " + song.toMinimalString());
            i++;
        }
    }

    private void showPlaylist() {
        int page = 1;
        List<Song> songs;
        Scanner scanner = new Scanner(System.in);

        String input;
        String orderBy = "";

        do {
            System.out.println("If you want to change of page just enter '>' or '<' and if you want to leave enter null !");
            System.out.println("If you want to change the order, do /help for more information.");
            songs = playlist.getSongs();
            songs = Safeguards.cutInto(songs, page, MAX_PAGE_SIZE);
            songs = orderBy(songs, orderBy);
            showSongs(songs);
            System.out.println("Enter your choice: ");
            input = scanner.nextLine().trim();
            if (input.contains("/")) {
                orderBy = input.replace("/", "");
            } else if (input.contains("<")) {
                page--;
                if (page < 1){ // Safeguard
                    page = 1;
                }
            } else if (input.contains(">")) {
                page++;
            }

        } while(!input.equalsIgnoreCase("null"));

        setNextOperation(new PlaylistConsulting(account, playlist));
    }

    private void playTheList(){
        List<Song> songs = playlist.getSongs();
        Scanner scanner = new Scanner(System.in);
        int i = 0;
        String input = "";

        do {
            System.out.println(songs.get(i));
            statsAPI.addView(account, songs.get(i));
            songAPI.addListening(songs.get(i));
            System.out.println("Enter '>' or '<' and if you want to leave enter null !");
            input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("<")){
                i--;
                if (i < 0){
                    i = songs.size()-1;
                }
            }
            else if (input.equalsIgnoreCase(">")){
                i++;
                if (i == songs.size()){
                    i = 0;
                }
            }
        } while (!input.equalsIgnoreCase("null"));

        setNextOperation(new PlaylistConsulting(account, playlist));
    }

    public Operation getNextOperation() {
        return nextOperation;
    }

    private void setNextOperation(Operation nextOperation) {
        this.nextOperation = nextOperation;
    }
}
