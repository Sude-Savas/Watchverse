package DataBase;

import java.sql.*;
import java.util.Properties;

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

    //for safety, we get username and password from config.properties
    //Use your own username and password at testing
    private void loadConfig() {

        //Initially we used file input stream to load config properties,
        //but it gives error because it depends on application's directory
        //with this code it can run on different machines without fail

        Properties props = new Properties();

        try {
            props.load(
                    DataBaseManager.class
                            .getClassLoader()
                            .getResourceAsStream("Services/config.properties")
            );

            SERVER_URL = props.getProperty("db.server.url");
            DB_URL = props.getProperty("db.db.url");
            USER = props.getProperty("db.user");
            PASSWORD = props.getProperty("db.password");

        } catch (Exception e) {
            throw new RuntimeException("Could not load database configuration", e);
        }
    }

    private void initializeDataBase() {

        //Creating database using server connection
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
            //Fail-fast: database must be available at startup
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    private void connect() throws SQLException {

        //Configuration safety check
        if (DB_URL == null || USER == null) {
            throw new SQLException("Database configuration is not loaded properly");
        }

        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
        }
    }

    private void createTables() throws SQLException {

        Statement statement = connection.createStatement();

        // Table for Users
        //!! for security, we add security question and answer to user table
        //(only once when registering)
        String sqlUsers =
                "CREATE TABLE IF NOT EXISTS users (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "username VARCHAR(50) UNIQUE NOT NULL, " +
                        "security_question VARCHAR(255) NOT NULL, " +
                        "security_answer VARCHAR(255) NOT NULL, " +
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

    public Connection getConnection() {
        if (connection == null) {
            throw new IllegalStateException("Database connection is not initialized");
        }
        return connection;
    }
}
