package Model;

import java.io.Serializable;

/**
 * A simplified model representing a watchlist shared publicly.
 * Used for the "Discover" feature to list available watchlists without loading all items initially.
 */
public class PublicWatchlist implements Serializable {

    private int id;
    private String name;

    public PublicWatchlist(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
