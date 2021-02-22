package pl.lodz.p.edu.crs.model;

import lombok.*;

import java.util.Date;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Repair {
    Date startDate;
    Date finishDate;
    Long cost;
    String details;
}
