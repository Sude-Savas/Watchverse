package Model;

import java.io.Serializable;

public class Item implements Serializable {
    private String title;
    private String type;
    private String genres;
    private String apiId;
    private String posterUrl;


    public Item(String title, String type, String genres, String apiId,String posterUrl) {
        this.title = title;
        this.type = type;
        this.genres = genres;
        this.apiId = apiId;
        this.posterUrl = posterUrl;
    }


    public String getTitle() { return title; }
    public String getType() { return type; }
    public String getGenres() { return genres; }
    public String getApiId() { return apiId; }
    public String getPosterUrl() { return posterUrl; }

    @Override
    public String toString() {
        return title + " [" + type + "] (" + genres + ")";
    }
}