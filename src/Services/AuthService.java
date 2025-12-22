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
                               String security_question, String security_answer) {
        try {
            if (userDao.isUserExists(username)) {
                return AuthResult.USER_ALREADY_EXISTS;
            }

            if (userDao.registerUser(username, password, security_question, security_answer)) {
                return AuthResult.SUCCESS;
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return AuthResult.ERROR;
    }

}
