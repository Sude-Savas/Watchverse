package Services;

import DataBase.Dao.UserDao;
import Model.AuthResult;

import java.sql.SQLException;

/**
 * AuthService class to
 * send appropriate
 * errors to gui.
 */


//It seems it's best practice to not directly using dao(data access objects) methods at gui
public class AuthService {

    private UserDao userDao;

    public AuthService() {
        userDao = new UserDao();
    }



    public AuthResult login(String username, String password) {
        /*
        *If user press login without filling in the fields
        * isBlank controls if the all characters all whitespaces
        * for that reason isBlank used rather than isEmpty
         */

        if (username == null || password == null) {
            return AuthResult.EMPTY_FIELDS;
        }

        if (username.isBlank() || password.isBlank()) {
            return AuthResult.EMPTY_FIELDS;
        }

        try {

            if (!userDao.isUserExists(username)) {
                return AuthResult.USER_NOT_FOUND;
            }

            if(!userDao.isLoginValid(username, password)) {
                return AuthResult.WRONG_PASSWORD;
            }

            return AuthResult.SUCCESS;

        } catch (SQLException e) {
            e.printStackTrace();
            return AuthResult.ERROR;
        }

    }

    public AuthResult register(String username, String password,
                               String securityQuestion, String securityAnswer) {

        boolean isThereNullField = username == null || password == null
                || securityQuestion == null || securityAnswer == null;

        if (isThereNullField) {
            return AuthResult.EMPTY_FIELDS;
        }

        boolean isThereEmptyField = username.isBlank() || password.isBlank()
                || securityQuestion.isBlank() || securityAnswer.isBlank();

        if (isThereEmptyField) {
            return AuthResult.EMPTY_FIELDS;
        }

        try {
            if (userDao.isUserExists(username)) {
                return AuthResult.USER_ALREADY_EXISTS;
            }

            boolean isRegistered = userDao.registerUser(
                    username, password, securityQuestion, securityAnswer
            );

            return isRegistered ? AuthResult.SUCCESS : AuthResult.ERROR;

        } catch (SQLException e) {
            e.printStackTrace();
            return AuthResult.ERROR;
        }
    }

    public AuthResult verifySecurityAnswer(String username, String securityAnswer) {
        if (username == null || securityAnswer == null) {
            return AuthResult.EMPTY_FIELDS;
        }

        if (username.isBlank() || securityAnswer.isBlank()) {
            return  AuthResult.EMPTY_FIELDS;
        }

        try {
            if (!userDao.isUserExists(username)) {
                return AuthResult.USER_NOT_FOUND;
            }
            if (!userDao.isSecurityAnswerCorrect(username, securityAnswer)) {
                return AuthResult.WRONG_SECURITY_ANSWER;
            }

            return AuthResult.SUCCESS;
        } catch (SQLException e) {
            e.printStackTrace();
            return AuthResult.ERROR;
        }
    }

    public AuthResult forgotPassword(String username, String newPassword) {

        if (username == null || newPassword == null) {
            return AuthResult.EMPTY_FIELDS;
        }

        if (username.isBlank() || newPassword.isBlank()) {
            return AuthResult.EMPTY_FIELDS;
        }

        try {
            boolean isUpdated = userDao.updatePassword(username, newPassword);

            return isUpdated ? AuthResult.PASSWORD_UPDATED : AuthResult.ERROR;
        } catch (SQLException e) {
            e.printStackTrace();
            return  AuthResult.ERROR;
        }
    }

}
