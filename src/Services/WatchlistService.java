package Services;

import DataBase.Dao.WatchlistDao;
import Model.Item;
import java.sql.SQLException;
import java.util.List;

public class WatchlistService {

    private WatchlistDao watchlistDao;

    public WatchlistService() {
        this.watchlistDao = new WatchlistDao();
    }

    // Request for creating the watchlist
    public boolean createWatchlist(String username, String listName,String visibility) {
        if (listName == null || listName.trim().isEmpty()) {
            return false; //There must be a title
        }
        try {
            return watchlistDao.createWatchlist(username, listName,visibility);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Request for gathering the watchlists
    public List<String> getUserWatchlists(String username) {
        try {
            return watchlistDao.getUserWatchlists(username);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Request for adding movie or series to the watchlist
    public boolean addItem(String username, String listName, Item item) {
        try {
            return watchlistDao.addItemToWatchlist(username, listName, item);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Request for gathering the contents of the watchlist
    public List<Item> getListItems(String username, String listName) {
        try {
            return watchlistDao.getItemsInWatchlist(username, listName);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}