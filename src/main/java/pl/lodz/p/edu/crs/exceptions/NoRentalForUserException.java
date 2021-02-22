package pl.lodz.p.edu.crs.exceptions;

public class NoRentalForUserException extends RuntimeException {
    public NoRentalForUserException(String s) {
        super(s);
    }
}
