package pl.lodz.p.edu.crs.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pl.lodz.p.edu.crs.model.*;
import pl.lodz.p.edu.crs.repository.CarRepository;
import pl.lodz.p.edu.crs.repository.UserRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class DataGenerator {

    private final static List<String> producers = Arrays.asList("BMW", "Seat", "Volkswagen", "Mercedes", "Toyota", "Fiat");
    private final static List<String> types = Arrays.asList("TT21", "WKD1", "PQPP2", "LDKS", "126-p");
    private final static List<String> engines = Arrays.asList("12TDI", "KKR435", "OMEGA123", "ROT3", "WKZ12");
    private final static List<String> descriptions = Arrays.asList("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum ac luctus lacus. Nam eu sem eu lorem pretium tempor eget a nunc. Nam gravida egestas turpis, ac faucibus ex. Mauris fringilla accumsan tellus quis tristique.",
            "Pellentesque ac felis vulputate, faucibus erat tempus, maximus lectus. Sed mattis sollicitudin enim, quis tristique ipsum tristique ac. Vestibulum pharetra euismod quam in mollis. Mauris imperdiet rutrum risus, vel commodo arcu euismod non.",
            "Aliquam ac nulla nulla. Nam vestibulum at ipsum sed lobortis. Etiam placerat dolor eget sem pellentesque aliquam at et libero. Interdum et malesuada fames ac ante ipsum primis in faucibus. Curabitur ullamcorper consectetur arcu in tristique.",
            "Quisque egestas ex tristique elit malesuada, non malesuada enim eleifend. Interdum et malesuada fames ac ante ipsum primis in faucibus.");
    private final static List<String> userLastNames = Arrays.asList("Piotrkowski", "Mazowiecki", "Legion", "Zgierski", "Sopocki", "Koniecki", "Twardowski", "Mielczarek","Migon", "Morawa", "Luks");
    private final static List<String> userFirstNames = Arrays.asList("Piotr", "Andrzej", "Witold", "Jan", "Roman", "Marek", "Krzysztof", "Darek", "Misza", "Marcin", "Kacper", "Baltazar", "Melchior");
    private final static List<String> repairDescriptions = Arrays.asList("Przebita opona", "Problem z silnikiem", "Wybita szyba", "Wymiana żarówek", "Usterka elektryczna");
    private final static List<String> imageSources = Arrays.asList("1.jpeg", "2.jpg", "3.jpeg", "4.jpeg");
    private final static Random random = new Random();
    private final static ThreadLocalRandom threadRandom = ThreadLocalRandom.current();

    private final static Date startDate;
    private final static Date endDate;

    static {
        Date start;
        Date end;
        try {
            start = new SimpleDateFormat("YYYY-mm-DD").parse("2020-01-01");
            end = new SimpleDateFormat("YYYY-mm-DD").parse("2020-12-31");
        } catch (ParseException e) {
            e.printStackTrace();
            start = new Date();
            end = new Date();
        }
        startDate = start;
        endDate = end;
    }

    private final static Calendar cal = new GregorianCalendar();


    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CarRepository carRepository;

    @RequestMapping(value = "/generator/generateData", method = RequestMethod.GET)
    public void generateData(int amount) {
        List<User> users = generateUsers(amount / 4);
        List<Car> cars = new ArrayList<>(amount);
        for (int a = 0; a <= amount; a++) {
            cars.add(getCar(users));
        }
        users.stream().forEach(user -> {
            long toBePaid = 0;
            for (long amm : user.getPayments().stream().map(pay -> pay.getIsPaid() ? 0 : pay.getPayment()).collect(Collectors.toList())) {
                toBePaid -= amm;
            }
            user.setBalance(toBePaid);
        });
        log.debug("Saving generated users :: {}", users);
        userRepository.saveAll(users);
        log.debug("Saving generated cars :: {}", cars);
        carRepository.saveAll(cars);
    }

    private Car getCar(List<User> users) {
        Car car = new Car();
        car.setId(UUID.randomUUID().toString());
        for (int i = 0; i < Math.abs(random.nextInt()) % 7; i++) {
            car.getCarRentals().add(getCarRental(users.get(Math.abs(random.nextInt()) % users.size()), false));
        }
        if (random.nextBoolean()) {
            car.getCarRentals().add(getCarRental(users.get(Math.abs(random.nextInt()) % users.size()), true));
            car.setStatus(CarStatus.RENTED.name());
        } else {
            car.setStatus(CarStatus.FREE.name());
        }
        car.setDescription(getCarDescription());
        car.setName(car.getDescription().getModel() + ' ' + car.getDescription().getEngine());
        car.setPosition(Arrays.asList(threadRandom.nextDouble(49, 54), threadRandom.nextDouble(14, 24)));
        car.setPrice(Math.abs(random.nextLong()) % 1000);
        for (int i = 0; i < Math.abs(random.nextInt()) % 3; i++) {
            car.getRepairs().add(getRepair());
        }
        return car;
    }

    private Repair getRepair() {
        Repair repair = new Repair();
        repair.setCost(Math.abs(random.nextLong()) % 2000);
        repair.setDetails(repairDescriptions.get(Math.abs(random.nextInt()) % repairDescriptions.size()));
        cal.setTime(new Date());
        repair.setStartDate(cal.getTime());
        cal.add(Calendar.DATE, Math.abs(random.nextInt()) % 14);
        repair.setFinishDate(cal.getTime());
        return repair;
    }

    private CarDescription getCarDescription() {
        CarDescription description = new CarDescription();
        description.setProductionYear("201" + Math.abs(random.nextInt()) % 8);
        description.setNoOfSeats(Math.abs(random.nextInt()) % 3 + 2);
        description.setModel(producers.get(Math.abs(random.nextInt()) % 6) + " " + types.get(Math.abs(random.nextInt()) % 5));
        description.setInfo(descriptions.get(Math.abs(random.nextInt()) % 4));
        description.setEngine(engines.get(Math.abs(random.nextInt()) % 5));
        description.setReach(Math.abs(random.nextLong()) % 2000);
        description.setImgId(imageSources.get(Math.abs(random.nextInt()) % imageSources.size()));
        return description;
    }

    private CarRental getCarRental(User user, Boolean isRented) {
        CarRental carRental = new CarRental();
        carRental.setRenter(user);
        log.debug("Randomizing date within dates :: {} :: {}", startDate, endDate);
        log.debug("In long format :: {} :: {}", startDate.getTime(), endDate.getTime());
        carRental.setStartDate(new Date(Math.abs(threadRandom.nextLong(startDate.getTime(), endDate.getTime()))));
        cal.setTime(carRental.getStartDate());
        cal.add(Calendar.DATE, 1);
        carRental.setPlannedReturnDate(cal.getTime());
        carRental.setPayment(Math.abs(random.nextLong()) % 2000);
        carRental.setIsPaid(random.nextBoolean());
        if (!isRented) {
            cal.add(Calendar.DATE, Math.abs(random.nextInt()) % 2);
            carRental.setFinishDate(cal.getTime());
            carRental.setStatus(RentalStatus.FINISHED.name());
            cal.add(Calendar.DATE, 30);
            user.getPayments().add(getPayment(carRental.getPayment(), carRental.getRentalNumber(), cal.getTime(), carRental.getFinishDate()));
        } else {
            carRental.setStatus(RentalStatus.RENTED.name());
        }
        carRental.setRentalNumber(UUID.randomUUID().toString());
        if (random.nextBoolean())
            carRental.setType(RentalType.DISPATCH.name());
        else
            carRental.setType(RentalType.PERSONAL.name());

        return carRental;
    }

    private Payment getPayment(Long toBePaid, String rentalNumber, Date paymentDate, Date creationDate) {
        Payment payment = new Payment();
        payment.setPayment(toBePaid);
        payment.setRentalNumber(rentalNumber);
        payment.setPaymentDate(paymentDate);
        payment.setNumber(UUID.randomUUID().toString());
        payment.setIsPaid(random.nextBoolean());
        payment.setIsInvoice(random.nextBoolean());
        payment.setCreationDate(creationDate);
        return payment;
    }

    private List<User> generateUsers(int amount) {
        log.debug("Generating {} users", amount);
        List<User> usersList = new ArrayList<>(amount);
        for (int i = 0; i < amount; i++) {
            User user = new User();
            user.setAccountType(AccountType.USER.name());
            user.setStatus(UserStatus.OK.name());
            user.setFirstName(userFirstNames.get(Math.abs(random.nextInt()) % userFirstNames.size()));
            user.setLastName(userLastNames.get(Math.abs(random.nextInt()) % userLastNames.size()));
            user.setEmail(user.getFirstName().toLowerCase() + '@' + user.getLastName().toLowerCase() + ".com");
            user.setLogin(user.getFirstName().toLowerCase() + '.' + user.getLastName().toLowerCase() + i);
            user.setPassword(encoder.encode("haslo"));
            user.setPayments(new ArrayList<>());
            user.setPhone("123123123");
            usersList.add(user);
        }
        return usersList;
    }

}
