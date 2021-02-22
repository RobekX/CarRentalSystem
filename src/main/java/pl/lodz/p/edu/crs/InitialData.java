package pl.lodz.p.edu.crs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import pl.lodz.p.edu.crs.model.*;
import pl.lodz.p.edu.crs.repository.CarRepository;
import pl.lodz.p.edu.crs.repository.UserRepository;
import pl.lodz.p.edu.crs.utils.UserStatus;

import javax.annotation.PostConstruct;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.UUID;

@Service
public class InitialData {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private final static DateFormat sdf= new SimpleDateFormat("YYYY-mm-DD");

    @PostConstruct
    public void createTestEntry() throws ParseException {
        final String rentalNumber = UUID.randomUUID().toString();

        User admin = new User();
        admin.setEmail("admin@admin.pl");
        admin.setFirstName("Admin");
        admin.setLastName("Taki");
        admin.setLogin("Admin");
        admin.setPassword(passwordEncoder.encode("password"));
        admin.setPhone("111111111");
        admin.setStatus(UserStatus.OK.name());
        admin.setAccountType(AccountType.ADMIN.name());
        userRepository.save(admin);

        User user1 = new User();
        user1.setEmail("noname@admin.pl");
        user1.setFirstName("Robert ");
        user1.setLastName("Radczyc");
        user1.setLogin("rradczyc");
        user1.setPassword(passwordEncoder.encode("password"));
        user1.setPhone("1234523431");
        user1.setStatus(UserStatus.OK.name());
        user1.setAccountType(AccountType.USER.name());
        userRepository.save(user1);

    }
}
