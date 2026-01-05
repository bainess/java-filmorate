package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
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

    public Optional<User> getUser(Long id) {
        return Optional.of(userStorage.getUser(id));
    }

    public Collection<User> getUsers() {
        return new ArrayList<>(userStorage.getUsers());
    }

    public Optional<User> updateUser(User user) {
        return Optional.of(userStorage.updateUser(user));
    }


    public List<User> getCommonFriends(Long userId, Long friendId) {
        return userStorage.getUser(userId).getFriends().stream()
                .filter(id -> userStorage.getUser(friendId).getFriends().contains(id))
                .map(id -> userStorage.getUser(id))
                .toList();
    }

    public Collection<User> getFriends(User user) {
        Set<Long> friendsIds = userStorage.getUser(user.getId()).getFriends();
        return new ArrayList<>(userStorage.getUsers().stream()
                .filter(user1 -> friendsIds.contains(user1.getId()))
                .toList());
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public void setFriendship(User user1, User user2) {
        addFriend(user1, user2);
        addFriend(user2, user1);
    }

    public void removeFromFriends(User user1, User user2) {
        removeFriend(user1, user2);
        removeFriend(user2, user1);
    }

    private Long removeFriend(User user, User friend) {
        if (!userStorage.getUser(user.getId()).getFriends().contains(friend.getId())) {
            throw new ValidationException("User has no such friend");
        }

        userStorage.getUser(user.getId()).getFriends().remove(friend.getId());
        return friend.getId();
    }

    private Long addFriend(User user, User friend) {
        if (!userStorage.getUsers().contains(user) || !userStorage.getUsers().contains(friend)) {
            throw new ValidationException("User not found");
        }
        return userStorage.getUser(user.getId()).setFriends(friend.getId());
    }
}
