package pl.lodz.p.edu.crs.dao;

import pl.lodz.p.edu.crs.model.Car;
import pl.lodz.p.edu.crs.model.CarRental;
import pl.lodz.p.edu.crs.model.User;

import java.util.List;

public interface UserDao {
    User addUser(User user);
    Boolean login(String login);
    Boolean logout();
    User updateUser(User user);
    User deleteUser(String userId);
    User changeUserStatus(String userId,String status);
    byte[] generateRentalCost(String userId);
    boolean rentACar(String carId,String username, CarRental rental);
    boolean returnCar(String userId, String carId);
    Car findNearestCar(Double N, Double E);
    List<Car> getUserRentedCars(String email, String status);
    boolean orderCar(Double N, Double E);
    byte[] generateRentalReport(String rentalId);
    byte[] generateInvoice(String rentalId);
    boolean makePayment(String rentalId);
    List<CarRental> getUserRentals(String username, String status);
    User getUserDetails(String userName);
}
