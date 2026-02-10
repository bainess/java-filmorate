package ru.yandex.practicum.filmorate.dal.mappers.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaName;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class FilmRowMapper implements RowMapper<Film> {

    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getLong("id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setDuration(resultSet.getInt("duration"));
        if (resultSet.getString("mpa_name") != null) {
            log.info(resultSet.getString("mpa_name"));
            MpaName mpa = new MpaName();
            mpa.setId(resultSet.getInt("mpa_id"));
            mpa.setName(resultSet.getString("mpa_name"));
            film.setMpa(mpa);
        }

//        java.sql.Array sqlArray = resultSet.getArray("genre_ids");
//        if (sqlArray != null) {
//            Object[] data = (Object[]) sqlArray.getArray();
//            Integer[] genres = Arrays.stream(data)
//                    .map(obj -> ((Number) obj).intValue())
//                    .toArray(Integer[]::new);
//            Arrays.stream(genres).peek(id -> {
//                Genre genre = new Genre();
//                genre.setId(id);
//                film.getGenres().add(genre);
//            }).toList();
//
//        }

        if (resultSet.getString("genres_data") != null) {
            film.setGenres(parseGenres(resultSet.getString("genres_data")));
        }

        if (resultSet.getString("film_likes") != null) {
            java.sql.Array sqlArrayUsers = resultSet.getArray("film_likes");
            if (sqlArrayUsers != null) {
                Object[] data = (Object[]) sqlArrayUsers.getArray();
                Long[] users = Arrays.stream(data)
                        .map(obj -> ((Number) obj).longValue())
                        .toArray(Long[]::new);
                film.addLikes(Arrays.stream(users).toList());
            }
        }

        Timestamp releaseDate = resultSet.getTimestamp("release_date");
        film.setReleaseDate(releaseDate.toLocalDateTime().toLocalDate());
        return film;
    }

    private List<Genre> parseGenres(String genresData) {
        if (genresData == null || genresData.isBlank()) {
            return Collections.emptyList();
        }

        return Arrays.stream(genresData.split(","))
                .map(genreInfo -> {
                    String[] genreArr = genreInfo.split(":");
                    return new Genre(
                            Integer.parseInt(genreArr[0]),
                            genreArr[1]
                    );
                }).toList();
    }
}
