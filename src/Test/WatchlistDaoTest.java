package Test;

import DataBase.Dao.WatchlistDao;
import Model.Item;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.sql.SQLException;
import java.util.List;
import static org.junit.Assert.*;

public class WatchlistDaoTest {

    private WatchlistDao watchlistDao;

    //Please use an existing account you created for this test
    //I've used mine so do not forget to change to yours
    private final String TEST_USER = "Sude123";
    private final String TEST_LIST_NAME = "JUnit_Test";

    @Before
    public void setUp() {
        watchlistDao = new WatchlistDao();
    }

    @After
    public void tearDown() throws SQLException {
        //Clean the list after test ends
        watchlistDao.deleteWatchlist(TEST_USER, TEST_LIST_NAME);
    }

    @Test
    public void testCreateAndFindWatchlist() throws SQLException {
        //Create a new list
        boolean isCreated = watchlistDao.createWatchlist(TEST_USER, TEST_LIST_NAME, "PRIVATE");
        assertTrue("Could not create the watchlist", isCreated);

        //Try to get watchlist's ID from the database
        int listId = watchlistDao.getWatchlistId(TEST_USER, TEST_LIST_NAME);

        //If ID is not -1 , it exists in database
        assertNotEquals("Watchlist was not found in the database", -1, listId);
    }

    @Test
    public void testAddItem() throws SQLException {
        //First create the list
        watchlistDao.createWatchlist(TEST_USER, TEST_LIST_NAME, "PRIVATE");

        //Create a dump movie
        Item item = new Item("Test movie", "MOVIE", "Drama", "tt999999", "url_null");

        //Add movie to the watchlist
        String result = watchlistDao.addItemToWatchlist(TEST_USER, TEST_LIST_NAME, item);
        assertEquals("An error occurred while trying to add the movie", "SUCCESS", result);

        //Look into the list and check if the movie is there or not
        List<Item> items = watchlistDao.getItemsInWatchlist(TEST_USER, TEST_LIST_NAME);
        assertFalse("Watchlist is empty,movie is not added!", items.isEmpty());
        assertEquals("Existing movie's title is incorrect", "Test movie", items.get(0).getTitle());
    }
}
