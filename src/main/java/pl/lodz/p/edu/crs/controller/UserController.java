package pl.lodz.p.edu.crs.controller;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.lodz.p.edu.crs.exceptions.UserNotLoggedInException;
import pl.lodz.p.edu.crs.model.Car;
import pl.lodz.p.edu.crs.model.CarRental;
import pl.lodz.p.edu.crs.model.User;
import pl.lodz.p.edu.crs.security.SecurityUtils;
import pl.lodz.p.edu.crs.service.UserService;
import pl.lodz.p.edu.crs.utils.Views;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/renter/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public User addUser(@RequestBody User user) {
        return userService.addUser(user);
    }

    @JsonView(Views.User.class)
    @RequestMapping(value = "/details", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public User getDetails() {
        return userService.getUserDetails(SecurityUtils.getUserName());
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Boolean changePassword(String password, String newPassword) {
        return userService.changePassword(SecurityUtils.getUserName(), password, newPassword);
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Boolean logout() {
        return userService.logout();
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public User editUser(@RequestBody User user) {
        return userService.editUser(user);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public User deleteUser(String userId) {
        return userService.deleteUser(userId);
    }

    @RequestMapping(value = "/changeStatus", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public User changeUserStatus(String userId, String status) {
        return userService.changeUserStatus(userId, status);
    }

    @RequestMapping(value = "/rentalCost", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> generateRentalCost(String userId) {
        byte[] contents = userService.generateRentalCost(userId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/pdf"));
        String filename = "output.pdf";
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return new ResponseEntity<>(contents, headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/rentCar", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public boolean rentACar(String carId, @RequestBody CarRental rental) {
        log.info("Renting a car");
        String username = SecurityUtils.getUserName();
        log.debug("USER name renter: {}", username);

        if (username.equals("Anonymous"))
            throw new UserNotLoggedInException("Użytkownik musi się zalogować przed wypożyczeniem samochodu");
        return userService.rentACar(carId, username, rental);
    }

    @RequestMapping(value = "/returnCar", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public boolean returnCar(String carId) {
        log.info("Returning car :: {}", carId);
        String username = SecurityUtils.getUserName();
        if (username.equals("Anonymous"))
            throw new UserNotLoggedInException("Użytkownik musi się zalogować przed zwróceniem samochodu");
        return userService.returnCar(username, carId);
    }

    @RequestMapping(value = "/nearest", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Car findNearestCar(Double N, Double E) {
        return userService.findNearestCar(N, E);
    }

    @JsonView(Views.Car.class)
    @RequestMapping(value = "/rentedCars", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<Car> getUserRentedCars(String status) {
        String username = SecurityUtils.getUserName();
        if (username.equals("Anonymous"))
            throw new UserNotLoggedInException("Użytkownik musi się zalogować aby mieć dostęp do swoich wypożyczeń");
        return userService.getUserRentedCars(username, status);
    }

    @JsonView(Views.CarRental.class)
    @RequestMapping(value = "/rentals", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<CarRental> checkUserRentals(String status) {
        String username = SecurityUtils.getUserName();
        if (username.equals("Anonymous"))
            throw new UserNotLoggedInException("Użytkownik musi się zalogować aby mieć dostęp do swoich wypożyczeń");
        List<CarRental> rentals = userService.getUserRentals(username, status);
        log.debug("Got rentals :: {}", rentals);
        return rentals;
    }

    @RequestMapping(value = "/orderCar", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public boolean orderCar(Double N, Double E) {
        return userService.orderCar(N, E);
    }

    @RequestMapping(value = "/rentalReport", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> generateRentalReport(String rentalId) {
        byte[] contents = userService.generateRentalReport(rentalId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/pdf"));
        String filename = "output.pdf";
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return new ResponseEntity<>(contents, headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/invoice", method = RequestMethod.GET, produces = MediaType.APPLICATION_PDF_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public byte[] generateInvoice(String rentalId) {
        log.debug("Generating invoice.");
        return userService.generateInvoice(rentalId);
    }

    @RequestMapping(value = "/payment", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public boolean makePayment(String rentalId) {
        return userService.makePayment(rentalId);
    }

}
