package ru.yandex.practicum.filmorate.dal.mappers.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaName;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class FilmRowMapper implements RowMapper<Film> {

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setDuration(rs.getInt("duration"));

        // MPA
        if (rs.getString("mpa_name") != null) {
            MpaName mpa = new MpaName();
            mpa.setId(rs.getInt("mpa_id"));
            mpa.setName(rs.getString("mpa_name"));
            film.setMpa(mpa);
        }

        // Genres
        if (rs.getString("genres_data") != null) {
            film.setGenres(parseGenres(rs.getString("genres_data")));
        }

        // Likes (STRING_AGG вернул строку "1,2,3")
        if (rs.getString("film_likes") != null && !rs.getString("film_likes").isBlank()) {
            List<Long> likes = Arrays.stream(rs.getString("film_likes").split(","))
                    .filter(s -> !s.isEmpty())
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            film.addLikes(likes);
        }

        // Directors
        if (rs.getString("directors_data") != null) {
            film.setDirectors(parseDirectors(rs.getString("directors_data")));
        }

        // Release date
        Timestamp releaseDate = rs.getTimestamp("release_date");
        if (releaseDate != null) {
            film.setReleaseDate(releaseDate.toLocalDateTime().toLocalDate());
        }

        return film;
    }

    private List<Genre> parseGenres(String genresData) {
        if (genresData == null || genresData.isBlank()) return Collections.emptyList();

        return Arrays.stream(genresData.split(","))
                .map(genreInfo -> {
                    String[] parts = genreInfo.split(":");
                    int id = Integer.parseInt(parts[0].trim());
                    String name = parts.length > 1 ? parts[1].trim() : null;
                    return new Genre(id, name);
                })
                .collect(Collectors.toList());
    }

    private List<Director> parseDirectors(String directorsData) {
        if (directorsData == null || directorsData.isBlank()) return Collections.emptyList();

        return Arrays.stream(directorsData.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(directorInfo -> {
                    String[] parts = directorInfo.split(":");
                    long id = Long.parseLong(parts[0].trim());
                    String name = parts.length > 1 ? parts[1].trim() : null;
                    return new Director(id, name);
                })
                .collect(Collectors.toList());
    }
}