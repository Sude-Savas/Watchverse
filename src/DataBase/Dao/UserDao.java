package DataBase.Dao;

import DataBase.DataBaseManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO class responsible for handling
 * user-related database operations.
 * (login & register)
 */

public class UserDao {
    private DataBaseManager db_manager;

    public UserDao() {
        db_manager = new DataBaseManager();

    }

    //Using prepared statement against sql injections, more safe
    public boolean isUserExists(String username) throws SQLException {

        //to just check if there is a row like that, select 1 used
        String sql = "SELECT 1 FROM users WHERE username = ?";

        try (PreparedStatement preparedStatement =
                     db_manager.getConnection().prepareStatement(sql)) {

            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next(); //if exists true, if not false
        }
    }

    public String getUsername(String username) throws SQLException {
        String sql = "SELECT username FROM users WHERE username = ?";

        try (PreparedStatement preparedStatement =
                db_manager.getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();
             if (resultSet.next()) {
                 return resultSet.getString("username");
             }
             return null;
        }
    }

    public String getPassword(String username) throws SQLException {
        String sql = "SELECT password FROM users WHERE username = ?";
        try (PreparedStatement preparedStatement =
                     db_manager.getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("password");
            }
            return null;
        }
    }

    public String getSecurityAnswer(String username) throws SQLException {
        String sql = "SELECT security_answer FROM users WHERE username = ?";

        try (PreparedStatement preparedStatement =
                     db_manager.getConnection().prepareStatement(sql)) {

            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("security_answer");
            }
        }
        return null;
    }

    public String getSecurityQuestion(String username) throws SQLException {
        String securityQuestion = "SELECT security_question FROM users WHERE username = ?";

        if (!isUserExists(username)) {
            return null;
        }

        try (PreparedStatement preparedStatement =
                     db_manager.getConnection().prepareStatement(securityQuestion)) {

            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("security_question");
            }
        }
        return null; //user not found
    }
    public boolean registerUser(String username, String password,
                                String question, String answer) throws SQLException {

        String registerQuery = "INSERT INTO users (username, password, security_question, security_answer)" +
                "VALUES (?, ?, ?, ?)";

        if (isUserExists(username)) {
            return  false;
        }

        try (PreparedStatement preparedStatement =
                db_manager.getConnection().prepareStatement(registerQuery)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, question);
            preparedStatement.setString(4, answer);

            //if one register added then update is successful
            return preparedStatement.executeUpdate() == 1;
        }
    }


    public boolean updatePassword(String username, String newPassword) throws SQLException {
        String sql = "UPDATE users SET password = ? WHERE username = ?";

        try (PreparedStatement preparedStatement =
                     db_manager.getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, newPassword);
            preparedStatement.setString(2, username);

            return preparedStatement.executeUpdate() == 1;
        }
    }


}
