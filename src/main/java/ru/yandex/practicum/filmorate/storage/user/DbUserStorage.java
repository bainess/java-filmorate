package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

@Primary
@Repository
public class DbUserStorage extends BaseRepository<User> implements UserStorage {

    private static final String FIND_USER_BY_ID_QUERY = """
            SELECT\s
                users.id,\s
                users.name,
                users.login,
                users.email,
                users.birthday,
                STRING_AGG(CAST(user_friends.friend_id AS VARCHAR), ',') AS friends_ids
            FROM users
            LEFT JOIN user_friends ON users.id = user_friends.user_id
            WHERE users.id = ?\s
            GROUP BY\s
                users.id,
                users.name,
                users.login,\s
                users.email,\s
                users.birthday;\s""";
    private static final String FIND_USER_BY_EMAIL = """
            SELECT\s
                users.id,\s
                users.name,
                users.login,
                users.email,
                users.birthday,
                STRING_AGG(CAST(user_friends.friend_id AS VARCHAR), ',') AS friends_ids
            FROM users
            LEFT JOIN user_friends ON users.id = user_friends.user_id
            WHERE users.email = ?\s
            GROUP BY\s
                users.id,
                users.name,
                users.login,\s
                users.email,\s
                users.birthday;\s""";
    private static final String FIND_ALL_USERS = """
            SELECT\s
                users.id,\s
                users.name,
                users.login,
                users.email,
                users.birthday,
                STRING_AGG(CAST(user_friends.friend_id AS VARCHAR), ',') AS friends_ids
            FROM users
            LEFT JOIN user_friends ON users.id = user_friends.user_id
            GROUP BY\s
                users.id,
                users.name,
                users.login,\s
                users.email,\s
                users.birthday;\s""";
    private static final String INSERT_USER = "INSERT INTO users (name, login, email, birthday) " +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_USER = "UPDATE users SET name=?, login=?, email=?, birthday=? WHERE id=?";
    private static final String INSERT_FRIEND = "INSERT INTO user_friends(user_id, friend_id) VALUES (?, ?)";
    private static final String FIND_USER_FRIENDS = "SELECT * FROM user_friends WHERE user_id = ?";
    private static final String REMOVE_FROM_FRIENDS_QUERY = "DELETE FROM user_friends WHERE user_id = ? AND friend_id = ?";
    private static final String DELETE_USER_QUERY = "DELETE FROM users WHERE id = ?";
    private final JdbcTemplate jdbcTemplate;

    public DbUserStorage(JdbcTemplate jdbc, RowMapper<User> mapper, JdbcTemplate jdbcTemplate) {
        super(jdbc, mapper);
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<User> getUser(Long id) {
        return findOne(FIND_USER_BY_ID_QUERY, id);
    }

    public Optional<User> getUserByEmail(String email) {
        return findOne(FIND_USER_BY_EMAIL, email);
    }

    public Collection<User> getFriends(Long userId) {
        String findFriendsQuery = """
                SELECT *
                FROM users
                LEFT JOIN user_friends AS uf ON users.id = uf.friend_id
                WHERE uf.user_id = ?
                ORDER BY uf.friend_id;
                """;
        return findMany(findFriendsQuery, userId);
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
                user.getBirthday(),
                user.getId()
        );
        return user;
    }

    @Override
    public Long saveFriend(Long userId, Long friend) {
        insert(
                INSERT_FRIEND,
                userId,
                friend
        );
        return friend;
    }

    public void removeFromFriends(Long userId, Long friendId) {
        update(
                REMOVE_FROM_FRIENDS_QUERY,
                userId,
                friendId
        );
    }

    @Override
    public void deleteUser(Long id) {
        update(DELETE_USER_QUERY, id);
    }


}
