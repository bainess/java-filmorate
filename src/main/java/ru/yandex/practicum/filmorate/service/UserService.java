package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Set;

import static com.fasterxml.jackson.databind.type.LogicalType.Collection;

@Service
public class UserService {
    private UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Set<Long> getFriends(User user) {
        return userStorage.getUser(user.getId()).getFriends();
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
        boolean a = userStorage.getUsers().contains(user);
        boolean b = userStorage.getUsers().contains(friend);
        if (!userStorage.getUsers().contains(user) || !userStorage.getUsers().contains(friend)) {
            throw new ValidationException("User not found");
        }
        return userStorage.getUser(user.getId()).setFriends(friend.getId());
    }
}
