package com.game;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PlayerBadRequestException extends RuntimeException{
    public PlayerBadRequestException(String message) {
        super(message);
    }
}
