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

public class ApiManager {

    private static final String Base_URL = "https://api.themoviedb.org/3/search/"; //Address of TMDB
    private String apiKey;

    public ApiManager(){
        loadApiKey();
    }

    //Instead of writing the API key directly, I used a safer method. I stored the API key in a file and I am reading it from there.
    private void loadApiKey(){
        Properties prop = new Properties();
        try(InputStream IS = new BufferedInputStream(new FileInputStream("config.properties"))) {
            prop.load(IS);
            this.apiKey =prop.getProperty("TMDB_API_KEY");
        }
        catch(IOException e){
            System.out.println("Error");
            e.printStackTrace();
        }
    }

    public String search(String query,String type) {

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
}
