package Model;

public class Item {
    private String title;
    private String type;
    private String genres;
    private String apiId;


    public Item(String title, String type, String genres, String apiId) {
        this.title = title;
        this.type = type;
        this.genres = genres;
        this.apiId = apiId;
    }


    public String getTitle() { return title; }
    public String getType() { return type; }
    public String getGenres() { return genres; }
    public String getApiId() { return apiId; }


    @Override
    public String toString() {
        return title + " [" + type + "] (" + genres + ")";
    }
}