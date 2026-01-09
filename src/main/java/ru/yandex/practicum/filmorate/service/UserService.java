package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
public class UserService {
    private UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User getUser(Long id) {
        return userStorage.getUser(id);
    }

    public Collection<User> getUsers() {
        return new ArrayList<>(userStorage.getUsers());
    }

    public Optional<User> updateUser(User user) {
        if (userStorage.getUser(user.getId()) == null) {
            throw new NotFoundException("User not found");
        }
        return Optional.of(userStorage.updateUser(user));
    }


    public List<User> getCommonFriends(Long userId, Long friendId) {
        return userStorage.getUser(userId).getFriends().stream()
                .filter(id -> userStorage.getUser(friendId).getFriends().contains(id))
                .map(id -> userStorage.getUser(id))
                .toList();
    }

    public Collection<User> getFriends(Long id) {
        if (userStorage.getUser(id) == null) {
            throw new NotFoundException("User was not found");
        }
        Set<Long> friendsIds = userStorage.getUser(id).getFriends();
        return new ArrayList<>(userStorage.getUsers().stream()
                .filter(user1 -> friendsIds.contains(user1.getId()))
                .toList());
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public void setFriendship(Long user1, Long user2) {
        if (userStorage.getUser(user1) == null) {
            throw new NotFoundException("User" + user1 + " was not found");
        }
        if (userStorage.getUser(user2) == null) {
            throw new NotFoundException("User" + user2 + " was not found");
        }
        addFriend(user1, user2);
        addFriend(user2, user1);
    }

    private Long addFriend(Long user, Long friend) {
        return userStorage.getUser(user).setFriends(friend);
    }

    public void removeFromFriends(Long user1, Long user2) {
        if (userStorage.getUser(user1) == null) {
            throw new NotFoundException("User" + user1 + " was not found");
        }
        if (userStorage.getUser(user2) == null) {
            throw new NotFoundException("User" + user2 + " was not found");
        }
        removeFriend(user1, user2);
        removeFriend(user2, user1);
    }

    private Long removeFriend(Long userId, Long friendId) {
        userStorage.getUser(userId).getFriends().remove(friendId);
        return friendId;
    }


}
