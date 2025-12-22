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

        String sql = "SELECT id FROM users WHERE username = ?";

        try (PreparedStatement preparedStatement =
                     db_manager.getConnection().prepareStatement(sql)) {

            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next(); //if exists true, if not false
        }
    }

    public boolean isLoginValid(String username, String password) throws SQLException {
        String sql = "SELECT id FROM users WHERE username = ? AND password = ?";

        try (PreparedStatement preparedStatement =
                db_manager.getConnection().prepareStatement(sql)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next();
        }
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

    public boolean isSecurityAnswerCorrect(String username, String answer) {
        String securityAnswer = "SELECT security_answer From users WHERE username = ?";

        return false;
    }

}
