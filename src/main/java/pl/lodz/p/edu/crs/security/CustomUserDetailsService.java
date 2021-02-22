package pl.lodz.p.edu.crs.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pl.lodz.p.edu.crs.model.User;
import pl.lodz.p.edu.crs.repository.UserRepository;

@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Logging user using email :: {}",username);
        User user = userRepository.findById(username)
                .orElseThrow(()-> new UsernameNotFoundException("User with email :: "+username+" :: not found."));
        return new MongoUserDetails(user.getEmail(),user.getPassword(),new String[]{"ROLE_"+user.getAccountType()});
    }
}
