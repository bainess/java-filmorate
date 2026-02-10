package ru.yandex.practicum.filmorate.storage.film.db;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaName;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Optional;

@Primary
@Repository
public class DbFilmStorage extends BaseRepository<Film> implements FilmStorage {
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;
    private static final String FIND_BY_ID_QUERY = "SELECT\n" +
            "     f.id, f.name, \n" +
            "     f.description, \n" +
            "     f.release_date,\n" +
            "     f.duration,\n" +
            "     r.id AS mpa_id,\n" +
            "      r.mpa_name, \n" +
            "     GROUP_CONCAT(DISTINCT g.id || ':' || g.name SEPARATOR ',') AS genres_data, \n" +
            "     ARRAY_AGG(DISTINCT fl.user_id) FILTER (WHERE fl.user_id IS NOT NULL) AS film_likes\n" +
            " FROM films f \n" +
            "LEFT JOIN films_ratings fr ON f.id = fr.film_id \n" +
            "LEFT JOIN ratings r ON fr.mpa_name = r.id \n" +
            "LEFT JOIN films_genre fg ON f.id = fg.film_id \n" +
            "LEFT JOIN genres g ON fg.genre_id = g.id \n" +
            "LEFT JOIN film_likes fl ON f.id = fl.film_id \n" +
            "WHERE f.id = ?" +
            "GROUP BY \n" +
            "     f.id, f.name, \n" +
            "     f.description,\n" +
            "      f.release_date, \n" +
            "     f.duration, \n" +
            "     r.mpa_name;";
    private static final String FIND_ALL_QUERY = "SELECT\n" +
            "     f.id, f.name, \n" +
            "     f.description, \n" +
            "     f.release_date,\n" +
            "     f.duration,\n" +
            "     r.id AS mpa_id,\n" +
            "      r.mpa_name, \n" +
            "     GROUP_CONCAT(DISTINCT g.id || ':' || g.name SEPARATOR ',') AS genres_data, \n" +
            "     ARRAY_AGG(DISTINCT fl.user_id) FILTER (WHERE fl.user_id IS NOT NULL) AS film_likes\n" +
            " FROM films f \n" +
            "LEFT JOIN films_ratings fr ON f.id = fr.film_id \n" +
            "LEFT JOIN ratings r ON fr.mpa_name = r.id \n" +
            "LEFT JOIN films_genre fg ON f.id = fg.film_id \n" +
            "LEFT JOIN genres g ON fg.genre_id = g.id \n" +
            "LEFT JOIN film_likes fl ON f.id = fl.film_id \n" +
            "GROUP BY \n" +
            "     f.id, f.name, \n" +
            "     f.description,\n" +
            "      f.release_date, \n" +
            "     f.duration, \n" +
            "     r.mpa_name;";
    private static final String INSERT_QUERY = "INSERT INTO films (name, description, release_date, duration)" +
            "VALUES (?, ?, ?, ?)";
    private static final String INSERT_TO_FILM_GENRE = "INSERT INTO films_genre(film_id, genre_id) VALUES (?, ?)";
    private static final String INSERT_TO_FILM_RATING = "INSERT INTO films_ratings(film_id, mpa_name) VALUES (?, ?)";
    private static final String UPDATE_FILM = "UPDATE films SET name=?, description=?, release_date=?, duration=? WHERE id=?";
    private static final String UPDATE_FILM_RATING = "UPDATE films_ratings SET mpa_name=? WHERE film_id=?";
    private static final String UPDATE_FILM_GENRE = "UPDATE films_genre SET genre_id=? WHERE film_id=?";
    private static final String INSERT_LIKES = "INSERT INTO film_likes(film_id, user_id) VALUES(?, ?)";
    private static final String REMOVE_LIKE_QUERY = "DELETE FROM film_likes WHERE film_id = ? and user_id = ?";

    public DbFilmStorage(JdbcTemplate jdbc, RowMapper<Film> filmMapper, MpaStorage mpaStorage, GenreStorage genreStorage) {
        super(jdbc, filmMapper);
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
    }

    public Collection<Film> getFilms() {
        return findMany(FIND_ALL_QUERY);
    }

    public Film createFilm(Film film) {
        if (!mpaStorage.getRatings().stream().map(MpaName::getId).toList().contains(film.getMpa().getId())) {
            throw new NotFoundException("Invalid rating");
        }

        for (Genre genre : film.getGenres()) {
            if (!genreStorage.getGenres().stream().map(Genre::getId).toList().contains(genre.getId())) {
                throw new NotFoundException("Invalid genre");
            }
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

        if (film.getMpa() != null) {
            insert(
                    INSERT_TO_FILM_RATING,
                    film.getId(),
                    film.getMpa().getId()
            );
        }
        return film;
    }

    public Optional<Film> findFilm(Long id) {
        return findOne(
                FIND_BY_ID_QUERY,
                id
        );
    }

    public Film updateFilm(Film film) {
        update(
                UPDATE_FILM,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getId()
        );
        update(
                UPDATE_FILM_RATING,
                film.getMpa().getId(),
                film.getId()
        );
        return film;
    }

    public void addLike(Long filmId,Long userId) {
        update(
                INSERT_LIKES,
                filmId,
                userId
        );
    }

    public void removeLike(Long filmId, Long userId) {
        update(
                REMOVE_LIKE_QUERY,
                filmId,
                userId
        );
    }
}
