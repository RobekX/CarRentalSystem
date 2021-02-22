package pl.lodz.p.edu.crs.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.lodz.p.edu.crs.model.Car;

import java.util.List;

public interface CarRepository extends MongoRepository<Car,String> {
    List<Car> findByStatus(String status);
    List<Car> findByCarRentalsRenterEmailAndCarRentalsStatus(String email, String status);
    List<Car> findCarRentalsByCarRentalsRenterEmailAndCarRentalsStatus(String email, String status);
    Car findByCarRentalsRentalNumber(String rentalId);
}
