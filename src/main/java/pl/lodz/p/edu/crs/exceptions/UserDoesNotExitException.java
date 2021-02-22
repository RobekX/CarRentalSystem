package pl.lodz.p.edu.crs.exceptions;

public class UserDoesNotExitException extends RuntimeException {
    public UserDoesNotExitException(String s) {
        super(s);
    }
}
