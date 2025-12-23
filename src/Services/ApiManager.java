package Services;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Properties;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import Model.Item;
import java.util.StringJoiner;

public class ApiManager {

    private static final String Base_URL = "https://api.themoviedb.org/3/search/"; //Address of TMDB
    private String apiKey;

    public ApiManager() {
        loadApiKey();
    }

    //Instead of writing the API key directly, I used a safer method. I stored the API key in a file and I am reading it from there.
    private void loadApiKey() {
        Properties prop = new Properties();
        try (InputStream IS = new BufferedInputStream(new FileInputStream("Services/config.properties"))) {
            prop.load(IS);
            this.apiKey = prop.getProperty("TMDB_API_KEY");
        } catch (IOException e) {
            System.out.println("Error");
            e.printStackTrace();
        }
    }

    public String search(String query, String type) {

        if (this.apiKey == null || this.apiKey.isEmpty()) { //Checks if there is an API key or not
            System.out.println("There is no API key");
            return null;
        }
        try {
            //In URL, spaces,Turkish characters and etc may create problems, thats why I've used URLEncoder.
            //It turns user's query into a more readable format (UTF_8)
            String formattedQuery = URLEncoder.encode(query.trim(), StandardCharsets.UTF_8);

            //This gives us the final endpoint URL
            String URL = Base_URL + type + "?api_key=" + this.apiKey + "&query=" + formattedQuery + "&language=tr-TR";

            //HttpClient acts as a web browser for java
            HttpClient client = HttpClient.newHttpClient();

            //Builds a standard GET request targeting the URL
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(URL)).build();

            //Sends the request to the server and waits for the reply
            //The BodyHandlers.ofString() tells the client to treat the incoming data as a text string (JSON).
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


            return response.body();
        } catch (Exception e) {
            System.out.println("Connection Error: " + e.getMessage());
            return null;
        }
    }

    public List<Item> parseResponse(String jsonResponse, String type) {
        List<Item> items = new ArrayList<>();


        //If response is empty or null return the list
        if (jsonResponse == null || jsonResponse.isEmpty()) {
            return items;
        }

        try {
            JSONObject object = new JSONObject(jsonResponse);

            JSONArray results = object.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {
                JSONObject rawItem = results.getJSONObject(i);

                String apiID = String.valueOf(rawItem.getInt("id"));

                String title;
                if (type.equals("movie")) {
                    title = rawItem.optString("title");
                } else {
                    title = rawItem.optString("name");
                }

                //Combining genres using StringJoiner
                StringJoiner joiner = new StringJoiner(", ");

                JSONArray genreIds = rawItem.optJSONArray("genre_ids");
                if (genreIds != null) {
                    for (int j = 0; j < genreIds.length(); j++) {
                        int id = genreIds.getInt(j);

                        //Receiving title from GenreMapper class and adding them
                        String genreName = GenreMapper.getGenreName(id);
                        joiner.add(genreName);
                    }
                }

                String finalGenres = joiner.toString();

                //Adding to the list
                Item newItem = new Item(title, type.toUpperCase(), finalGenres, apiID);
                items.add(newItem);
            }
        } catch (Exception e) {

            System.out.println("Json Parse Error" + e.getMessage());
            e.printStackTrace();
        }

        return items;
    }
}
