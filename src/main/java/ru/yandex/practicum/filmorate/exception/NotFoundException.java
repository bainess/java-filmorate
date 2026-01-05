package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String reason) {
        super(reason);
    }
}
