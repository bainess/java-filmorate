package ru.yandex.practicum.filmorate.dal.mappers.film;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.MpaName;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MpaMapper implements RowMapper<MpaName> {

    @Override
    public MpaName mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        MpaName mpa = new MpaName();
        mpa.setId(resultSet.getInt("id"));
        mpa.setName(resultSet.getString("mpa_name"));
        return mpa;
    }
}
