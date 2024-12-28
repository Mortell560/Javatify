package Interface.SongRelated;

import DatabaseAPI.SongAPI;
import DatabaseAPI.StatsAPI;
import Entities.Account;
import Entities.PersonalStats;
import Entities.Song;
import Interface.Menu.MusicSearchMenu;
import Interface.Operation;
import jakarta.persistence.EntityManagerFactory;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class SongSearch implements Operation {
    private Operation nextOperation;
    private Account account;
    private SongAPI songAPI;
    private StatsAPI statsAPI;
    private final int mode;
    private Logger logger = Logger.getLogger(this.getClass().getName());
    public SongSearch(Account account, int mode){
        this.mode = mode;
        this.account = account;
    }

    public void run(EntityManagerFactory emf) {
        songAPI = new SongAPI(logger, emf);
        statsAPI = new StatsAPI(logger, emf);
        browseSongs();
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

    private void showHelp(){
        String s = "";
        s += "To order the responses that you get from the search results, use one of the following modes:\n"
                + "/title to order by titles\n"
                + "/count to order by the number of your own listening counts\n"
                + "/friend to order by the number of recommendations made by your friends\n";
        System.out.println(s);
    }

    private void browseSongs(){
        int page = 1;
        List<Song> songs = null;
        Scanner scanner = new Scanner(System.in);
        String arg = "";
        System.out.println("Enter what you want to search for: ");
        arg = scanner.nextLine().trim();

        String input = "";
        String orderBy = "";
        int choice = -1;
        boolean found = false;
        do {
            System.out.println("If you want to change of page just enter '>' or '<' and if you want to leave enter null !");
            System.out.println("If you want to change the order, do /help for more information.");
            switch (mode){
                case 1 -> songs = songAPI.getAllByTitlePaginated(arg, page);
                case 2 -> songs = songAPI.getAllByArtistPaginated(arg, page);
                case 3 -> songs = songAPI.getAllByPopularityPaginated(arg, page);
                case 4 -> songs = songAPI.getAllByTextPaginated(arg, page);
            }
            songs = orderBy(songs, orderBy);
            showSongs(songs);

            try {
                System.out.println("Enter the choice: ");
                input = scanner.nextLine().trim();
                choice = Integer.parseInt(input);
                found = true;
            }
            catch (NumberFormatException ignored){}

            if (input.equals("<")){
                page--;
                if (page < 1){ // safeguard
                    page = 1;
                }
            }
            else if (input.equals(">")){
                page++;
            }
            else if (input.contains("/")) {
                orderBy = input.replace("/", "");
            }

        } while (!input.equalsIgnoreCase("null") && !found);

        if (choice >= 0){
            Long accId = account.getId();
            Long songId = songs.get(choice).getId();
            String args = accId + ";" + songId;
            setNextOperation(new MusicConsulting(args));
        }
        else{
            setNextOperation(new MusicSearchMenu(account));
        }
    }

    private void showSongs(List<Song> songs){
        System.out.println("Num | Title | Artist | Link | Listening Count");
        int i = 0;
        for (Song song : songs){
            System.out.println(i + " - " + song.toMinimalString());
            i++;
        }
    }

    private void setNextOperation(Operation nextOperation){
        this.nextOperation = nextOperation;
    }

    public Operation getNextOperation() {
        return nextOperation;
    }
}
