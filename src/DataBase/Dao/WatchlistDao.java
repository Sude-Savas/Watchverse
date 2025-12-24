package DataBase.Dao;

import DataBase.DataBaseManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import Model.Item;

public class WatchlistDao {

    private DataBaseManager db_manager;

    public WatchlistDao() {
        db_manager = new DataBaseManager();
    }

    public boolean createWatchlist(String username, String listName) throws SQLException {
        /* Default visibility is private
           Using subquery to avoid a separate SELECT query.
        */
        String sql = "INSERT INTO watchlists (user_id, name, visibility) " +
                "VALUES ((SELECT id FROM users WHERE username = ?), ?, 'PRIVATE')";

        try (PreparedStatement preparedStatement =
                     db_manager.getConnection().prepareStatement(sql)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, listName);

            return preparedStatement.executeUpdate() == 1;
        }
    }

    //Gets all the watchlist titles that user has
    public List<String> getUserWatchlists(String username) throws SQLException {
        List<String> watchlists = new ArrayList<>();

        // Used JOIN to combine User and Watchlist tables
        String sql = "SELECT w.name FROM watchlists w " +
                "JOIN users u ON w.user_id = u.id " +
                "WHERE u.username = ?";

        try (PreparedStatement preparedStatement =
                     db_manager.getConnection().prepareStatement(sql)) {

            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            //Adding every watchlist in results to the list
            while (resultSet.next()) {
                watchlists.add(resultSet.getString("name"));
            }
        }
        return watchlists;
    }

    //Helper method
    public int getWatchlistId(String username, String listName) throws SQLException {

        //Used JOIN because listName might not be unique
        String sql = "SELECT w.id FROM watchlists w " +
                "JOIN users u ON w.user_id = u.id " +
                "WHERE u.username = ? AND w.name = ?";

        try (PreparedStatement ps = db_manager.getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, listName);
            ResultSet rs = ps.executeQuery();

            //If there is a result from the database, get the ID
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        //If it fails we return -1
        return -1;
    }

    /**
     This method adds selected film or series to the selected watchlist
     First find the ID of the list if found then content is added to the list
     */
    public boolean addItemToWatchlist(String username, String listName, Item item) throws SQLException {
        //Get the list ID first. If list doesn't exist, stop immediately.
        int watchlistId = getWatchlistId(username, listName);
        if (watchlistId == -1) return false;

        // Status is set as Planning
        String sql = "INSERT INTO list_items (watchlist_id, title, content_type, genres, api_id, status) " +
                "VALUES (?, ?, ?, ?, ?, 'PLANNING')";

        try (PreparedStatement ps = db_manager.getConnection().prepareStatement(sql)) {
            ps.setInt(1, watchlistId);
            ps.setString(2, item.getTitle());

            // Database stores ENUM as 'MOVIE'/'SERIES', so we ensure upper case.
            ps.setString(3, item.getType().toUpperCase());

            ps.setString(4, item.getGenres());
            ps.setString(5, item.getApiId());

            //If one row is added , Operation is successful
            return ps.executeUpdate() == 1;
        }
    }

    // Retrieves all contents in a list from the database and converts them into Item objects.
    public List<Item> getItemsInWatchlist(String username, String listName) throws SQLException {
        List<Item> items = new ArrayList<>();

        //First pick the correct watchlist
        int watchlistId = getWatchlistId(username, listName);
        if (watchlistId == -1) return items;

        String sql = "SELECT * FROM list_items WHERE watchlist_id = ?";

        try (PreparedStatement ps = db_manager.getConnection().prepareStatement(sql)) {
            ps.setInt(1, watchlistId);
            ResultSet rs = ps.executeQuery();

            // Mapping:Database to Java Objects
            while (rs.next()) {
                String title = rs.getString("title");
                String type = rs.getString("content_type");
                String genres = rs.getString("genres");
                String apiId = rs.getString("api_id");


                items.add(new Item(title, type, genres, apiId));
            }
        }
        return items;
    }


    // Updates the watching status and current episode number of an item.
    public boolean updateItemStatus(int itemId, String newStatus, int currentEpisode) throws SQLException {
        String sql = "UPDATE list_items SET status = ?, current_episode = ? WHERE id = ?";

        try (PreparedStatement ps = db_manager.getConnection().prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, currentEpisode);
            ps.setInt(3, itemId);

            return ps.executeUpdate() == 1;
        }
    }


    //Permanently deletes the item
    public boolean deleteItem(int itemId) throws SQLException {
        String sql = "DELETE FROM list_items WHERE id = ?";

        try (PreparedStatement ps = db_manager.getConnection().prepareStatement(sql)) {
            ps.setInt(1, itemId);
            return ps.executeUpdate() == 1;
        }
    }
}