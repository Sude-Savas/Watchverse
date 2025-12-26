package DataBase;

import java.sql.*;
import java.util.Properties;
import java.io.InputStream;

/**
 * Manager class that handles database connection,
 * schema initialization, and table creation.
 */
public class DataBaseManager {

    private static final String DB_NAME = "watchverse_db";

    private String DB_URL;
    private String SERVER_URL;
    private String USER;
    private String PASSWORD;

    private Connection connection;

    public DataBaseManager() {
        loadConfig();
        initializeDataBase();
    }

    /**
     * Reads database credentials from the 'config.properties' file.
     * This keeps sensitive information separate from the source code.
     */
    private void loadConfig() {
        Properties props = new Properties();

        try (InputStream in = DataBaseManager.class.getClassLoader().getResourceAsStream("config.properties")) {

            if (in == null) {
                throw new RuntimeException("config.properties not found in classpath");
            }

            props.load(in);

            SERVER_URL = props.getProperty("db.server.url");
            DB_URL = props.getProperty("db.db.url");
            USER = props.getProperty("db.user");
            PASSWORD = props.getProperty("db.password");

        } catch (Exception e) {
            throw new RuntimeException("Could not load database configuration", e);
        }
    }



    //Creates the database if it doesn't exist and proceeds to initialize tables.
    private void initializeDataBase() {

        try (
                Connection tempConnection =
                        DriverManager.getConnection(SERVER_URL, USER, PASSWORD);
                Statement statement = tempConnection.createStatement()
        ) {

            statement.executeUpdate(
                    "CREATE DATABASE IF NOT EXISTS " + DB_NAME
            );

            connect();
            createTables();

        } catch (SQLException e) {
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    //Establishes a connection to the specific application database.
    private void connect() throws SQLException {

        if (DB_URL == null || USER == null) {
            throw new SQLException("Database configuration is not loaded properly");
        }

        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
        }
    }

    //Creates all necessary tables with Foreign Key constraints and Indexes.
    private void createTables() throws SQLException {
        try (Statement statement = connection.createStatement()) {

            //User accounts
            String sqlUsers =
                    "CREATE TABLE IF NOT EXISTS users (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY, " +
                            "username VARCHAR(50) UNIQUE NOT NULL, " +
                            "security_question VARCHAR(255) NOT NULL, " +
                            "security_answer VARCHAR(255) NOT NULL, " +
                            "password VARCHAR(255) NOT NULL" +
                            ") ENGINE=InnoDB";
            statement.execute(sqlUsers);

            //Personal watchlist
            String sqlLists =
                    "CREATE TABLE IF NOT EXISTS watchlists (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY, " +
                            "user_id INT NOT NULL, " +
                            "name VARCHAR(100) NOT NULL, " +
                            "visibility ENUM('PRIVATE','LINK_ONLY','PUBLIC') DEFAULT 'PRIVATE', " +
                            "share_token VARCHAR(100), " +
                            "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
                            ") ENGINE=InnoDB";
            statement.execute(sqlLists);

            //Social groups
            String sqlGroups =
                    "CREATE TABLE IF NOT EXISTS user_groups (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY, " +
                            "owner_id INT NOT NULL, " +
                            "name VARCHAR(100) NOT NULL, " +
                            "join_code VARCHAR(10) UNIQUE, " +
                            "FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE" +
                            ") ENGINE=InnoDB";
            statement.execute(sqlGroups);

            //Users in groups
            String sqlGroupMembers =
                    "CREATE TABLE IF NOT EXISTS group_members (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY, " +
                            "group_id INT NOT NULL, " +
                            "user_id INT NOT NULL, " +
                            "FOREIGN KEY (group_id) REFERENCES user_groups(id) ON DELETE CASCADE, " +
                            "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE, " +
                            "UNIQUE KEY unique_member (group_id, user_id)" +
                            ") ENGINE=InnoDB";
            statement.execute(sqlGroupMembers);

            //Watchlists shared by groups
            String sqlGroupWatchlists =
                    "CREATE TABLE IF NOT EXISTS group_watchlists (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY, " +
                            "group_id INT NOT NULL, " +
                            "watchlist_id INT NOT NULL, " +
                            "added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                            "FOREIGN KEY (group_id) REFERENCES user_groups(id) ON DELETE CASCADE, " +
                            "FOREIGN KEY (watchlist_id) REFERENCES watchlists(id) ON DELETE CASCADE, " +
                            "UNIQUE KEY unique_list_in_group (group_id, watchlist_id)" +
                            ") ENGINE=InnoDB";
            statement.execute(sqlGroupWatchlists);

            //Movies and series within the watchlists
            String sqlItems =
                    "CREATE TABLE IF NOT EXISTS list_items (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY, " +
                            "watchlist_id INT NOT NULL, " +
                            "title VARCHAR(200) NOT NULL, " +
                            "content_type ENUM('MOVIE','SERIES','TV','SHOW') DEFAULT 'MOVIE', " +
                            "genres VARCHAR(255), " +
                            "api_id VARCHAR(50), " +
                            "poster_url VARCHAR(500), " +
                            "priority INT DEFAULT 1, " +
                            "status ENUM('WATCHING','FINISHED','PLANNING') DEFAULT 'PLANNING', " +
                            "current_episode INT DEFAULT 0, " +
                            "total_episodes INT DEFAULT 1, " +
                            "FOREIGN KEY (watchlist_id) REFERENCES watchlists(id) ON DELETE CASCADE, " +
                            "UNIQUE KEY unique_item_in_list (watchlist_id, api_id)" +
                            ") ENGINE=InnoDB";

            statement.execute(sqlItems);
        }
    }

    //Provides the active database connection to DAO classes.
    public Connection getConnection() {
        if (connection == null) {
            throw new IllegalStateException("Database connection is not initialized");
        }
        return connection;
    }
}