package ru.yandex.practicum.filmorate.storage.user.db;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Optional;

@Primary
@Repository
public class DbUserStorage extends BaseRepository<User> implements UserStorage {
    private static final String FIND_USER_BY_ID_QUERY = "SELECT * FROM users WHERE id=?";
    private static final String FIND_USER_BY_EMAIL = "SELECT * FROM users WHERE email=?";
    private static final String FIND_ALL_USERS = "SELECT * FROM users";
    private static final String INSERT_USER = "INSERT INTO users (name, login, email, birthday) " +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_USER = "UPDATE users SET name=?, login=?, email=?, birthday=? WHERE id=?";
    public DbUserStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }
    @Override
    public Optional<User> getUser(Long id) {
        return findOne(FIND_USER_BY_ID_QUERY, id);
    }

    public Optional<User> getUserByEmail(String email) {
        return findOne(FIND_USER_BY_EMAIL, email);
    }

    @Override
    public Collection<User> getUsers() {
        return findMany(FIND_ALL_USERS);
    }

    @Override
    public User createUser(User user) {
        long id = insert(
                INSERT_USER,
                user.getName(),
                user.getLogin(),
                user.getEmail(),
                user.getBirthday()
        );
        user.setId(id);
        return user;
    }

    @Override
    public User updateUser(User user) {
        update(
                UPDATE_USER,
                user.getName(),
                user.getLogin(),
                user.getEmail(),
                user.getBirthday()
        );
        return user;
    }
}
