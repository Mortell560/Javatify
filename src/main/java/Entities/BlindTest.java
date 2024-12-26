package Entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "BlindTest")
public class BlindTest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @OneToOne
    private Account owner;
    @OneToMany
    private List<Account> invitedAccounts;
    @OneToMany
    private List<Song> songs = new ArrayList<>();
    @Column(nullable = false)
    @Basic
    private int MODE; // 1 for exact title, 2 for artist, 3 for both with choices

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Account> getInvitedAccounts() {
        return invitedAccounts;
    }

    public void setInvitedAccounts(List<Account> invitedAccounts) {
        this.invitedAccounts = invitedAccounts;
    }

    public int getMODE() {
        return MODE;
    }

    public void setMODE(int MODE) {
        this.MODE = MODE;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Account getOwner() {
        return owner;
    }

    public void setOwner(Account owner) {
        this.owner = owner;
    }
}
