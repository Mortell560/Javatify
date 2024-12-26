package Entities;

import jakarta.persistence.*;


@Entity
@Table(name = "Song")
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String artist;
    @Column(nullable = false)
    private String title;
    @Basic(fetch = FetchType.LAZY) // Most of the time it is useless, so we won't load it
    @Column(nullable = false)
    private String link;
    @Basic(fetch = FetchType.LAZY) // Lazy fetch since lyrics can get pretty long
    @Lob
    private String text;
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false)
    private Long listeningCount;

    /**
     * Used while navigating between songs because the text can get pretty heavy
     * @return a string with the minimum of information about the song
     */
    public String toMinimalString(){
        return getTitle() + " - " + getArtist() + " - " + getLink() + " - " + getListeningCount();
    }

    @Override
    public String toString() {
        return getTitle() + " by Artist: " + getArtist() + "\n" + getText();
    }

    public Long getListeningCount() {
        return listeningCount;
    }

    public void setListeningCount(Long listeningCount) {
        this.listeningCount = listeningCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
