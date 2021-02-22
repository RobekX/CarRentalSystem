package pl.lodz.p.edu.crs.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pl.lodz.p.edu.crs.security.SecurityUtils;

@Slf4j
@RestController
@RequestMapping("/security")
public class SecurityController {

    @RequestMapping(value = "/isAdmin", method = RequestMethod.GET)
    public boolean isAdmin() {
        log.info("User data :: {}", SecurityUtils.getUserName());
        return (SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().filter(auth ->  auth.getAuthority()
                        .equals("ROLE_ADMIN")).findFirst().orElse(null)) != null;
    }
}
