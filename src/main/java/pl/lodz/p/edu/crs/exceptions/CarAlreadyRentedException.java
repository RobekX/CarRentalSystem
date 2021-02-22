package pl.lodz.p.edu.crs.exceptions;

public class CarAlreadyRentedException extends RuntimeException {
    public CarAlreadyRentedException(String s) {
        super(s);
    }
}
