package Services;
import java.util.HashMap;
import java.util.Map;

public class GenreMapper {

    //HashMap that holds IDs and equivalent genre names
    private static final Map<Integer,String> genres = new HashMap<>();

    static {
        genres.put(28, "Action");
        genres.put(12, "Adventure");
        genres.put(16, "Animation");
        genres.put(35, "Comedy");
        genres.put(80, "Crime");
        genres.put(99, "Documentary");
        genres.put(18, "Drama");
        genres.put(10751, "Family");
        genres.put(14, "Fantasy");
        genres.put(36, "History");
        genres.put(27, "Horror");
        genres.put(10402, "Music");
        genres.put(9648, "Mystery");
        genres.put(10749, "Romance");
        genres.put(878, "Science Fiction");
        genres.put(10770, "TV Movie");
        genres.put(53, "Thriller");
        genres.put(10752, "War");
        genres.put(37, "Western");
        genres.put(10759, "Action & Adventure");
        genres.put(10765, "Sci-Fi & Fantasy");
    }

    public static String getGenreName(int id){
        return genres.getOrDefault(id,"Other");
    }
}
