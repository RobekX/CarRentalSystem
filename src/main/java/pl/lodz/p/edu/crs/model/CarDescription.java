package pl.lodz.p.edu.crs.model;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import pl.lodz.p.edu.crs.utils.Views;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CarDescription {
    @JsonView(Views.Car.class)
    String model;
    @JsonView(Views.Car.class)
    String engine;
    @JsonView(Views.Car.class)
    String productionYear;
    @JsonView(Views.Car.class)
    Integer noOfSeats;
    @JsonView(Views.Car.class)
    Long reach;
    @JsonView(Views.Car.class)
    String info;
    @JsonView(Views.Car.class)
    String imgId;
}
