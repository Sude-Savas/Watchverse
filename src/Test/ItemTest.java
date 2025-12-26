package Test;

import Model.Item;
import org.junit.Test;
import static org.junit.Assert.*;

public class ItemTest {

    @Test
    public void testItemCreation() {
        //Create dump movie
        Item movie = new Item(
                "Inception",   // Title
                "MOVIE",       // Type
                "Sci-Fi",      // Genre
                "tt1375666",   // API ID
                "poster.jpg"   // Url
        );

        //Checking with assert
        assertEquals("Title should match", "Inception", movie.getTitle());
        assertEquals("Type should match", "MOVIE", movie.getType());
        assertEquals("API ID should match", "tt1375666", movie.getApiId());

    }

    @Test
    public void testToString() {
        Item movie = new Item("Matrix", "MOVIE", "Action", "tt001", "url");
        assertNotNull("ToString should not be null", movie.toString());
    }
}