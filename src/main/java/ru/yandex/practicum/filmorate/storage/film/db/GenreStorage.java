package ru.yandex.practicum.filmorate.storage.film.db;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

@Repository
public class GenreStorage extends BaseRepository<Genre> {
    private static final String FIND_GENRES_QUERY = "SELECT * from genres ORDER BY id";
    private static final String FIND_GENRE_QUERY = "SELECT * FROM genres WHERE id = ?";

    public GenreStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    public List<Genre> getGenres() {
        return findMany(FIND_GENRES_QUERY);
    }

    public Optional<Genre> getGenre(int id) {
        return findOne(FIND_GENRE_QUERY, id);
    }
}
