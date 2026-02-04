package ru.yandex.practicum.filmorate.storage.film.db;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Repository
public class GenreStorage extends BaseRepository<Genre> {
    private static final String FIND_GENRES_QUERY = "SELECT id from genres";

    public GenreStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    public List<Integer> getGenres() {
        return findMany(FIND_GENRES_QUERY).stream().map(Genre::getId).toList();
    }
}
