package pl.lodz.p.edu.crs.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import pl.lodz.p.edu.crs.dao.UserDao;
import pl.lodz.p.edu.crs.model.AccountType;
import pl.lodz.p.edu.crs.model.Car;
import pl.lodz.p.edu.crs.model.CarRental;
import pl.lodz.p.edu.crs.model.User;
import pl.lodz.p.edu.crs.utils.UserStatus;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UserService {

    @Autowired
    UserDao userDao;
    @Autowired
    BCryptPasswordEncoder encoder;

    public User addUser(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        user.setStatus(UserStatus.OK.name());
        user.setAccountType(AccountType.USER.name());
        user.setBalance(0L);
        user.setPayments(new ArrayList<>());
        return userDao.addUser(user);
    }

    public Boolean login(String login) {
        return userDao.login(login);
    }

    public Boolean logout() {
        return userDao.logout();
    }

    public User editUser(User user) {
        return userDao.updateUser(user);
    }

    public User deleteUser(String userId) {
        return userDao.deleteUser(userId);
    }

    public User changeUserStatus(String userId, String status) {
        return userDao.changeUserStatus(userId, status);
    }

    public byte[] generateRentalCost(String userId) {
        return userDao.generateRentalCost(userId);
    }

    public boolean rentACar(String carId, String username, CarRental rental) {
        return userDao.rentACar(carId, username, rental);
    }

    public boolean returnCar(String userId, String carId) {
        return userDao.returnCar(userId, carId);
    }

    public Car findNearestCar(Double N, Double E) {
        return userDao.findNearestCar(N, E);
    }

    public List<Car> getUserRentedCars(String email, String status) {
        return userDao.getUserRentedCars(email, status);
    }

    public boolean orderCar(Double N, Double E) {
        return userDao.orderCar(N, E);
    }

    public byte[] generateRentalReport(String rentalId) {
        return userDao.generateRentalReport(rentalId);
    }

    public byte[] generateInvoice(String rentalId) {
        return userDao.generateInvoice(rentalId);
    }

    public boolean makePayment(String rentalId) {
        return userDao.makePayment(rentalId);
    }

    public List<CarRental> getUserRentals(String username, String status) {
        return userDao.getUserRentals(username, status);
    }

    public User getUserDetails(String userName) {
        return userDao.getUserDetails(userName);
    }

    public Boolean changePassword(String userName, String password, String newPassword) {
        User user = userDao.getUserDetails(userName);
        if (encoder.matches(password, user.getPassword())) {
            user.setPassword(encoder.encode(newPassword));
            userDao.updateUser(user);
            return true;
        } else {
            log.error("Password does not match");
            return false;
        }
    }
}
