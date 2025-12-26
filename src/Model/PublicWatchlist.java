package Model;

import java.io.Serializable;

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

    // JList ekranda SADECE ismi gösterecek
    @Override
    public String toString() {
        return name;
    }
}
