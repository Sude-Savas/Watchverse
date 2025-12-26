package DataBase.Dao;

import DataBase.DataBaseManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import Model.Item;
import Model.PublicWatchlist;


public class WatchlistDao {

    private DataBaseManager db_manager;

    public WatchlistDao() {
        db_manager = new DataBaseManager();
    }

    public boolean createWatchlist(String username, String listName, String visibility) throws SQLException {
        /* Default visibility is private
           Using subquery to avoid a separate SELECT query.
        */
        String sql = "INSERT INTO watchlists (user_id, name, visibility) " +
                "VALUES ((SELECT id FROM users WHERE username = ?), ?, ?)";

        try (java.sql.PreparedStatement preparedStatement =
                     db_manager.getConnection().prepareStatement(sql)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, listName);
            preparedStatement.setString(3, visibility);

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
        return -1;
    }

    public String addItemToWatchlist(String username, String listName, Item item) throws SQLException {
        int watchlistId = getWatchlistId(username, listName);
        if (watchlistId == -1) return "ERROR:LIST_NOT_FOUND";

        String sql = "INSERT INTO list_items (watchlist_id, title, content_type, genres, api_id, poster_url, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, 'PLANNING')";

        try (PreparedStatement ps = db_manager.getConnection().prepareStatement(sql)) {
            ps.setInt(1, watchlistId);
            ps.setString(2, item.getTitle());
            ps.setString(3, normalizeContentType(item.getType()));
            ps.setString(4, item.getGenres());
            ps.setString(5, item.getApiId());
            ps.setString(6, item.getPosterUrl());

            ps.executeUpdate();
            return "SUCCESS";
        } catch (SQLException e) {

            if (e.getErrorCode() == 1062) {
                return "ALREADY_EXISTS";
            }
            throw e;
        }
    }

    // Retrieves all contents in a list from the database and converts them into Item objects.
    public List<Item> getItemsInWatchlist(String username, String listName) throws SQLException {
        List<Item> items = new ArrayList<>();
        int watchlistId = getWatchlistId(username, listName);
        if (watchlistId == -1) return items;

        String sql = "SELECT title, content_type, genres, api_id, poster_url FROM list_items WHERE watchlist_id = ?";

        try (PreparedStatement ps = db_manager.getConnection().prepareStatement(sql)) {
            ps.setInt(1, watchlistId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String title = rs.getString("title");
                String type = rs.getString("content_type");
                String genres = rs.getString("genres");
                String apiId = rs.getString("api_id");

                String posterUrl = rs.getString("poster_url");

                items.add(new Item(title, type, genres, apiId, posterUrl));
            }
        }
        return items;
    }

