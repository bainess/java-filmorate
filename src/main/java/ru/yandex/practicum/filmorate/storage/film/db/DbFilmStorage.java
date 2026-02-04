package ru.yandex.practicum.filmorate.storage.film.db;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Optional;

@Primary
@Repository
public class DbFilmStorage extends BaseRepository<Film> implements FilmStorage {
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM films WHERE id = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM films LEFT JOIN films_genre ON" +
        "films.id = film_genre.film_id";
    private static final String INSERT_QUERY = "INSERT INTO films (name, description, release_date, duration)" +
            "VALUES (?, ?, ?, ?)";
    private static final String INSERT_TO_FILM_GENRE = "INSERT INTO films_genre(film_id, genre_id) VALUES (?, ?)";


    public DbFilmStorage(JdbcTemplate jdbc, RowMapper<Film> filmMapper, MpaStorage mpaStorage, GenreStorage genreStorage) {
        super(jdbc, filmMapper);
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
    }

    public Collection<Film> getFilms() {
        return findMany(FIND_ALL_QUERY);
    }

    public Film createFilm(Film film) {
        if (!mpaStorage.getRatings().contains(film.getMpa().getId())) {
            throw new NotFoundException("Invalid rating");
        }

        for (Genre genre : film.getGenres()) {
            if (!genreStorage.getGenres().contains(genre.getId())) {throw new NotFoundException("Invalid genre");}
        }

        long id = insert(
                INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                Timestamp.valueOf(film.getReleaseDate().atStartOfDay()),
                film.getDuration()
        );

        film.setId(id);

        if (!film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                insert(INSERT_TO_FILM_GENRE,
                        film.getId(),
                        genre.getId()
                );
            }
        }

        return film;
    }

    public Optional<Film> getFilm(Long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    };

    public Film updateFilm(Film film) {
        return film;
    };

}
