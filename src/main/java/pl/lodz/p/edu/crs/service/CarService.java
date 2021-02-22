package pl.lodz.p.edu.crs.service;

import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import pl.lodz.p.edu.crs.dao.CarDao;
import pl.lodz.p.edu.crs.model.Car;
import pl.lodz.p.edu.crs.model.Repair;

import java.io.*;
import java.util.List;

@Slf4j
@Service
public class CarService {

    @Autowired
    private CarDao carDao;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private MongoDbFactory mongoDbFactory;

    public List<Car> getAllCars() {
        return carDao.getAllCars();
    }

    public List<Car> getCarsByStatus(String status) {
        return carDao.getCarsByStatus(status);
    }

    public Car addCar(Car car) {
        return carDao.addCar(car);
    }

    public Car editCar(Car car) {
        return carDao.updateCar(car);
    }

    public Boolean deleteCar(String carId) {
        return carDao.deleteCar(carId);
    }

    public byte[] generateReportForCar(String carId) {
        return carDao.generateReportForCar(carId);
    }

    public Car changeCarStatus(String carID, String status) {
        return carDao.changeCarStatus(carID, status);
    }

    public List<Repair> getCarRepairs(String carID) {
        return carDao.getCarRepairs(carID);
    }

    public Car getCarById(String id) {
        return carDao.getCar(id);
    }

    public Long availableWhen(String carId) {
        return carDao.availableWhen(carId);
    }

    public String uploadImage(byte[] image, String name, String contentType) {
        log.info("Saving file :: {} :: content-type :: {}", name, contentType);
        InputStream inputStream = new ByteArrayInputStream(image);
        String id = gridFsTemplate.store(inputStream, name, contentType).toString();
        log.info("Got id :: {}", id);
        return id;
    }

    public byte[] getImage(String imageId) {
        log.info("Getting file for id :: {}", imageId);
        GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(imageId)));
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        GridFSBuckets.create(mongoDbFactory.getDb()).downloadToStream(file.getObjectId(), os);
        return os.toByteArray();

    }
}
