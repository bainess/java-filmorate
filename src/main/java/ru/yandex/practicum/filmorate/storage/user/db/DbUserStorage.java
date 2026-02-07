package ru.yandex.practicum.filmorate.storage.user.db;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserFriend;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Optional;

@Primary
@Repository
public class DbUserStorage extends BaseRepository<User> implements UserStorage {

    private static final String FIND_USER_BY_ID_QUERY = "SELECT \n" +
            "    users.id, \n" +
            "    users.name,\n" +
            "    users.login,\n" +
            "    users.email,\n" +
            "    users.birthday,\n" +
            "    ARRAY_AGG(DISTINCT user_friends.friend_id) FILTER (WHERE user_friends.friend_id IS NOT NULL) AS friends_ids\n" +
            "FROM users\n" +
            "LEFT JOIN user_friends ON users.id = user_friends.user_id\n" +
            "WHERE users.id = ? \n" +
            "GROUP BY \n" +
            "    users.id,\n" +
            "    users.name,\n" +
            "    users.login, \n" +
            "    users.email, \n" +
            "    users.birthday; ";
    private static final String FIND_USER_BY_EMAIL = "SELECT \n" +
            "    users.id, \n" +
            "    users.name,\n" +
            "    users.login,\n" +
            "    users.email,\n" +
            "    users.birthday,\n" +
            "    ARRAY_AGG(DISTINCT user_friends.friend_id) FILTER (WHERE user_friends.friend_id IS NOT NULL) AS friends_ids\n" +
            "FROM users\n" +
            "LEFT JOIN user_friends ON users.id = user_friends.user_id\n" +
            "WHERE users.email = ? \n" +
            "GROUP BY \n" +
            "    users.id,\n" +
            "    users.name,\n" +
            "    users.login, \n" +
            "    users.email, \n" +
            "    users.birthday; ";;

    private static final String FIND_ALL_USERS = "SELECT \n" +
            "    users.id, \n" +
            "    users.name,\n" +
            "    users.login,\n" +
            "    users.email,\n" +
            "    users.birthday,\n" +
            "    ARRAY_AGG(DISTINCT user_friends.friend_id)  \n" +
            " FILTER (WHERE user_friends.friend_id IS NOT NULL) AS friends_ids\n" +
            "FROM users\n" +
            "LEFT JOIN user_friends ON users.id = user_friends.user_id\n" +
            "GROUP BY \n" +
            "    users.id,\n" +
            "    users.name,\n" +
            "    users.login, \n" +
            "    users.email, \n" +
            "    users.birthday; ";

    private static final String INSERT_USER = "INSERT INTO users (name, login, email, birthday) " +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_USER = "UPDATE users SET name=?, login=?, email=?, birthday=? WHERE id=?";
    private static final String INSERT_FRIEND = "INSERT INTO user_friends(user_id, friend_id) VALUES (?, ?)";
    private static final String FIND_USER_FRIENDS = "SELECT * FROM user_friends WHERE user_id = ?";
    private static final String REMOVE_FROM_FRIENDS_QUERY = "DELETE FROM user_friends WHERE user_id = ? AND friend_id = ?";
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

        for (UserFriend friend_id : user.getFriends()) {
            insert(
                    INSERT_FRIEND,
                    user.getId(),
                    friend_id
            );
        }
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
    public Long saveFriend(Long user_id, Long friend) {
        insert(
                INSERT_FRIEND,
                user_id,
                friend
        );
        return friend;
    }

    public void removeFromFriends(Long userId, Long friendId) {
        delete(
                REMOVE_FROM_FRIENDS_QUERY,
                userId,
                friendId
        );
    }
}
