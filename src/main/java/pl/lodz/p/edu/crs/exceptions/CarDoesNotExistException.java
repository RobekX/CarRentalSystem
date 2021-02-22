package pl.lodz.p.edu.crs.exceptions;

public class CarDoesNotExistException extends RuntimeException {
    public CarDoesNotExistException(String s) {
        super(s);
    }
}
