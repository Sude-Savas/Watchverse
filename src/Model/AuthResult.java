package Model;

public enum AuthResult {
    //Errors to return
    SUCCESS,
    EMPTY_FIELDS,
    USER_NOT_FOUND,
    USER_ALREADY_EXISTS,
    WRONG_SECURITY_ANSWER,
    WRONG_PASSWORD,
    WEAK_PASSWORD,
    PASSWORD_UPDATED,
    ERROR;
}
