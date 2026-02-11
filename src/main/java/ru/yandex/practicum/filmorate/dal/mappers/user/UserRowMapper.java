package ru.yandex.practicum.filmorate.dal.mappers.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;

@Slf4j
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
        if (resultSet.getString("email") != null) {
            user.setEmail(resultSet.getString("email"));
        }
        if (resultSet.getTimestamp("birthday") != null) {
            Timestamp birthday = resultSet.getTimestamp("birthday");
            user.setBirthday(birthday.toLocalDateTime().toLocalDate());
        }

        if (resultSet.getString("friends_ids") != null) {
            java.sql.Array sqlArray = resultSet.getArray("friends_ids");
            if (sqlArray != null) {
                Object[] data = (Object[]) sqlArray.getArray();
                Arrays.stream(data)
                        .map(obj -> ((Number) obj).longValue())
                        .forEach(user::addFriendToList);
            }
       }
        return user;
    }
}
