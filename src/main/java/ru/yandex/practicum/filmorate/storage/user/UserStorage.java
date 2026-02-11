package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    Optional<User> getUser(Long id);

    Collection<User> getUsers();

    User createUser(User user);

    User updateUser(User user);

    Long saveFriend(Long userId, Long friend);

    void removeFromFriends(Long userId, Long friendId);
}
