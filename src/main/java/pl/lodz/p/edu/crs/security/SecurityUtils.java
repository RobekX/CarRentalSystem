package pl.lodz.p.edu.crs.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public class SecurityUtils {
    public static String getUserName() {
        log.debug("Authentication :: {}", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        if (SecurityContextHolder.getContext() != null && !SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals("anonymousUser"))
            return ((MongoUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        else
            return "Anonymous";
    }
}
