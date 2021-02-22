package pl.lodz.p.edu.crs.dao;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import pl.lodz.p.edu.crs.exceptions.*;
import pl.lodz.p.edu.crs.model.Car;
import pl.lodz.p.edu.crs.model.CarRental;
import pl.lodz.p.edu.crs.model.User;
import pl.lodz.p.edu.crs.repository.UserRepository;
import pl.lodz.p.edu.crs.security.SecurityUtils;
import pl.lodz.p.edu.crs.utils.CarStatus;
import pl.lodz.p.edu.crs.utils.RentalStatus;
import pl.lodz.p.edu.crs.utils.UserStatus;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
public class UserDaoImpl implements UserDao {

    @Autowired
    UserRepository userRepository;
    @Autowired
    CarDao carDao;

    @Override
    public User addUser(User user) {
        if (user != null)
            return userRepository.save(user);
        else
            throw new EmptyParameterException("User parameter is missing");
    }

    @Override
    public Boolean login(String login) {
        log.info("Logged in user :: {} ", login);
        return false;
    }

    @Override
    public Boolean logout() {
        //TODO remove user context
        return false;
    }

    @Override
    public User updateUser(User user) {
        if (user != null && user.getEmail() != null) {
            userRepository.findById(user.getEmail())
                    .orElseThrow(() -> new UserDoesNotExitException("User with login " + user.getEmail() + " does not exist"));
            return userRepository.save(user);
        } else
            throw new EmptyParameterException("User is empty or he does not have id set.");
    }

    @Override
    public User deleteUser(String userId) {
        if (userId != null && !userId.isEmpty()) {
            User userFromRepo = userRepository.findById(userId)
                    .orElseThrow(() -> new UserDoesNotExitException("User with id " + userId + " does not exist"));
            userFromRepo.setStatus(UserStatus.REMOVED.name());
            return userRepository.save(userFromRepo);
        } else
            throw new EmptyParameterException("UserId is empty or missing.");
    }

    @Override
    public User changeUserStatus(String userId, String status) {
        if (userId != null && !userId.isEmpty() && status != null && !status.isEmpty()) {
            User userFromRepo = userRepository.findById(userId)
                    .orElseThrow(() -> new UserDoesNotExitException("User with id " + userId + " does not exist"));
            userFromRepo.setStatus(status);
            return userRepository.save(userFromRepo);
        } else
            throw new EmptyParameterException("UserId and/or status is empty or missing.");
    }

    @Override
    public byte[] generateRentalCost(String userId) {
        //TODO generowanie pdf
        return new byte[0];
    }

    @Override
    public boolean rentACar(String carId, String username, CarRental rental) {
        //TODO Zmienić jak będzie security
        if (rental != null && rental.getRenter() == null) {
            User user = userRepository.findById(username).orElse(null);
            rental.setRenter(user);
        }
        if (carId != null && !carId.isEmpty() && rental != null && rental.getRenter() != null && rental.getRenter().getEmail() != null) {
            User userFromRepo = userRepository.findById(rental.getRenter().getEmail())
                    .orElseThrow(() -> new UserDoesNotExitException("User with login " + rental.getRenter().getEmail() + " does not exist"));
            Car car = carDao.getCar(carId);
            if (car.getStatus().equals(CarStatus.RENTED.name()))
                throw new CarAlreadyRentedException("Car is already rented.");
            log.info("Searching for rentals that are not RENTED or RESERVED :: {}", car.getCarRentals().stream().map(CarRental::getStatus).collect(Collectors.toList()));
            CarRental rent = car.getCarRentals().stream()
                    .filter(elem -> elem.getStatus().equals(CarStatus.RENTED.name())
                            || elem.getStatus().equals(CarStatus.RESERVED.name()))
                    .findFirst().orElse(null);
            if (rent != null)
                throw new CarAlreadyRentedException("Car is already rented.");
            rental.setRenter(userFromRepo);
            rental.setStatus(RentalStatus.RENTED.name());
            rental.setRentalNumber(UUID.randomUUID().toString());
            rental.setIsPaid(false);
            car.getCarRentals().add(rental);
            car.setStatus(CarStatus.RENTED.name());
            carDao.updateCar(car);
            return true;
        } else
            throw new EmptyParameterException("CarId and/or rental is empty or missing.");
    }

