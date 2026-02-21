package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.mappers.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserFriend;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
public class UserService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final EventService eventService;

    @Autowired
    public UserService(UserStorage userStorage, FilmStorage filmStorage, EventService eventService) {

        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.eventService = eventService;
    }

    public UserDto getUserById(Long id) {
        return userStorage.getUser(id)
                .map(UserMapper::mapToUserDto)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    public Collection<UserDto> getUsers() {
        return new ArrayList<>(userStorage.getUsers())
                .stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    public UserDto updateUser(Long id, UpdateUserRequest request) {

        if (userStorage.getUser(id).isEmpty()) {
            throw new NotFoundException("User not found");
        }
        User updatedUser = userStorage.getUser(id)
                .map(user -> UserMapper.updateUserFields(request, user))
                .orElseThrow(() -> new NotFoundException("User not found"));
        updatedUser = userStorage.updateUser(updatedUser);
        return UserMapper.mapToUserDto(updatedUser);
    }


    public List<UserDto> getCommonFriends(Long userId, Long friendId) {
        return userStorage.getUser(userId).get().getFriends().stream()
                .filter(id -> userStorage.getUser(friendId).get().getFriends().contains(id))
                .map(user -> userStorage.getUser(user.getId()))
                .map(user -> UserMapper.mapToUserDto(user.get()))
                .toList();
    }

    public Collection<UserFriend> getFriends(Long id) {
        if (userStorage.getUser(id).isEmpty()) {
            throw new NotFoundException("User " + id + " was not found");
        }
        return userStorage.getUser(id).get().getFriends();
    }

    public UserDto createUser(NewUserRequest request) {
        User user = UserMapper.mapToUser(request);
        user = userStorage.createUser(user);
        return UserMapper.mapToUserDto(user);
    }

    public void setFriendship(Long userId, Long friendId) {
        if (userStorage.getUser(userId).isEmpty()) {
            throw new NotFoundException("User " + userId + " was not found");
        }
        if (userStorage.getUser(friendId).isEmpty()) {
            throw new NotFoundException("User " + friendId + " was not found");
        }
        eventService.createEvent(userId, "ADD", "FRIEND", friendId);
        userStorage.saveFriend(userId, friendId);
    }

    public void removeFromFriends(Long userId, Long friendId) {
        if (userStorage.getUser(userId).isEmpty()) {
            throw new NotFoundException("User " + userId + " was not found");
        }
        if (userStorage.getUser(friendId).isEmpty()) {
            throw new NotFoundException("User " + friendId + " was not found");
        }
        eventService.createEvent(userId, "REMOVE", "FRIEND", friendId);
        userStorage.removeFromFriends(userId, friendId);
    }

    private Long removeFriend(Long userId, Long friendId) {
        userStorage.getUser(userId).get().getFriends().remove(friendId);
        return friendId;
    }

    public Collection<FilmDto> getRecommendations(Long userId) {
        userStorage.getUser(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return filmStorage.getRecommendations(userId).stream()
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

    public void deleteUser(Long id) {
        userStorage.getUser(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));
        userStorage.deleteUser(id);
    }

}
