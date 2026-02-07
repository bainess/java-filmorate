package ru.yandex.practicum.filmorate.dal.mappers.film;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaName;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class FilmRowMapper implements RowMapper<Film>{

    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getLong("id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setDuration(resultSet.getInt("duration"));
        if (resultSet.getString("mpa_name") != null) {
            MpaName mpa = new MpaName();
            mpa.setId(resultSet.getInt("mpa_name"));
            film.setMpa(mpa);
        }

            java.sql.Array sqlArray = resultSet.getArray("genre_ids");
            if (sqlArray != null) {
            Object[] data = (Object[]) sqlArray.getArray();
            Integer[] genres = Arrays.stream(data)
                    .map(obj -> ((Number) obj).intValue())
                    .toArray(Integer[]::new);
            Arrays.stream(genres).peek(id -> {
                Genre genre = new Genre();
                genre.setId(id);
                film.getGenres().add(genre);
            }).toList();

        }

        Timestamp releaseDate = resultSet.getTimestamp("release_date");
        film.setReleaseDate(releaseDate.toLocalDateTime().toLocalDate());
        return film;
    }
}