    @Override
    public boolean returnCar(String userId, String carId) {
        log.info("Returning car :: {} :: for user :: {}", carId, userId);
        if (userId != null && !userId.isEmpty() && carId != null && !carId.isEmpty()) {
            userRepository.findById(userId)
                    .orElseThrow(() -> new UserDoesNotExitException("User with login " + userId + " does not exist"));
            Car car = carDao.getCar(carId);
            CarRental rntl = car.getCarRentals().stream()
                    .filter(carRental -> carRental.getRenter().getEmail().equals(userId)
                            && carRental.getStatus().equals(CarStatus.RENTED.name()))
                    .findFirst().orElseThrow(() -> new NoRentalForUserException("User was not renting any this car"));
            int rntlIndex = car.getCarRentals().indexOf(rntl);
            rntl.setStatus(RentalStatus.FINISHED.name());
            rntl.setFinishDate(new Date());
            Long minutesRented = Math.abs(TimeUnit.MINUTES.convert((rntl.getFinishDate().getTime() - rntl.getStartDate().getTime()), TimeUnit.MILLISECONDS));
            Long payment = car.getPrice() * minutesRented / 60;
            rntl.setPayment(payment);
            car.getCarRentals().set(rntlIndex, rntl);
            car.setStatus(CarStatus.FREE.name());
            carDao.updateCar(car);
            log.info("Car returned.");
            return true;
        } else
            throw new EmptyParameterException("CarId and/or userId is empty or missing.");
    }

    @Override
    public Car findNearestCar(Double N, Double E) {
        List<Car> freeCars = carDao.getCarsByStatus(CarStatus.FREE.name());
        if (freeCars != null && !freeCars.isEmpty()) {
            Car closest = freeCars.get(0);
            Double distance = Double.MAX_VALUE;
            Double[] from = {N, E};
            for (Car current : freeCars) {
                Double currentCarDistance = countDistance(from, current.getPosition().toArray(new Double[2]));
                if (distance > currentCarDistance) {
                    closest = current;
                    distance = currentCarDistance;
                }
            }
            return closest;
        } else
            return null;
    }

    @Override
    public List<Car> getUserRentedCars(String email, String status) {
        return carDao.getUserRentedCars(email, status);
    }


    @Override
    public boolean orderCar(Double N, Double E) {
        //TODO obsługa tego. Wysłanie maila do obsługi, żeby ktoś przywiózł samochód?
        return false;
    }

    @Override
    public byte[] generateRentalReport(String rentalId) {
        User user = userRepository.findById(SecurityUtils.getUserName()).orElseThrow(() -> new UserDoesNotExitException("Użytkownik o podanym identyfikatorze nie istnieje"));
        Car car = carDao.getCarRentalByRentalId(SecurityUtils.getUserName());
        if (car == null)
            throw new RentalDoesNotExist("Wypożyczenie o takim numerze nie istnieje");
        CarRental rental = carDao.getCarRentalByRentalId(SecurityUtils.getUserName()).getCarRentals().stream().filter(elem -> elem.getRentalNumber().equals(rentalId)).findFirst().orElseThrow(() -> new RentalDoesNotExist("Wypożyczenie o takim numerze nie istnieje"));
        if (!rental.getRenter().equals(user))
            throw new AccessDeniedException("Użytkownik nie ma dostępu do tego wypożyczenia");
        try {
            Document document = new Document();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);
            document.open();
            document.setPageSize(PageSize.A4);
            Paragraph paragraph;
            paragraph = new Paragraph(String.format("Raport rental"));
            paragraph.setAlignment(Element.ALIGN_RIGHT);
            paragraph.add(Chunk.NEWLINE);
            document.add(paragraph);

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date date = new Date();
            Paragraph paragraphDate = new Paragraph("Date generated: " + sdf.format(date));
            paragraphDate.setAlignment(Element.ALIGN_RIGHT);
            paragraphDate.add(Chunk.NEWLINE);
            paragraphDate.add(Chunk.NEWLINE);
            document.add(paragraphDate);

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            PdfPCell seller = new PdfPCell();
            seller.setBorder(PdfPCell.NO_BORDER);
            seller.addElement(new Paragraph("Rental from: ", new Font(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.WINANSI, BaseFont.EMBEDDED), 12)));
            seller.addElement(new Paragraph("CarRentalCompany "));
            seller.addElement(new Paragraph("Politechniki 1 "));
            seller.addElement(new Paragraph(String.format("%s %s %s", "Polska", "93-590", "Lodz")));
            seller.addElement(new Paragraph("NIP: 729-247-00-00"));
            table.addCell(seller);

