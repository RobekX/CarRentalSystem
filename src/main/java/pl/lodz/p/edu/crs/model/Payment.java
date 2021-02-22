package pl.lodz.p.edu.crs.model;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    String number;
    Date creationDate;
    Date paymentDate;
    String rentalNumber;
    Long payment;
    Boolean isPaid;
    Boolean isInvoice;
}
