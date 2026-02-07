package ru.yandex.practicum.filmorate.storage.user.db;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.model.UserFriend;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class DbFriendsStorage extends BaseRepository<UserFriend> {
    private static final String FIND_FRIENDS_QUERY = "SELECT friend_id FROM user_friends WHERE user_id = ?";
    public DbFriendsStorage(JdbcTemplate jdbc, RowMapper<UserFriend> mapper) {
        super(jdbc, mapper);
    }

    public Set<UserFriend> getFriends(Long id) {
        return new HashSet<>(findMany(FIND_FRIENDS_QUERY, id));
    }
}
