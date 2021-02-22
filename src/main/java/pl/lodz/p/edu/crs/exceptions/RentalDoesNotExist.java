package pl.lodz.p.edu.crs.exceptions;

public class RentalDoesNotExist extends RuntimeException {
    public RentalDoesNotExist(String msg) {
        super(msg);
    }
}
