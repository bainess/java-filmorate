package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getUsers() {
        return users.values();
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        if (user.getEmail() == null || (user.getEmail().isEmpty() && !user.getEmail().contains("@"))) throw new ValidationException("Incorrect email format");
        if (user.getLogin() == null || (user.getLogin().isBlank() && user.getLogin().isEmpty())) throw new ValidationException("Incorrect login format");
        if (user.getName().isEmpty()) user.setName(user.getLogin());
        if (user.getBirthday().isAfter(Instant.now())) throw new ValidationException("Birthday cannot be in future");
        users.put(user.getId(), user);
        return user;
    }

}
