package pl.lodz.p.edu.crs.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.lodz.p.edu.crs.model.User;

public interface UserRepository extends MongoRepository<User, String> {

}
