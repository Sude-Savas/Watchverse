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

        if (username == null || password == null ||
        username.isBlank() || password.isBlank()) {
            return AuthResult.EMPTY_FIELDS;
        }

        try {

            if (!userDao.isUserExists(username)) {
                return AuthResult.USER_NOT_FOUND;
            }

            //there is no case sensitivity at sql, ozge,Ozge, OZGE all same
            //extra control for unique usernames
            String storedUsername = userDao.getUsername(username);

            if (storedUsername == null || !storedUsername.equals(username)) {
                return AuthResult.USER_NOT_FOUND;
            }

            String storedPassword = userDao.getPassword(username);

            if (storedPassword == null || !storedPassword.equals(password)) {
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

        if (!isPasswordStrong(password)) {
            return AuthResult.WEAK_PASSWORD;
        }

        try {
            if (userDao.isUserExists(username)) {
                return AuthResult.USER_ALREADY_EXISTS;
            }

            boolean isRegistered = userDao.registerUser(username, password,
                    securityQuestion, securityAnswer.trim()
            );

            return isRegistered ? AuthResult.SUCCESS : AuthResult.ERROR;

        } catch (SQLException e) {
            e.printStackTrace();
            return AuthResult.ERROR;
        }
    }
    public boolean isUserExists(String username) {
        try {
            return userDao.isUserExists(username);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isPasswordStrong(String password) {
        /*
        *At least 8 characters {8,}
        * At least 1 capital A-Z
        * At least 1 lower case a-z
        * At least 1 number (?=.*\\d)
         */
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";
        return password.matches(regex);
    }

    public String getSecurityQuestion(String username) {
        try {
            return userDao.getSecurityQuestion(username);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public AuthResult verifySecurityAnswer(String username, String securityAnswer) {
        if (username == null || securityAnswer == null ||
        username.isBlank() || securityAnswer.isBlank()) {
            return AuthResult.EMPTY_FIELDS;
        }

        try {
            if (!userDao.isUserExists(username)) {
                return AuthResult.USER_NOT_FOUND;
            }

            String storedAnswer = userDao.getSecurityAnswer(username);

            if (storedAnswer == null) {
                return AuthResult.ERROR;
            }

            if (!storedAnswer.trim().equalsIgnoreCase(securityAnswer.trim())) {
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

        if (!isPasswordStrong(newPassword)) {
            return AuthResult.WEAK_PASSWORD;
        }

        try {
            boolean isUpdated = userDao.updatePassword(username, newPassword);
            return isUpdated ? AuthResult.PASSWORD_UPDATED : AuthResult.ERROR;
        } catch (SQLException e) {
            e.printStackTrace();
            return  AuthResult.ERROR;
        }
    }
    //in app password change,
    public AuthResult changePassword(String username, String oldPassword, String newPassword) {

        if (username == null || oldPassword == null || newPassword == null ||
                username.isBlank() || oldPassword.isBlank() || newPassword.isBlank()) {
            return AuthResult.EMPTY_FIELDS;
        }

        try {
            String storedPassword = userDao.getPassword(username);

            if (storedPassword == null) {
                return AuthResult.USER_NOT_FOUND;
            }

            if (!storedPassword.equals(oldPassword)) {
                return AuthResult.WRONG_PASSWORD;
            }

            if  (newPassword.equals(oldPassword)) {
                return AuthResult.SAME_PASSWORD;
            }

            if (!isPasswordStrong(newPassword)) {
                return AuthResult.WEAK_PASSWORD;
            }

            boolean isUpdated = userDao.updatePassword(username, newPassword);

            return isUpdated ? AuthResult.PASSWORD_UPDATED : AuthResult.ERROR;

        } catch (SQLException e) {
            e.printStackTrace();
            return AuthResult.ERROR;
        }
    }
    //at setting dialog on app panel
    public boolean deleteAccount(String username) {
        try {
            return userDao.deleteUser(username);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
