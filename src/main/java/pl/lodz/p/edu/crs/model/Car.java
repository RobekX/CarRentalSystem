package pl.lodz.p.edu.crs.model;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import org.springframework.data.annotation.Id;
import pl.lodz.p.edu.crs.utils.Views;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Car {
    @JsonView(Views.Car.class)
    @Id
    String id;
    @JsonView(Views.Car.class)
    String name;
    @JsonView(Views.Car.class)
    String status;
    @JsonView(Views.Car.class)
    CarDescription description;
    @JsonView(Views.Car.class)
    Long price;
    @JsonView(Views.CarRental.class)
    List<CarRental> carRentals = new ArrayList<>();
    List<Repair> repairs = new ArrayList<>();
    List<Double> position = new ArrayList<>();
}
