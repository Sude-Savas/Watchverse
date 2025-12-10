package DataBase;

import javax.swing.plaf.nimbus.State;
import java.sql.*;


public class DataBaseManager {

    private static final String DB_name = "watchverse_db";
    private static final String SERVER_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/" + DB_name;

    private static final String USER = "root";
    private static final String PASSWORD = "2402";

    private Connection connection;

    public DataBaseManager() {
        initializeDataBase();
    }

    private void initializeDataBase() {
        try {
            Connection tempConnection = DriverManager.getConnection(SERVER_URL, USER, PASSWORD);
            Statement statement = tempConnection.createStatement();
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_name);
            statement.close();
            tempConnection.close();


            connect();

            createTables();


        } catch (SQLException e) {
            System.out.println("SQL Error");
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

        //Table for Users
        String sqlUsers = "CREATE TABLE IF NOT EXISTS users ("+
                "id INT AUTO_INCREMENT PRIMARY KEY, "+
                "username VARCHAR(50) UNIQUE NOT NULL, "+
                "password VARCHAR(50) NOT NULL)";
        statement.execute(sqlUsers);

        //Table for Lists
        String sqlLists = "CREATE TABLE IF NOT EXISTS watchlists("+
                "id INT AUTO_INCREMENT PRIMARY KEY, "+
                "watchlist_id INT, "+
                "name VARCHAR(100) NOT NULL, "+
                "visibility ENUM('PRIVATE','LINK_ONLY','PUBLIC') DEFAULT 'PRIVATE', "+
                "share_token VARCHAR(100), "+
                "FOREIGN KEY (user_id) REFERENCES users(id))";
        statement.execute(sqlLists);

        //Table for Contents
        String sqlItems = "CREATE TABLE IF NOT EXISTS list_items(" +
                "id INT AUTO_INCREMENT PRIMARY KEY, "+
                "watchlist_id INT, "+
                "title VARCHAR(200), "+
                "content_type ENUM('MOVIE','SERIES') DEFAULT 'MOVIE',  "+
                "genres VARCHAR(255), "+ //Type of the content
                "api_id VARCHAR(50), "+
                "priority INT DEFAULT 1, "+
                "status ENUM('WATCHING','FINISHED','PLANNING') DEFAULT 'PLANNING', "+
                "current_episode INT DEFAULT 0, "+
                "total episodes INT DEFAULT 1, "+
                "FOREIGN KEY (watchlist_id) REFERENCES watchlists(id))";
        statement.execute(sqlItems);
        statement.close();
    }
}

