    public List<PublicWatchlist> getPublicWatchlists() throws SQLException {

        List<PublicWatchlist> lists = new ArrayList<>();

        String sql = """
        SELECT id, name
        FROM watchlists
        WHERE visibility = 'PUBLIC'
        """;

        try (PreparedStatement ps = db_manager.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lists.add(
                        new PublicWatchlist(
                                rs.getInt("id"),
                                rs.getString("name")
                        )
                );
            }
        }
        return lists;
    }

    public List<Item> getPublicListItemsById(int listId) {
        List<Item> items = new ArrayList<>();

        String sql = "SELECT title, content_type, genres, api_id FROM list_items WHERE watchlist_id = ?";

        try (PreparedStatement ps = db_manager.getConnection().prepareStatement(sql)) {
            ps.setInt(1, listId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                items.add(new Item(
                        rs.getString("title"),
                        rs.getString("content_type"),
                        rs.getString("genres"),
                        rs.getString("api_id"),
                        null
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return items;
    }

    //to fit it to the enum exactly at the database
    private String normalizeContentType(String apiType) {
        if (apiType == null) return "MOVIE";
        String type = apiType.toUpperCase();
        if (type.contains("TV") || type.contains("SERIES") || type.contains("SHOW")) {
            return "SERIES";
        }
        return "MOVIE";
    }

    //Permanently deletes the item
    public boolean deleteItem(String username, String listName, String apiId) throws SQLException {
        int watchlistId = getWatchlistId(username, listName);
        if (watchlistId == -1) return false;

        String sql = "DELETE FROM list_items WHERE watchlist_id = ? AND api_id = ?";

        try (PreparedStatement ps = db_manager.getConnection().prepareStatement(sql)) {
            ps.setInt(1, watchlistId);
            ps.setString(2, apiId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean createGroup(String username, String groupName) throws SQLException {

        String sql = "INSERT INTO user_groups (owner_id, name) VALUES ((SELECT id FROM users WHERE username = ?), ?)";

        try (PreparedStatement ps = db_manager.getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, groupName);
            return ps.executeUpdate() == 1;
        }
    }

    public List<String> getUserGroups(String username) throws SQLException {
        List<String> groups = new ArrayList<>();
        String sql = "SELECT g.name FROM user_groups g JOIN users u ON g.owner_id = u.id WHERE u.username = ?";

        try (PreparedStatement ps = db_manager.getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                groups.add(rs.getString("name"));
            }
        }
        return groups;
    }

    public List<String> getUserLinkOnlyWatchlists(String username) throws SQLException {
        List<String> lists = new ArrayList<>();
        String sql = "SELECT w.name FROM watchlists w " +
                "JOIN users u ON w.user_id = u.id " +
                "WHERE u.username = ? AND w.visibility = 'LINK_ONLY'";

        try (PreparedStatement ps = db_manager.getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lists.add(rs.getString("name"));
            }
        }
        return lists;
    }

    public boolean addWatchlistToGroup(String username, String groupName, String watchlistName) throws SQLException {
        // Önce ID'leri bul
        int groupId = getGroupId(username, groupName);
        int watchlistId = getWatchlistId(username, watchlistName);

        if (groupId == -1 || watchlistId == -1) return false;

        if (!isWatchlistLinkOnly(watchlistId)) {
            return false; //if it's not link-only fail
        }

        String sql = "INSERT INTO group_watchlists (group_id, watchlist_id) VALUES (?, ?)";
        try (PreparedStatement ps = db_manager.getConnection().prepareStatement(sql)) {
            ps.setInt(1, groupId);
            ps.setInt(2, watchlistId);
            return ps.executeUpdate() == 1;
        }
    }

    private int getGroupId(String username, String groupName) throws SQLException {
        String sql = "SELECT g.id FROM user_groups g JOIN users u ON g.owner_id = u.id WHERE u.username = ? AND g.name = ?";
        try (PreparedStatement ps = db_manager.getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, groupName);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt("id") : -1;
        }
    }

    private boolean isWatchlistLinkOnly(int watchlistId) throws SQLException {
        String sql = "SELECT visibility FROM watchlists WHERE id = ?";
        try (PreparedStatement ps = db_manager.getConnection().prepareStatement(sql)) {
            ps.setInt(1, watchlistId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return "LINK_ONLY".equals(rs.getString("visibility"));
            }
        }
        return false;
    }

    public List<String> getGroupWatchlists(String username, String groupName) throws SQLException {
        List<String> result = new ArrayList<>();
        int groupId = getGroupId(username, groupName);
        if (groupId == -1) return result;

        //name of the watchlist and their owner
        String sql = "SELECT w.id, w.name, u.username FROM group_watchlists gw " +
                "JOIN watchlists w ON gw.watchlist_id = w.id " +
                "JOIN users u ON w.user_id = u.id " +
                "WHERE gw.group_id = ?";

        try (PreparedStatement ps = db_manager.getConnection().prepareStatement(sql)) {
            ps.setInt(1, groupId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String listName = rs.getString("name");
                String owner = rs.getString("username");
                result.add(id + ":" + listName + ":" + owner);
            }
        }
        return result;
    }

    public List<Item> getSharedListItems(int watchlistId) {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT title, content_type, genres, api_id, poster_url FROM list_items WHERE watchlist_id = ?";

        try (PreparedStatement ps = db_manager.getConnection().prepareStatement(sql)) {
            ps.setInt(1, watchlistId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                items.add(new Item(
                        rs.getString("title"),
                        rs.getString("content_type"),
                        rs.getString("genres"),
                        rs.getString("api_id"),
                        rs.getString("poster_url")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return items;
    }

    public String getWatchlistVisibility(String username, String listName) throws SQLException {
        String sql = "SELECT w.visibility FROM watchlists w " +
                "JOIN users u ON w.user_id = u.id " +
                "WHERE u.username = ? AND w.name = ?";

        try (PreparedStatement ps = db_manager.getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, listName);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("visibility");
            }
        }
        return "PRIVATE"; //If not found return Private
    }

    // LISTE SİLME
    public boolean deleteWatchlist(String username, String listName) throws SQLException {
        //Find the ID of the watchlist that we want to delete
        int watchlistId = getWatchlistId(username, listName);

        if (watchlistId == -1) {
            return false; //Couldnt find the watchlist
        }

        // 2. Önce listenin içindeki filmleri (list_items) silelim
        String deleteItemsSql = "DELETE FROM list_items WHERE watchlist_id = ?";
        try (PreparedStatement ps = db_manager.getConnection().prepareStatement(deleteItemsSql)) {
            ps.setInt(1, watchlistId);
            ps.executeUpdate();
        }

        // 3. Grup bağlantılarını silelim
        String deleteGroupLinksSql = "DELETE FROM group_watchlists WHERE watchlist_id = ?";
        try (PreparedStatement ps = db_manager.getConnection().prepareStatement(deleteGroupLinksSql)) {
            ps.setInt(1, watchlistId);
            ps.executeUpdate();
        }

        // 4. Son olarak listenin kendisini (watchlists) silelim
        String deleteListSql = "DELETE FROM watchlists WHERE id = ?";
        try (PreparedStatement ps = db_manager.getConnection().prepareStatement(deleteListSql)) {
            ps.setInt(1, watchlistId);
            return ps.executeUpdate() > 0;
        }
    }

    // GRUP SİLME
    public boolean deleteGroup(String username, String groupName) throws SQLException {
        // 1. Grubun ID'sini bul
        int groupId = getGroupId(username, groupName);
        if (groupId == -1) return false;

        // 2. Önce grubun içindeki liste bağlantılarını sil (Grubu boşalt)
        String deleteRelations = "DELETE FROM group_watchlists WHERE group_id = ?";
        try (PreparedStatement ps = db_manager.getConnection().prepareStatement(deleteRelations)) {
            ps.setInt(1, groupId);
            ps.executeUpdate();
        }

        // 3. Grubun kendisini sil
        String deleteGroup = "DELETE FROM user_groups WHERE id = ?";
        try (PreparedStatement ps = db_manager.getConnection().prepareStatement(deleteGroup)) {
            ps.setInt(1, groupId);
            return ps.executeUpdate() > 0;
        }
    }
}