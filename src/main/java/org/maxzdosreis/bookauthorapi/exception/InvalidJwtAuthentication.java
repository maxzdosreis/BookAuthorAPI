package org.maxzdosreis.bookauthorapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class InvalidJwtAuthentication extends AuthenticationException {

    public InvalidJwtAuthentication(String message){
        super(message);
    }
}
