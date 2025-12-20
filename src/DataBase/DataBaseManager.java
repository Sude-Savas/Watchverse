package DataBase;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class DataBaseManager {

    private static final String DB_name = "watchverse_db";
    private String DB_URL;
    private String SERVER_URL;
    private String USER;
    private String PASSWORD;

    private Connection connection;

    public DataBaseManager() {
        loadConfig();
        initializeDataBase();
    }

    //for safety, we get username and password from config.properties
    //Use your own username and password at testing
    private void loadConfig() {
        Properties props = new Properties();

        try (FileInputStream fis = new FileInputStream("config.properties")) {
            props.load(fis);

            SERVER_URL = props.getProperty("db.server.url");
            DB_URL     = props.getProperty("db.db.url");
            USER       = props.getProperty("db.user");
            PASSWORD   = props.getProperty("db.password");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void initializeDataBase() {

        try (
                Connection tempConnection =
                        DriverManager.getConnection(SERVER_URL, USER, PASSWORD);
                Statement statement = tempConnection.createStatement()
        ) {

            statement.executeUpdate(
                    "CREATE DATABASE IF NOT EXISTS watchverse_db"
            );

            connect();
            createTables();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
        }
    }

    private void createTables() throws SQLException {
        Statement statement = connection.createStatement();

        // Table for Users
        String sqlUsers =
                "CREATE TABLE IF NOT EXISTS users (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "username VARCHAR(50) UNIQUE NOT NULL, " +
                        "password VARCHAR(255) NOT NULL" +
                        ") ENGINE=InnoDB";
        statement.execute(sqlUsers);

        // Table for Lists
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

        // Table for Contents
        String sqlItems =
                "CREATE TABLE IF NOT EXISTS list_items (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "watchlist_id INT NOT NULL, " +
                        "title VARCHAR(200) NOT NULL, " +
                        "content_type ENUM('MOVIE','SERIES') DEFAULT 'MOVIE', " +
                        "genres VARCHAR(255), " + // Type of the content
                        "api_id VARCHAR(50), " +
                        "priority INT DEFAULT 1, " +
                        "status ENUM('WATCHING','FINISHED','PLANNING') DEFAULT 'PLANNING', " +
                        "current_episode INT DEFAULT 0, " +
                        "total_episodes INT DEFAULT 1, " +
                        "FOREIGN KEY (watchlist_id) REFERENCES watchlists(id) ON DELETE CASCADE" +
                        ") ENGINE=InnoDB";
        statement.execute(sqlItems);

        statement.close();

    }
}