            PdfPCell buyer = new PdfPCell();
            buyer.setBorder(PdfPCell.NO_BORDER);
            buyer.setVerticalAlignment(Element.ALIGN_RIGHT);
            buyer.addElement(new Paragraph("Rental by: ", new Font(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.WINANSI, BaseFont.EMBEDDED), 12)));
            buyer.addElement(new Paragraph("Jan Kowalski "));
            buyer.addElement(new Paragraph("Kajakowa 10 "));
            buyer.addElement(new Paragraph(String.format("%s %s %s", "Polska", "90-500", "Lodz")));
            buyer.addElement(new Paragraph("NIP: 729-247-00-00"));
            table.addCell(buyer);
            document.add(table);
            Paragraph newline = new Paragraph();
            newline.add(Chunk.NEWLINE);
            document.add(newline);


            table = new PdfPTable(2);
            table.setWidthPercentage(100);
            PdfPCell cell = new PdfPCell();
            cell.setUseAscender(true);
            cell.setUseDescender(true);
            cell.addElement(new Paragraph("Name: "));
            cell.addElement(new Paragraph("Model: "));
            cell.addElement(new Paragraph("Engine: "));
            cell.addElement(new Paragraph("Production Year: "));
            cell.addElement(new Paragraph("Course Before: "));
            cell.addElement(new Paragraph("Course After: "));
            cell.addElement(new Paragraph("Rental Start Date: "));
            cell.addElement(new Paragraph("Rental Finish Date: "));
            cell.addElement(new Paragraph("Information: "));
            table.addCell(cell);

            PdfPCell cellInform = new PdfPCell();
            cellInform.setUseAscender(true);
            cellInform.setUseDescender(true);
            cellInform.setVerticalAlignment(Element.ALIGN_RIGHT);
            cellInform.addElement(new Paragraph("Skoda"));
            cellInform.addElement(new Paragraph("SuperB "));
            cellInform.addElement(new Paragraph("1.8 TFSI"));
            cellInform.addElement(new Paragraph("2016"));
            cellInform.addElement(new Paragraph("32543 Km"));
            cellInform.addElement(new Paragraph("33213 Km"));
            cellInform.addElement(new Paragraph("25-04-2020"));
            cellInform.addElement(new Paragraph("29-04-2020"));
            cellInform.addElement(new Paragraph("Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."));
            table.addCell(cellInform);

            document.add(table);

            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    @Override
    public byte[] generateInvoice(String rentalId) {
        log.debug("Getting invoice for rental :: {}", rentalId);
        User user = userRepository.findById(SecurityUtils.getUserName()).orElseThrow(() -> new UserDoesNotExitException("Użytkownik o podanym identyfikatorze nie istnieje"));
        log.debug("Found user :: {}", user);
        Car car = carDao.getCarRentalByRentalId(rentalId);
        log.debug("Found car :: {}", car);
        if (car == null)
            throw new RentalDoesNotExist("Wypożyczenie o takim numerze nie istnieje");
        CarRental rental = car.getCarRentals().stream().filter(elem -> elem.getRentalNumber().equals(rentalId)).findFirst().orElseThrow(() -> new RentalDoesNotExist("Wypożyczenie o takim numerze nie istnieje"));
        log.debug("Found rental:: {}", rental);
        if (!rental.getRenter().equals(user)) {
            log.debug("User does not have permission to this rental. \nRental user :: {}\nDatabase user :: {}", rental.getRenter(), user);
            throw new AccessDeniedException("Użytkownik nie ma dostępu do tego wypożyczenia");
        }
        Double minutesRented = Math.abs(TimeUnit.MINUTES.convert((rental.getFinishDate().getTime() - rental.getStartDate().getTime()), TimeUnit.MILLISECONDS)) / 60.0;
        try {
            Document document = new Document();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);
            document.open();
            document.setPageSize(PageSize.A4);
            Paragraph paragraph;
            paragraph = new Paragraph("invoice " + rentalId + "/2021");
            paragraph.setAlignment(Element.ALIGN_RIGHT);
            document.add(paragraph);
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date date = new Date();
            Paragraph paragraphDate = new Paragraph(sdf.format(date));
            paragraphDate.setAlignment(Element.ALIGN_RIGHT);
            document.add(paragraphDate);

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            PdfPCell seller = new PdfPCell();
            seller.setBorder(PdfPCell.NO_BORDER);
            seller.addElement(new Paragraph("From: ", new Font(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.WINANSI, BaseFont.EMBEDDED), 12)));
            seller.addElement(new Paragraph("CarRentalCompany "));
            seller.addElement(new Paragraph("Politechniki 1 "));
            seller.addElement(new Paragraph(String.format("%s %s %s", "Polska", "93-590", "Lodz")));
            seller.addElement(new Paragraph("NIP: 729-247-00-00"));
            table.addCell(seller);

            PdfPCell buyer = new PdfPCell();
            buyer.setBorder(PdfPCell.NO_BORDER);
            buyer.setVerticalAlignment(Element.ALIGN_RIGHT);
            buyer.addElement(new Paragraph("To: ", new Font(BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.WINANSI, BaseFont.EMBEDDED), 12)));
            buyer.addElement(new Paragraph(user.getFirstName() + " " + user.getLastName()));
            buyer.addElement(new Paragraph("Kajakowa 10 "));
            buyer.addElement(new Paragraph(String.format("%s %s %s", "Polska", "90-500", "Lodz")));
