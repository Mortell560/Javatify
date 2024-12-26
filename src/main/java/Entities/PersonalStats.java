package Entities;

import jakarta.persistence.*;

@Entity
public class PersonalStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;
    @ManyToOne(fetch = FetchType.LAZY)
    private Song song;
    @Column
    @Basic(fetch = FetchType.LAZY)
    private int nbReco;
    @Column
    @Basic(fetch = FetchType.LAZY)
    private int views;

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getNbReco() {
        return nbReco;
    }

    public void setNbReco(int nbReco) {
        this.nbReco = nbReco;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

}
