package Services;

import DataBase.Dao.WatchlistDao;
import Model.Item;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import Model.PublicWatchlist;

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
    public String addItem(String username, String listName, Item item) {
        try {
            return watchlistDao.addItemToWatchlist(username, listName, item);
        } catch (SQLException e) {
            e.printStackTrace();
            return "ERROR:" + e.getMessage();
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

    public List<PublicWatchlist> getPublicWatchlists() {
        try {
            return watchlistDao.getPublicWatchlists();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Item> getPublicListItemsById(int listId) {
        return watchlistDao.getPublicListItemsById(listId);
    }

    public boolean removeItem(String username, String listName, String apiId) {
        try {
            return watchlistDao.deleteItem(username, listName, apiId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean createGroup(String username, String groupName) {
        if (groupName == null || groupName.trim().isEmpty()) return false;
        try {
            return watchlistDao.createGroup(username, groupName);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String> getUserGroups(String username) {
        try {
            return watchlistDao.getUserGroups(username);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<String> getLinkOnlyLists(String username) {
        try {
            return watchlistDao.getUserLinkOnlyWatchlists(username);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean addListToGroup(String username, String groupName, String listName) {
        try {
            return watchlistDao.addWatchlistToGroup(username, groupName, listName);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String> getGroupWatchlists(String username, String groupName) {
        try {
            return watchlistDao.getGroupWatchlists(username, groupName);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    public String getWatchlistVisibility(String username, String listName) throws SQLException {
        return watchlistDao.getWatchlistVisibility(username, listName);
    }

}