package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.UserFriend;
import ru.yandex.practicum.filmorate.service.EventService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final EventService eventService;

    @Autowired
    public UserController(UserService userService, EventService eventService) {

        this.userService = userService;
        this.eventService = eventService;
    }

    @GetMapping("/{userId}/feed")
    public ResponseEntity<Collection<Event>> getUserFeed(@RequestParam ("userId") Long userId) {
        return new ResponseEntity<>(eventService.getEvents(userId), HttpStatus.OK );
    }

    @GetMapping("/{userId}/friends/common/{friendId}")
    public ResponseEntity<List<UserDto>> getCommonFriends(@PathVariable Long userId,
                                                          @PathVariable Long friendId) {
        return new ResponseEntity<>(userService.getCommonFriends(userId, friendId), HttpStatus.OK);
    }

    @GetMapping("/{id}/friends")
    public ResponseEntity<Collection<UserFriend>> getUserFriends(@PathVariable Long id) {
        Collection<UserFriend> friends = userService.getFriends(id);
        return new ResponseEntity<>(friends, HttpStatus.OK);
    }

    @PutMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void addUserFriend(@PathVariable Long id,
                              @PathVariable Long friendId) {
        userService.setFriendship(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeUserFriend(@PathVariable Long id,
                                 @PathVariable Long friendId) {
        userService.removeFromFriends(id, friendId);
    }

    @GetMapping
    public ResponseEntity<Collection<UserDto>> getUsers() {
        Collection<UserDto> users = userService.getUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable("id") Long id) {
        UserDto user = userService.getUserById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody NewUserRequest request) {
        UserDto newUser = userService.createUser(request);
        log.info("User {} created", newUser.getLogin());
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<UserDto> updateUser(@Valid @RequestBody UpdateUserRequest request) {
        long id = request.getId();
        log.info("User data updated {}", id);
        UserDto userUpdated = userService.updateUser(request.getId(), request);
        log.info("User data updated");
        return new ResponseEntity<>(userUpdated, HttpStatus.OK);
    }

    @GetMapping("/{id}/recommendations")
    public ResponseEntity<Collection<FilmDto>> getRecommendations(@PathVariable Long id) {
        log.info("Requested recommendations for user {}", id);
        return new ResponseEntity<>(userService.getRecommendations(id), HttpStatus.OK);
    }
}
