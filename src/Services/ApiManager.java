package Services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Proxy; // <--- Bu import çok önemli (Proxy ayarı için)
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringJoiner;

import org.json.JSONArray;
import org.json.JSONObject;

import Model.Item;

public class ApiManager {

    private static final String Base_URL = "https://api.themoviedb.org/3/search/"; //Address of TMDB
    private static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w92"; //For pictures of movies and series
    private String apiKey;

    public ApiManager() {
        loadApiKey();
    }

    //Instead of writing the API key directly, I used a safer method. I stored the API key in a file and I am reading it from there.
    private void loadApiKey() {
        Properties prop = new Properties();
        try (InputStream IS = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (IS == null) {
                System.out.println("HATA: config.properties dosyası bulunamadı! Lütfen 'Rebuild' yapın.");
                return;
            }
            prop.load(IS);
            this.apiKey = prop.getProperty("TMDB_API_KEY").trim();
            System.out.println("API Key Durumu: " + (this.apiKey != null && !this.apiKey.isEmpty() ? "Yüklendi" : "Boş"));
        } catch (IOException e) {
            System.out.println("Dosya okuma hatası");
            e.printStackTrace();
        }
    }

    public String search(String query, String type) {

        if (this.apiKey == null || this.apiKey.isEmpty()) { //Checks if there is an API key or not
            System.out.println("HATA: API Key yok, istek atılamaz.");
            return null;
        }

        HttpURLConnection connection = null;
        try {
            //In URL, spaces,Turkish characters and etc may create problems, thats why I've used URLEncoder.
            //It turns user's query into a more readable format (UTF_8)
            String formattedQuery = URLEncoder.encode(query.trim(), StandardCharsets.UTF_8);

            //This gives us the final endpoint URL
            String urlString = Base_URL + type + "?api_key=" + this.apiKey + "&query=" + formattedQuery + "&language=tr-TR";
            URL url = new URL(urlString);

            //HttpClient acts as a web browser for java (Updated to HttpURLConnection with Proxy bypass)
            //Proxy.NO_PROXY ensures we bypass local firewall/proxy blocks
            connection = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);

            //Builds a standard GET request targeting the URL
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000); // 10 saniye bekle
            connection.setReadTimeout(10000);

            //We add a User-Agent to mimic a real web browser so the API doesn't block us
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            //Sends the request to the server and waits for the reply
            int status = connection.getResponseCode();

            InputStream stream;
            if (status >= 200 && status < 300) {
                stream = connection.getInputStream(); // Success
            } else {
                stream = connection.getErrorStream(); // Error
            }

            //Reading the response (Treating incoming data as text string)
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            StringBuilder responseContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseContent.append(line);
            }
            reader.close();

            String responseBody = responseContent.toString();

            //Debug print
            System.out.println("API'den Cevap Geldi: " + responseBody);

            return responseBody;

        } catch (Exception e) {
            System.out.println("--- BAĞLANTI HATASI ---");
            System.err.println("Hata Mesajı: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
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

            if (!object.has("results")) {
                return items;
            }

            JSONArray results = object.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {
                JSONObject rawItem = results.getJSONObject(i);

                String title = rawItem.optString("title", rawItem.optString("name"));
                // If title is empty, skip
                if (title.isEmpty()) continue;

                String apiID = String.valueOf(rawItem.getInt("id"));

                String posterPath = rawItem.optString("poster_path", null);
                String fullPosterUrl = null;
                if (posterPath != null && !posterPath.isEmpty()) {
                    // Path of the poster
                    fullPosterUrl = IMAGE_BASE_URL + posterPath;
                }

                //Combining genres using StringJoiner
                StringJoiner joiner = new StringJoiner(", ");
                JSONArray genreIds = rawItem.optJSONArray("genre_ids");
                if (genreIds != null) {
                    for (int j = 0; j < genreIds.length(); j++) {
                        //Receiving title from GenreMapper class and adding them
                        joiner.add(GenreMapper.getGenreName(genreIds.getInt(j)));
                    }
                }

                String finalGenres = joiner.toString();
                if (finalGenres.isEmpty()) finalGenres = "Unknown";

                //Adding to the list
                Item newItem = new Item(title, type.toUpperCase(), finalGenres, apiID,fullPosterUrl);
                items.add(newItem);
            }
        } catch (Exception e) {
            System.out.println("Json Parse Hatası: " + e.getMessage());
        }

        return items;
    }
}