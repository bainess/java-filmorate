package ru.yandex.practicum.filmorate.dal.mappers.user;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserFriend;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FriendMapper implements RowMapper<UserFriend> {

    @Override
    public UserFriend mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserFriend uf= new UserFriend();
        uf.setId(rs.getLong("friend_id"));
        return uf;
    }
}
