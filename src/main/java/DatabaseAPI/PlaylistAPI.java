package DatabaseAPI;

import Entities.Account;
import Entities.Playlist;
import Entities.Song;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class PlaylistAPI extends API {
    public PlaylistAPI(Logger logger, EntityManagerFactory factory) {
        super(logger, factory);
    }

    public List<Song> getAllSongs(Long id) {
        return super.getById(Playlist.class, id).getSongs();
    }

    public void createPlaylist(String title, Account owner, boolean collaborative) {
        Playlist playlist = new Playlist();
        playlist.setName(title);
        Set<Account> s = new HashSet<>();
        s.add(owner);
        playlist.setOwners(s);
        playlist.setSongs(new ArrayList<>());
        playlist.setCollaborative(collaborative);
        super.createObject(playlist);
    }

    public List<Playlist> getAllPlaylistsForAccount(Account account) {
        List<Playlist> p = super.getAll(Playlist.class);
        p.removeIf(playlist -> !playlist.getOwners().contains(account));
        return p;
    }

    public void addOwnerToPlaylist(Long id, Account owner) {
        Playlist p = super.getById(Playlist.class, id);
        if (!p.isCollaborative()){
            logger.warning("This is not a collaborative playlist !");
            return;
        }
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        p = em.merge(p);
        em.refresh(p);
        p.getOwners().add(owner);
        try {
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            logger.severe(e.getMessage());
            em.getTransaction().rollback();
        }
        em.close();
    }

    public void addSongToPlaylist(Long id, Song song) {
        Playlist p = super.getById(Playlist.class, id);
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        p = em.merge(p);
        em.refresh(p);
        p.getSongs().add(song);
        try {
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            logger.severe(e.getMessage());
            em.getTransaction().rollback();
        }
        em.close();
    }

    public void removeSongFromPlaylist(Long id, Song song) {
        Playlist p = super.getById(Playlist.class, id);
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        p = em.merge(p);
        em.refresh(p);
        p.getSongs().remove(song);
        try {
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            logger.severe(e.getMessage());
            em.getTransaction().rollback();
        }
        em.close();
    }

    /**
     * Removes a user from a playlist <br/>
     * <b>This method will only remove the playlist if there's no owner left to it<b/>
     * @param id id of the playlist affected
     * @param owner Account to remove from the playlist
     */
    public void deletePlaylist(Long id, Account owner) {
        Playlist p = super.getById(Playlist.class, id);
        if (p.isCollaborative()){
            removeFromCollaborative(p, owner);
        }
        else {
            super.deleteObjectById(Playlist.class, id);
        }
    }

    private void removeFromCollaborative(Playlist playlist, Account owner) {
        EntityManager em = entityManagerFactory.createEntityManager();
        playlist.getOwners().remove(owner);
        if (playlist.getOwners().isEmpty()){
            super.deleteObjectById(Playlist.class, playlist.getId());
            return;
        }
        em.getTransaction().begin();
        Playlist p = em.find(Playlist.class, playlist.getId());
        Set<Account> s = p.getOwners();
        s.remove(owner);
        p.setOwners(s);
        try {
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            logger.severe(e.getMessage());
            em.getTransaction().rollback();
        }
        em.close();
    }
}
