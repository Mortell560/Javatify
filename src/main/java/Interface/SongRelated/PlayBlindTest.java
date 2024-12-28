package Interface.SongRelated;

import DatabaseAPI.BlindTestResultAPI;
import DatabaseAPI.SongAPI;
import Entities.Account;
import Entities.BlindTest;
import Entities.BlindTestResult;
import Entities.Song;
import Interface.Menu.BlindTestMenu;
import Interface.Operation;
import Utils.Safeguards;
import jakarta.persistence.EntityManagerFactory;

import java.util.*;
import java.util.logging.Logger;

/**
 * Menu that handles all the gameplay for the blindtests
 */
public class PlayBlindTest implements Operation {
    private final Account account;
    private final BlindTest test;
    private final Logger logger = Logger.getLogger(PlayBlindTest.class.getName());
    private Operation nextOperation;
    private SongAPI songAPI;
    private BlindTestResultAPI resultAPI;

    public PlayBlindTest(Account account, BlindTest blindTest) {
        this.account = account;
        test = blindTest;
    }

    public void run(EntityManagerFactory emf) {
        songAPI = new SongAPI(logger, emf);
        resultAPI = new BlindTestResultAPI(logger, emf);
        System.out.println("Are you ready to play ? (y/n)");
        Scanner scanner = new Scanner(System.in);
        String answer = scanner.nextLine().trim();
        setNextOperation(new BlindTestMenu(account));
        if (answer.equalsIgnoreCase("y")) {
            System.out.println("Have fun !");
            switch (test.getMODE()) {
                case 1 -> play1();
                case 2 -> play2();
                case 3 -> play3();
            }
        } else {
            System.out.println("Returning to previous menu");
        }
    }

    /**
     * Plays a blind test with the exact title
     */
    private void play1() {
        List<Song> songs = test.getSongs();
        BlindTestResult br = new BlindTestResult();
        br.setUser(account);
        br.setTest(test);
        br.setTimestamp(Calendar.getInstance());
        br.setTotal(0);
        br.setSuccess(0);
        for (Song song : songs) {
            String partial = getPartialLyrics(song.getText(), 2);
            String ans = song.getTitle();
            Scanner scanner = new Scanner(System.in);
            System.out.println(partial);
            System.out.println("Give the title of the song: ");
            String input = scanner.nextLine().trim();
            boolean correct = Objects.equals(ans, input);

            br.setTotal(br.getTotal() + 1);
            br.setSuccess(correct ? br.getSuccess() + 1 : br.getSuccess());

        }
        System.out.println("Thanks for playing ! Your score is " + br.getSuccess() + " out of " + br.getTotal());
        resultAPI.createBTResult(br);
    }

    /**
     * Plays a blind test with the exact artist
     */
    private void play2() {
        List<Song> songs = test.getSongs();
        BlindTestResult br = new BlindTestResult();
        br.setUser(account);
        br.setTest(test);
        br.setTimestamp(Calendar.getInstance());
        br.setTotal(0);
        br.setSuccess(0);
        for (Song song : songs) {
            String partial = getPartialLyrics(song.getText(), 2);
            String ans = song.getArtist();
            System.out.println(partial);
            Scanner scanner = new Scanner(System.in);
            System.out.println("Give the artist of the song: ");
            String input = scanner.nextLine().trim();
            boolean correct = Objects.equals(ans, input);

            br.setTotal(br.getTotal() + 1);
            br.setSuccess(correct ? br.getSuccess() + 1 : br.getSuccess());

        }
        System.out.println("Thanks for playing ! Your score is " + br.getSuccess() + " out of " + br.getTotal());
        resultAPI.createBTResult(br);
    }

    /**
     * Plays a blind test with a multiple answer format
     */
    private void play3() {
        List<Song> songs = test.getSongs();
        BlindTestResult br = new BlindTestResult();
        br.setUser(account);
        br.setTest(test);
        br.setTimestamp(Calendar.getInstance());
        br.setTotal(0);
        br.setSuccess(0);
        for (Song song : songs) {
            String partial = getPartialLyrics(song.getText(), 2);
            String ans = song.getTitle() + " by " + song.getArtist();
            List<String> solutions = getAllRandomChoices(ans);
            System.out.println(partial);
            System.out.println("Choose an option: ");
            for (int i = 0; i < solutions.size(); i++) {
                System.out.println(i + ". - " + solutions.get(i));
            }
            boolean correct = Objects.equals(solutions.get(Safeguards.getInputInterval(0, solutions.size() - 1)), ans);
            br.setTotal(br.getTotal() + 1);
            br.setSuccess(correct ? br.getSuccess() + 1 : br.getSuccess());
        }
        System.out.println("Thanks for playing ! Your score is " + br.getSuccess() + " out of " + br.getTotal());
        resultAPI.createBTResult(br);
    }

    /**
     * Shuffles random song choices with the correct answer
     * @param answer the correct answer
     * @return a shuffled list of the answers
     */
    private List<String> getAllRandomChoices(String answer) {
        List<String> randomChoices = new ArrayList<>();
        randomChoices.add(answer);
        for (int i = 0; i < test.getNbChoices() - 1; i++) {
            Song s = songAPI.getRandomSong();
            randomChoices.add(s.getTitle() + " by " + s.getArtist());
        }
        Collections.shuffle(randomChoices);
        return randomChoices;
    }

    /**
     * Separates the lyrics and only keep <code>linesToKeep</code> sentences
     * @param lyrics lyrics of the song
     * @param linesToKeep number of lines to keep
     * @return the partial lyrics
     */
    private String getPartialLyrics(String lyrics, int linesToKeep) {
        String[] lyricsArray = lyrics.split("\n");
        List<String> lyricsList = new ArrayList<>(Arrays.stream(lyricsArray).toList());
        lyricsList.removeIf(x -> x.length() <= 10);
        Random rand = new Random();
        int randomLyricsIndex = rand.nextInt(lyricsList.size() - linesToKeep);
        List<String> res = lyricsList.subList(randomLyricsIndex, randomLyricsIndex + linesToKeep);
        return String.join("\n", res);
    }

    public Operation getNextOperation() {
        return nextOperation;
    }

    private void setNextOperation(Operation nextOperation) {
        this.nextOperation = nextOperation;
    }
}
