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
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

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
    public ResponseEntity<Collection<User>> getUserFriends(@PathVariable Long id, @RequestBody User user) {
       Set<Long> friendsIds = userService.getUser(user.getId()).getFriends();
       return new ResponseEntity<>(userService.getFriends(user), HttpStatus.OK);
    }

    @PutMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void addUserFriend(@PathVariable Long id,
                              @PathVariable Long friendId) {
        userService.setFriendship(userService.getUser(id), userService.getUser(friendId));
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeUserFriend(@PathVariable Long id,
                                 @PathVariable Long friendId) {
        userService.removeFromFriends(userService.getUser(id), userService.getUser(friendId));
    }

    @GetMapping
    public ResponseEntity<Collection<User>> getUsers() {
        try {
            log.info("Users list: {}", userService.getUsers().size());
            return new ResponseEntity<>(userService.getUsers(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        User newUser = userService.createUser(user);
        log.info("User {} created", newUser.getLogin());
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user) {
       User userUpdated = userService.updateUser(user);
        log.info("User data updated");
        return new ResponseEntity<>(userUpdated, HttpStatus.OK);
    }
}
