package pl.lodz.p.edu.crs.dao;

import pl.lodz.p.edu.crs.model.Car;
import pl.lodz.p.edu.crs.model.CarRental;
import pl.lodz.p.edu.crs.model.Repair;

import java.util.List;


public interface CarDao {

    List<Car> getAllCars();
    List<Car> getCarsByStatus(String status);
    Car addCar(Car car);
    Car updateCar(Car car);
    Boolean deleteCar(String carId);
    byte[] generateReportForCar(String carId);
    Car changeCarStatus(String carId, String status);
    List<Repair> getCarRepairs(String carID);
    Car getCar(String carId);
    Boolean reserveCar(String carId);
    Boolean freeCar(String carId);
    List<Car> getUserRentedCars(String email, String status);
    Long availableWhen(String carId);
    List<CarRental> getUserRentals(String username, String status);
    Car getCarRentalByRentalId(String rentalId);
}
