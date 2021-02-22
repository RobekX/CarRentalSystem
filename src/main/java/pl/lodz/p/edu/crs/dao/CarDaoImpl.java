package pl.lodz.p.edu.crs.dao;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.lodz.p.edu.crs.exceptions.CarDoesNotExistException;
import pl.lodz.p.edu.crs.exceptions.EmptyParameterException;
import pl.lodz.p.edu.crs.model.Car;
import pl.lodz.p.edu.crs.model.CarRental;
import pl.lodz.p.edu.crs.model.Repair;
import pl.lodz.p.edu.crs.repository.CarRepository;
import pl.lodz.p.edu.crs.utils.CarStatus;
import pl.lodz.p.edu.crs.utils.RentalStatus;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j

@Component
public class CarDaoImpl implements CarDao {

    @Autowired
    CarRepository carRepository;

    @Override
    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    @Override
    public List<Car> getCarsByStatus(String status) {
        if (status != null && !status.isEmpty()) {
            return carRepository.findByStatus(status);
        } else
            throw new EmptyParameterException("Status is empty or null");
    }

    @Override
    public Car addCar(Car car) {
        if (car != null) {
            return carRepository.save(car);
        } else
            throw new EmptyParameterException("Car parameter is missing");
    }

    @Override
    public Car updateCar(Car car) {
        if (car != null && car.getId() != null) {
            Car carFromRepo = carRepository.findById(car.getId()).orElseThrow(() -> new CarDoesNotExistException("Car with id " + car.getId() + " does not exist"));
            car.getDescription().setImgId(carFromRepo.getDescription().getImgId());
            return carRepository.save(car);
        } else
            throw new EmptyParameterException("Car parameter is missing or it has no id.");
    }

    @Override
    public Boolean deleteCar(String carId) {
        if (carId != null && !carId.isEmpty()) {
            Car repoCar = carRepository.findById(carId).orElseThrow(() -> new CarDoesNotExistException("Car with id " + carId + " does not exist"));
            repoCar.setStatus(CarStatus.REMOVED.name());
            carRepository.save(repoCar);
            return true;
        } else
            throw new EmptyParameterException("CarId parameter is missing or empty");
    }

    @Override
    public byte[] generateReportForCar(String carId) {
        try {
            Document document = new Document();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);
            document.open();
            document.setPageSize(PageSize.A4);
            Paragraph paragraph;
            paragraph = new Paragraph(String.format("Raport samochodu: Skoda SuperB"));
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
            PdfPCell cell = new PdfPCell();
            cell.setUseAscender(true);
            cell.setUseDescender(true);
            cell.addElement(new Paragraph("Name: "));
            cell.addElement(new Paragraph("Model: "));
            cell.addElement(new Paragraph("Engine: "));
            cell.addElement(new Paragraph("Production Year: "));
            cell.addElement(new Paragraph("Course: "));
            cell.addElement(new Paragraph("Status: "));
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
            cellInform.addElement(new Paragraph("Free"));
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
    public Car changeCarStatus(String carId, String status) {
        if (carId != null && !carId.isEmpty()) {
            Car repoCar = carRepository.findById(carId).orElseThrow(() -> new CarDoesNotExistException("Car with id " + carId + " does not exist"));
            repoCar.setStatus(status);
            return carRepository.save(repoCar);
        } else
            throw new EmptyParameterException("CarId parameter is missing or empty");
    }

    @Override
    public List<Repair> getCarRepairs(String carId) {
        if (carId != null && !carId.isEmpty()) {
            Car repoCar = carRepository.findById(carId).orElseThrow(() -> new CarDoesNotExistException("Car with id " + carId + " does not exist"));
            return repoCar.getRepairs();
        } else
            throw new EmptyParameterException("Car parameter is missing");
    }

    @Override
    public Car getCar(String carId) {
        if (carId != null && !carId.isEmpty()) {
            return carRepository.findById(carId).orElseThrow(() -> new CarDoesNotExistException("Car with id " + carId + " does not exist"));
        } else
            throw new EmptyParameterException("CarId parameter is missing or empty");
    }

    @Override
    public Boolean reserveCar(String carId) {
        if (carId != null && !carId.isEmpty()) {
            Car repoCar = carRepository.findById(carId).orElseThrow(() -> new CarDoesNotExistException("Car with id " + carId + " does not exist"));
            repoCar.setStatus(CarStatus.RESERVED.name());
            carRepository.save(repoCar);
            return true;
        } else
            throw new EmptyParameterException("CarId parameter is missing or empty");
    }

    @Override
    public Boolean freeCar(String carId) {
        if (carId != null && !carId.isEmpty()) {
            Car repoCar = carRepository.findById(carId).orElseThrow(() -> new CarDoesNotExistException("Car with id " + carId + " does not exist"));
            repoCar.setStatus(CarStatus.FREE.name());
            carRepository.save(repoCar);
            return true;
        } else
            throw new EmptyParameterException("CarId parameter is missing or empty");
    }

    @Override
    public List<Car> getUserRentedCars(String email, String status) {
        return carRepository.findByCarRentalsRenterEmailAndCarRentalsStatus(email, status);
    }

    @Override
    public Long availableWhen(String carId) {
        if (carId != null && !carId.isEmpty()) {
            Car car = carRepository.findById(carId).orElseThrow(() -> new CarDoesNotExistException("Car with id " + carId + " does not exist"));
            CarRental rental = car.getCarRentals().stream().filter(elem -> elem.getStatus().equals(RentalStatus.RENTED.name())).findFirst().orElse(null);
            if (rental != null) {
                return rental.getPlannedReturnDate().getTime();
            } else
                return null;
        } else
            throw new EmptyParameterException("CarId parameter is missing or empty");
    }

    @Override
    public List<CarRental> getUserRentals(String username, String status) {
        List<CarRental> rentalList = carRepository.findCarRentalsByCarRentalsRenterEmailAndCarRentalsStatus(username, status).stream()
                .flatMap(rentals -> rentals.getCarRentals().stream()).filter(rental -> rental.getRenter().getEmail().equals(username)).collect(Collectors.toList());
        log.debug("Filtered rentals :: {}", rentalList);
        return rentalList;
    }

    @Override
    public Car getCarRentalByRentalId(String rentalId) {
        return carRepository.findByCarRentalsRentalNumber(rentalId);
    }
}
