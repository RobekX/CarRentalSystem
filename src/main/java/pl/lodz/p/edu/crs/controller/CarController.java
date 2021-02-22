package pl.lodz.p.edu.crs.controller;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.lodz.p.edu.crs.model.Car;
import pl.lodz.p.edu.crs.model.Repair;
import pl.lodz.p.edu.crs.service.CarService;
import pl.lodz.p.edu.crs.utils.Views;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/renter/car")
public class CarController {

    @Autowired
    CarService carService;

    @JsonView(Views.Car.class)
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public List<Car> getAllCars() {
        log.info("Getting all the cars.");
        return carService.getAllCars();
    }

    @JsonView(Views.Car.class)
    @RequestMapping(value = "/one", method = RequestMethod.GET)
    public Car getCarById(String id) {
        return carService.getCarById(id);
    }

    @RequestMapping(value = "/getCarsByStatus", method = RequestMethod.GET)
    public List<Car> getCarsByStatus(String status) {
        return carService.getCarsByStatus(status);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Car addCar(@RequestBody Car car) {
        return carService.addCar(car);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Car editCar(@RequestBody Car car) {
        return carService.editCar(car);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public Boolean deleteCar(String carId) {
        return carService.deleteCar(carId);
    }

    @RequestMapping(value = "/report", method = RequestMethod.GET)
    public ResponseEntity<byte[]> generateReportForCar(String carId) {
        byte[] contents = carService.generateReportForCar(carId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/pdf"));
        String filename = "output.pdf";
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return new ResponseEntity<>(contents, headers, HttpStatus.OK);

    }

    @RequestMapping(value = "/changeStatus", method = RequestMethod.GET)
    public Car changeCarStatus(String carID, String status) {
        return carService.changeCarStatus(carID, status);
    }

    @RequestMapping(value = "/repairs", method = RequestMethod.GET)
    public List<Repair> getCarRepairs(String carId) {
        return carService.getCarRepairs(carId);
    }

    @RequestMapping(value = "/availableWhen", method = RequestMethod.GET)
    public Long availableWhen(String carId) {
        return carService.availableWhen(carId);
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        return carService.uploadImage(file.getBytes(), file.getName(), file.getContentType());
    }

    @RequestMapping(value = "/image", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getCarImage(String imageId){
        return carService.getImage(imageId);
    }

}
