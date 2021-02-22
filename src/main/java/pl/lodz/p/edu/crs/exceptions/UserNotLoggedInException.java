package pl.lodz.p.edu.crs.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UserNotLoggedInException extends RuntimeException {
    public UserNotLoggedInException(String msg){
        super(msg);
    }

}
