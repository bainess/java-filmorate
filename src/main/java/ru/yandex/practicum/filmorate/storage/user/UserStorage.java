package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserFriend;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserStorage {

    Optional<User> getUser(Long id);

    Collection<User> getUsers();

    User createUser(User user);

    User updateUser(User user);

    Long saveFriend(Long user_id, Long friend);
}
