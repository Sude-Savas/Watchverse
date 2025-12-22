package Model;

public enum AuthResult {
    //Errors to return
    SUCCESS,
    EMPTY_FIELDS,
    USER_NOT_FOUND,
    WRONG_PASSWORD,
    USER_ALREADY_EXISTS,
    WRONG_SECURITY_ANSWER,
    PASSWORD_UPDATED,
    ERROR;
}
