import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

class PlaylistApp {
    private final PlaylistManager playlistManager;

    public PlaylistApp() {
        playlistManager = new PlaylistManager();
    }

    public void createGUI() {
        JFrame frame = new JFrame("Music Playlist Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Music Playlist Manager", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 24));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JButton addSongButton = new JButton("Add Song");
        JButton removeSongButton = new JButton("Remove Song");
        JButton viewGeneralButton = new JButton("View General Playlist");
        JButton viewFavoritesButton = new JButton("View Favorites Playlist");
        JButton sortButton = new JButton("Sort Songs");

        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        buttonPanel.add(addSongButton);
        buttonPanel.add(removeSongButton);
        buttonPanel.add(viewGeneralButton);
        buttonPanel.add(viewFavoritesButton);
        buttonPanel.add(sortButton);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        addSongButton.addActionListener(e -> addSong());
        removeSongButton.addActionListener(e -> removeSong());
        viewGeneralButton.addActionListener(e -> viewPlaylist(playlistManager.getGeneralPlaylist()));
        viewFavoritesButton.addActionListener(e -> viewPlaylist(playlistManager.getFavoritesPlaylist()));
        sortButton.addActionListener(e -> sortSongs());

        frame.add(mainPanel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void addSong() {
        String title = JOptionPane.showInputDialog("Enter Song Title:");
        if (title == null || title.isBlank()) return;

        String artist = JOptionPane.showInputDialog("Enter Artist:");
        if (artist == null || artist.isBlank()) return;

        String genre = JOptionPane.showInputDialog("Enter Genre:");
        if (genre == null || genre.isBlank()) return;

        String durationStr = JOptionPane.showInputDialog("Enter Duration (in seconds):");
        if (durationStr == null) return;

        int duration;
        try {
            duration = Integer.parseInt(durationStr.trim());
            if (duration < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Please enter a valid non-negative number for duration.");
            return;
        }

        Song song = new Song(title, artist, genre, duration);
        playlistManager.addSongToGeneral(song);

        int addToFavorites = JOptionPane.showConfirmDialog(null, "Add to Favorites?");
        if (addToFavorites == JOptionPane.YES_OPTION) {
            playlistManager.addSongToFavorites(song);
        }
    }

    private void removeSong() {
        String title = JOptionPane.showInputDialog("Enter the title of the song to remove:");
        if (title == null || title.isBlank()) return;
        playlistManager.removeSongFromGeneral(title);
        playlistManager.removeSongFromFavorites(title);
    }

    private void viewPlaylist(Playlist playlist) {
        if (playlist.getSongs().isEmpty()) {
            JOptionPane.showMessageDialog(null, "No songs in this playlist yet.");
            return;
        }
        StringBuilder playlistDetails = new StringBuilder();
        for (Song song : playlist.getSongs()) {
            playlistDetails.append(song).append("\n");
        }
        JOptionPane.showMessageDialog(null, playlistDetails.toString());
    }

    private void sortSongs() {
        String[] options = {"Title", "Artist", "Genre", "Duration"};
        String choice = (String) JOptionPane.showInputDialog(
                null, "Sort by:", "Sort Songs",
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]
        );
        if (choice == null) return;

        switch (choice) {
            case "Title" -> playlistManager.sortSongsBy(Comparator.comparing(Song::getTitle, String.CASE_INSENSITIVE_ORDER));
            case "Artist" -> playlistManager.sortSongsBy(Comparator.comparing(Song::getArtist, String.CASE_INSENSITIVE_ORDER));
            case "Genre" -> playlistManager.sortSongsBy(Comparator.comparing(Song::getGenre, String.CASE_INSENSITIVE_ORDER));
            case "Duration" -> playlistManager.sortSongsBy(Comparator.comparingInt(Song::getDuration));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PlaylistApp().createGUI());
    }
}

abstract class Playlist {
    protected final ArrayList<Song> songs;

    public Playlist() {
        this.songs = new ArrayList<>();
    }

    public void addSong(Song song) {
        songs.add(song);
    }

    public void removeSong(String title) {
        songs.removeIf(song -> song.getTitle().equalsIgnoreCase(title));
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public abstract void displayPlaylistDetails();
}

class GeneralPlaylist extends Playlist {
    @Override
    public void displayPlaylistDetails() {
        System.out.println("General Playlist:");
        for (Song song : songs) System.out.println(song);
    }
}

class FavoritesPlaylist extends Playlist {
    @Override
    public void displayPlaylistDetails() {
        System.out.println("Favorites Playlist:");
        for (Song song : songs) System.out.println(song);
    }
}

class PlaylistManager {
    private final GeneralPlaylist generalPlaylist;
    private final FavoritesPlaylist favoritesPlaylist;

    public PlaylistManager() {
        generalPlaylist = new GeneralPlaylist();
        favoritesPlaylist = new FavoritesPlaylist();
    }

    public void addSongToGeneral(Song song) {
        generalPlaylist.addSong(song);
    }

    public void addSongToFavorites(Song song) {
        favoritesPlaylist.addSong(song);
    }

    public void removeSongFromGeneral(String title) {
        generalPlaylist.removeSong(title);
    }

    public void removeSongFromFavorites(String title) {
        favoritesPlaylist.removeSong(title);
    }

    public void sortSongsBy(Comparator<Song> comparator) {
        generalPlaylist.getSongs().sort(comparator);
        favoritesPlaylist.getSongs().sort(comparator);
    }

    public GeneralPlaylist getGeneralPlaylist() {
        return generalPlaylist;
    }

    public FavoritesPlaylist getFavoritesPlaylist() {
        return favoritesPlaylist;
    }
}

class Song implements Serializable {
    private final String title;
    private final String artist;
    private final String genre;
    private final int duration;

    public Song(String title, String artist, String genre, int duration) {
        this.title = title;
        this.artist = artist;
        this.genre = genre;
        this.duration = duration;
    }

    public String getTitle() { return title; }

    public String getArtist() { return artist; }

    public String getGenre() { return genre; }

    public int getDuration() { return duration; }

    @Override
    public String toString() {
        return title + " by " + artist + " (" + genre + ", " + duration + " seconds)";
    }
}