//            buyer.addElement(new Paragraph("NIP: 729-247-00-00"));
            table.addCell(buyer);

            document.add(table);

            table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);
            table.setSpacingAfter(10);
            table.setWidths(new int[]{7, 2, 1, 2, 2, 2});
            table.addCell(getCell("Item:", Element.ALIGN_LEFT));
            table.addCell(getCell("Price:", Element.ALIGN_LEFT));
            table.addCell(getCell("Qty:", Element.ALIGN_LEFT));
            table.addCell(getCell("Subtotal:", Element.ALIGN_LEFT));
            table.addCell(getCell("VAT:", Element.ALIGN_LEFT));
            table.addCell(getCell("Total:", Element.ALIGN_LEFT));

            DecimalFormat format = new DecimalFormat("0.00");

            table.addCell(getCell(car.getName() + " " + car.getDescription().getModel() + " " + car.getDescription().getEngine() , Element.ALIGN_LEFT));
            table.addCell(getCell(format.format(car.getPrice() / 100.0), Element.ALIGN_RIGHT));
            table.addCell(getCell(format.format(minutesRented), Element.ALIGN_RIGHT));
            table.addCell(getCell(format.format(rental.getPayment() / 100.0), Element.ALIGN_RIGHT));
            table.addCell(getCell("23.00", Element.ALIGN_RIGHT));
            table.addCell(getCell(String.valueOf(rental.getPayment() * 1.23 / 100.0), Element.ALIGN_RIGHT));
            document.add(table);


            table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new int[]{1, 1, 3, 3, 3, 1});
            table.addCell(getCell("TAX", Element.ALIGN_LEFT));
            table.addCell(getCell("%", Element.ALIGN_RIGHT));
            table.addCell(getCell("Base amount:", Element.ALIGN_LEFT));
            table.addCell(getCell("Tax amount:", Element.ALIGN_LEFT));
            table.addCell(getCell("Total:", Element.ALIGN_LEFT));
            table.addCell(getCell("Curr.:", Element.ALIGN_LEFT));

            table.addCell(getCell("VAT", Element.ALIGN_RIGHT));
            table.addCell(getCell("23.00", Element.ALIGN_RIGHT));
            table.addCell(getCell(format.format(rental.getPayment() / 100.0), Element.ALIGN_RIGHT));
            table.addCell(getCell(String.valueOf(rental.getPayment() * 0.23 / 100.0), Element.ALIGN_RIGHT));
            table.addCell(getCell(String.valueOf(rental.getPayment() * 1.23 / 100.0), Element.ALIGN_RIGHT));
            table.addCell(getCell("PLN", Element.ALIGN_LEFT));

            document.add(table);

            Paragraph para = new Paragraph(String.format("Please wire the amount due to our bank account using the following reference: %s", paragraph));
            para.add(Chunk.NEWLINE);
            para.add("mBank: 67 2490 0005 0000 4000 3469 8257");

            document.add(para);

            document.close();

            log.debug("Generated invoice. Returning.");

            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    @Override
    public boolean makePayment(String rentalId) {
        //TODO płatność
        return false;
    }

    @Override
    public List<CarRental> getUserRentals(String username, String status) {
        return carDao.getUserRentals(username, status);
    }

    @Override
    public User getUserDetails(String userName) {
        return userRepository.findById(userName).orElseThrow(() -> new UserDoesNotExitException("User with id " + userName + " does not exist"));
    }

    private Double countDistance(Double[] from, Double[] to) {
        return Math.sqrt(Math.pow(from[0] - to[0], 2) + Math.pow(from[1] - to[1], 2));
    }

    private PdfPCell getCell(String value, int alignment) {
        PdfPCell cell = new PdfPCell();
        cell.setUseAscender(true);
        cell.setUseDescender(true);
        Paragraph p = new Paragraph(value);
        p.setAlignment(alignment);
        cell.addElement(p);
        return cell;
    }
}
