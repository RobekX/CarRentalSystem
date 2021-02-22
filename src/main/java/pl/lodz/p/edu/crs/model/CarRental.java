package pl.lodz.p.edu.crs.model;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.DBRef;
import pl.lodz.p.edu.crs.utils.Views;

import java.util.Date;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CarRental {
    @DBRef
    User renter;
    @JsonView(Views.CarRental.class)
    String rentalNumber;
    @JsonView(Views.CarRental.class)
    String type;
    @JsonView(Views.CarRental.class)
    String status;
    @JsonView(Views.CarRental.class)
    Date startDate;
    @JsonView(Views.CarRental.class)
    Date plannedReturnDate;
    @JsonView(Views.CarRental.class)
    Date finishDate;
    @JsonView(Views.CarRental.class)
    Long payment;
    @JsonView(Views.CarRental.class)
    Boolean isPaid;
}
