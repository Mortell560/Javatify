package Entities;

import com.opencsv.bean.CsvBindByName;

/**
 * Middleware class used only for an easier conversion from csv to the final class Song
 */
public class SongDAO {

    @CsvBindByName(column = "artist")
    private String artist;

    @CsvBindByName(column = "song")
    private String title;

    @CsvBindByName(column = "link")
    private String link;

    @CsvBindByName(column = "text")
    private String text;

    @Override
    public String toString() {
        return "SongDAO{" +
                "artist='" + artist + '\'' +
                ", title='" + title + '\'' +
                ", link='" + link + '\'' +
                ", text='" + text + '\'' +
                '}';
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

    /**
     * Converts current "CSV" type Song to the real Song type
     * @return Song with the correct class
     */
    public Song toSong() {
        Song song = new Song();
        song.setArtist(artist);
        song.setTitle(title);
        song.setLink(link);
        song.setText(text);
        song.setListeningCount(0L);
        return song;
    }
}
