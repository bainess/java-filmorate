package ru.yandex.practicum.filmorate.dal.mappers.user;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@Component
public class UserRowMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        User user = new User();
        user.setId(resultSet.getLong("id"));
        user.setName(resultSet.getString("name"));
        if (resultSet.getString("login") != null) {
            user.setLogin(resultSet.getString("login"));
        }
        user.setEmail(resultSet.getString("email"));

        Timestamp birthday = resultSet.getTimestamp("birthday");
        user.setBirthday(birthday.toLocalDateTime().toLocalDate());

        return user;
    }
}
