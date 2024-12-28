package Interface.Menu;

import DatabaseAPI.NotificationAPI;
import DatabaseAPI.PlaylistAPI;
import Entities.Account;
import Entities.Notification;
import Entities.Playlist;
import Interface.Operation;
import Interface.SongRelated.PlaylistConsulting;
import Utils.Safeguards;
import jakarta.persistence.EntityManagerFactory;

import java.util.*;
import java.util.logging.Logger;

public class PlaylistMenu implements Operation {
    private Operation nextOperation;
    private Account account;
    private PlaylistAPI playlistAPI;
    private NotificationAPI notificationAPI;
    private Logger logger = Logger.getLogger(PlaylistMenu.class.getName());
    public PlaylistMenu(Account account) {
        this.account = account;
    }

    public void run(EntityManagerFactory emf) {
        playlistAPI = new PlaylistAPI(logger,emf);
        notificationAPI = new NotificationAPI(logger,emf);
        System.out.println("Choose an operation: ");
        System.out.println("1. Create Playlist");
        System.out.println("2. Delete Playlist");
        System.out.println("3. Share Playlist");
        System.out.println("4. Listen to playlist");
        System.out.println("5. Add a collaborative playlist from a friend/family");
        System.out.println("6. Exit");

        int choice = Safeguards.getInputInterval(1,6);

        switch (choice) {
            case 1 -> createPlaylist();
            case 2 -> deletePlaylist();
            case 3 -> sharePlaylist();
            case 4 -> listenPlaylist();
            case 5 -> addPlaylistCollaborative();
            case 6 -> setNextOperation(new MainMenu(account));
        }
    }

    private void addPlaylistCollaborative() {
        List<Playlist> p = playlistAPI.getAllAccessiblePlaylistsForAccount(account);
        if (p.isEmpty()) {
            System.out.println("No playlists found !");
            return;
        }
        System.out.println("Choose the playlist you want to add (0 to return): ");
        int i = 0;
        for (Playlist pp : p) {
            i++;
            System.out.println("Playlist " + i + ": " + pp.getName());
        }
        int choice = Safeguards.getInputInterval(0,i);
        if (choice == 0) {
            setNextOperation(new PlaylistMenu(account));
            return;
        }
        Playlist playlist = p.get(choice - 1);
        playlistAPI.addOwnerToPlaylist(playlist.getId(), account);
        System.out.println("Playlist " + i + ": " + playlist.getName() + " added to playlist");
    }

    private void listenPlaylist() {
        List<Playlist> p = playlistAPI.getAllPlaylistsForAccount(account);
        if (p.isEmpty()) {
            System.out.println("No playlists found !");
        }
        System.out.println("Choose the playlist you want to play (0 to return): ");
        int i = 0;
        for (Playlist pp : p) {
            i++;
            System.out.println("Playlist " + i + ": " + pp.getName());
        }
        int choice = Safeguards.getInputInterval(0,i);
        if (choice == 0) {
            setNextOperation(new PlaylistMenu(account));
            return;
        }
        Playlist playlist = p.get(choice - 1);
        setNextOperation(new PlaylistConsulting(account, playlist));
    }

    private void sharePlaylist() {
        List<Playlist> p = playlistAPI.getAllPlaylistsForAccount(account);
        p.removeIf(x -> !x.isCollaborative());
        if (p.isEmpty()) {
            System.out.println("No playlists found !");
        }
        System.out.println("Choose the playlist you want to share (0 to return): ");
        int i = 0;
        for (Playlist pp : p) {
            i++;
            System.out.println("Playlist " + i + ": " + pp.getName());
        }
        int choice = Safeguards.getInputInterval(0,i);
        if (choice == 0) {
            setNextOperation(new PlaylistMenu(account));
            return;
        }
        Playlist playlist = p.get(choice - 1);

        Set<Account> accounts = account.getFamily();
        accounts.addAll(account.getFriends());
        if (accounts.isEmpty()) {
            System.out.println("You have no one to share this playlist with !");
            setNextOperation(new PlaylistMenu(account));
            return;
        };
        int j = 0;
        System.out.println("Choose a person to share the playlist with (0 to return): ");
        for (Account a : accounts) {
            j++;
            System.out.println("Account " + j + ": " + a.getName());
        }
        int choice2 = Safeguards.getInputInterval(0,j);
        if (choice2 == 0) {
            setNextOperation(new PlaylistMenu(account));
            return;
        }
        Account acc = accounts.stream().toList().get(choice - 1);
        Notification n = new Notification();
        n.setAccount(acc);
        n.setArgs(acc.getId() + ";" + playlist.getId());
        n.setOperation(4);
        n.setMessage("User " + acc.getUsername() + " wants to share a playlist you! Playlist name: " + playlist.getName());
        n.setTime(Calendar.getInstance());
        notificationAPI.createNotification(n);
        System.out.println("Playlist shared");
        setNextOperation(new PlaylistMenu(account));
    }

    private void createPlaylist() {
        String name;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the name of the playlist: ");
        name = scanner.next().trim();
        System.out.println("Is it a collaborative playlist? (y/n): ");
        boolean response = scanner.next().trim().equalsIgnoreCase("y");
        playlistAPI.createPlaylist(name, account, response);
        System.out.println("Playlist created");
        setNextOperation(new PlaylistMenu(account));
    }

    private void deletePlaylist() {
        List<Playlist> l = playlistAPI.getAllPlaylistsForAccount(account);
        setNextOperation(new PlaylistMenu(account));
        if (l.isEmpty()) {
            System.out.println("Create a new playlist first !");
            return;
        }
        System.out.println("Choose the playlist to delete or 0 to exit: ");
        int i = 0;
        for (Playlist p : l) {
            i++;
            System.out.println("Playlist " + i + ": " + p.getName());
        }
        int choice = Safeguards.getInputInterval(0,i);
        if (choice == 0) {
            System.out.println("Returning...");
            return;
        }
        Playlist p = l.get(choice - 1);
        if (p.isCollaborative() && p.getOwners().size() > 1){
            System.out.println("Ownership of the playlist will be kept by other owners !");
        }
        playlistAPI.deletePlaylist(p.getId(), account);
        System.out.println("Playlist " + i + ": " + p.getName() + " deleted");

    }

    public Operation getNextOperation() {
        return nextOperation;
    }

    private void setNextOperation(Operation nextOperation) {
        this.nextOperation = nextOperation;
    }
}
