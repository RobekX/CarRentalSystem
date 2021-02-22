package pl.lodz.p.edu.crs.model;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import org.springframework.data.annotation.Id;
import pl.lodz.p.edu.crs.utils.Views;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @JsonView(Views.User.class)
    String email;
    @JsonView(Views.User.class)
    String login;
    String password;
    @JsonView(Views.User.class)
    String firstName;
    @JsonView(Views.User.class)
    String lastName;
    @JsonView(Views.User.class)
    String phone;
    String status;
    String accountType;
    List<Payment> payments = new ArrayList<>();
    Long balance;
}
