package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    public Optional<User> getUser(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Collection<User> getUsers() {
        return users.values();
    }

    @Override
    public User createUser(User user) {
        User newUser = User.builder()
                .name(user.getName())
                .email(user.getEmail())
                .login(user.getLogin())
                .birthday(user.getBirthday())
                .friends(new HashSet<>())
                .friends(new HashSet<>())
                .build();
        if (user.getId() != null && !users.containsKey(user.getId())) {
            throw new ValidationException("User not found");
        }

        if (user.getName() == null) user.setName(user.getLogin());
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        User oldUser = users.get(user.getId());
        if (user.getName() != null) {
            oldUser.setName(user.getName());
        }
        if (!user.getLogin().isEmpty()) {
            oldUser.setLogin(user.getLogin());
        }
        if (user.getBirthday() != null) {
            oldUser.setBirthday(user.getBirthday());
        }
        if (user.getEmail() != null) {
            oldUser.setEmail(user.getEmail());
        }
        return oldUser;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
