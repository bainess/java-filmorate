package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}/friends/common/{friendId}")
    public ResponseEntity<List<User>> getCommonFriends(@PathVariable Long userId,
                                       @PathVariable Long friendId) {
            return new ResponseEntity<>(userService.getCommonFriends(userId, friendId), HttpStatus.OK);
    }

    @GetMapping("/{id}/friends")
    public ResponseEntity<Collection<User>> getUserFriends(@PathVariable Long id) {
        if (userService.getUser(id) == null) {
            throw new NotFoundException("User was not found");
        }
       return new ResponseEntity<>(userService.getFriends(id), HttpStatus.OK);
    }

    @PutMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void addUserFriend(@PathVariable Long id,
                              @PathVariable Long friendId) {
        if (userService.getUser(id) == null) {
            throw new NotFoundException("User" + id + " was not found");
        }
        if (userService.getUser(friendId) == null) {
            throw new NotFoundException("User" + friendId + " was not found");
        }
        userService.setFriendship(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeUserFriend(@PathVariable Long id,
                                 @PathVariable Long friendId) {
        if (userService.getUser(id) == null) {
            throw new NotFoundException("User" + id + " was not found");
        }
        if (userService.getUser(friendId) == null) {
            throw new NotFoundException("User" + friendId + " was not found");
        }
        userService.removeFromFriends(id, friendId);
    }

    @GetMapping
    public ResponseEntity<Collection<User>> getUsers() {
        log.info("Users list: {}", userService.getUsers().size());
            return new ResponseEntity<>(userService.getUsers(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        User newUser = userService.createUser(user);
        log.info("User {} created", newUser.getLogin());
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Optional<User>> updateUser(@Valid @RequestBody User user) {
        if (userService.getUser(user.getId()) == null) {
            throw new NotFoundException("User not found");
        }
       Optional<User> userUpdated = userService.updateUser(user);
        log.info("User data updated");
        return new ResponseEntity<>(userUpdated, HttpStatus.OK);
    }
}
