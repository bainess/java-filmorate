package ru.yandex.practicum.filmorate.storage.film.db;

import jakarta.validation.ValidationException;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaName;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Optional;

@Primary
@Repository
public class DbFilmStorage extends BaseRepository<Film> implements FilmStorage {
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;
    private final DirectorStorage directorStorage; // добавлено хранилище режиссёров

    // Изменяем SQL-запросы, добавляем выборку режиссёров
    private static final String FIND_BY_ID_QUERY = "SELECT\n" +
            "                f.id, f.name,\n" +
            "                f.description, \n" +
            "                f.release_date,\n" +
            "                f.duration,\n" +
            "                r.id AS mpa_id,\n" +
            "                 r.mpa_name, \n" +
            "                GROUP_CONCAT(DISTINCT g.id || ':' || g.name ORDER BY g.id  SEPARATOR ',') AS genres_data, \n" +
            "                ARRAY_AGG(DISTINCT fl.user_id) FILTER (WHERE fl.user_id IS NOT NULL) AS film_likes, \n" +
            "                ARRAY_AGG(DISTINCT d.id || ':' || d.director_name) AS directors_data \n" + // добавлены данные режиссёров
            "            FROM films f \n" +
            "           LEFT JOIN ratings r ON f.mpa_id = r.id \n" +
            "           LEFT JOIN films_genre fg ON f.id = fg.film_id \n" +
            "           LEFT JOIN genres g ON fg.genre_id = g.id\n" +
            "           LEFT JOIN film_likes fl ON f.id = fl.film_id \n" +
            "           LEFT JOIN film_directors fd ON f.id = fd.film_id \n" + // добавлено соединение с film_directors
            "           LEFT JOIN directors d ON fd.director_id = d.id \n" + // добавлено соединение с directors
            "           WHERE f.id = ? \n" +
            "           GROUP BY\n" +
            "                f.id, f.name,\n" +
            "                f.description,\n" +
            "                 f.release_date, \n" +
            "                f.duration,\n" +
            "                 r.id, \n" +
            "                r.mpa_name;";

    private static final String FIND_ALL_QUERY = "SELECT\n" +
            "                f.id, f.name,\n" +
            "                f.description, \n" +
            "                f.release_date,\n" +
            "                f.duration,\n" +
            "                r.id AS mpa_id,\n" +
            "                 r.mpa_name, \n" +
            "                GROUP_CONCAT(DISTINCT g.id || ':' || g.name ORDER BY g.id  SEPARATOR ',') AS genres_data, \n" +
            "                ARRAY_AGG(DISTINCT fl.user_id) FILTER (WHERE fl.user_id IS NOT NULL) AS film_likes, \n" +
            "                ARRAY_AGG(DISTINCT d.id || ':' || d.director_name) AS directors_data \n" + // добавлено
            "            FROM films f \n" +
            "           LEFT JOIN ratings r ON f.mpa_id = r.id \n" +
            "           LEFT JOIN films_genre fg ON f.id = fg.film_id \n" +
            "           LEFT JOIN genres g ON fg.genre_id = g.id\n" +
            "           LEFT JOIN film_likes fl ON f.id = fl.film_id \n" +
            "           LEFT JOIN film_directors fd ON f.id = fd.film_id \n" + // добавлено
            "           LEFT JOIN directors d ON fd.director_id = d.id \n" + // добавлено
            "           GROUP BY\n" +
            "                f.id, f.name,\n" +
            "                f.description,\n" +
            "                 f.release_date, \n" +
            "                f.duration,\n" +
            "                 r.id, \n" +
            "                r.mpa_name;";

    private static final String INSERT_QUERY = "INSERT INTO films (name, description, release_date, duration, mpa_id)" +
            "VALUES (?, ?, ?, ?, ?)";

    private static final String INSERT_TO_FILM_GENRE = "INSERT INTO films_genre(film_id, genre_id) VALUES (?, ?)";
    private static final String INSERT_TO_FILM_DIRECTOR = "INSERT INTO film_directors(film_id, director_id) VALUES (?, ?)"; // добавлено
    private static final String UPDATE_FILM = "UPDATE films SET name=?, description=?, release_date=?, duration=?, mpa_id =? WHERE id=?";
    private static final String UPDATE_FILM_GENRE = "UPDATE films_genre SET genre_id=? WHERE film_id=?";
    private static final String UPDATE_FILM_DIRECTOR = "DELETE FROM film_directors WHERE film_id=?"; // добавлено при обновлении сначала удаляем старых режиссёров
    private static final String INSERT_LIKES = "INSERT INTO film_likes(film_id, user_id) VALUES(?, ?)";
    private static final String REMOVE_LIKE_QUERY = "DELETE FROM film_likes WHERE film_id = ? and user_id = ?";

    public DbFilmStorage(JdbcTemplate jdbc, RowMapper<Film> filmMapper, MpaStorage mpaStorage, GenreStorage genreStorage, DirectorStorage directorStorage) {
        super(jdbc, filmMapper);
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
        this.directorStorage = directorStorage; // добавлено
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

        // проверка наличия хотя бы одного режиссёра
        if (film.getDirectors() == null || film.getDirectors().isEmpty()) {
            throw new ValidationException("Film must have at least one director"); // добавлено
        }

        long id = insert(
                INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                Timestamp.valueOf(film.getReleaseDate().atStartOfDay()),
                film.getDuration(),
                film.getMpa().getId()
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

        // вставка режиссёров
        for (Director director : film.getDirectors()) { // добавлено
            update(INSERT_TO_FILM_DIRECTOR, film.getId(), director.getId()); // добавлено
        }

        return film;
    }

    public Optional<Film> findFilm(Long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    public Film updateFilm(Film film) {
        update(
                UPDATE_FILM,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );

        // Обновление режиссёров сначала удаляем старые, потом вставляем новые
        update(UPDATE_FILM_DIRECTOR, film.getId()); // удаляем старых
        for (Director director : film.getDirectors()) { // вставляем новых
            insert(INSERT_TO_FILM_DIRECTOR, film.getId(), director.getId());
        }

        return film;
    }

    public void addLike(Long filmId, Long userId) {
        update(INSERT_LIKES, filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        update(REMOVE_LIKE_QUERY, filmId, userId);
    }

    //Получение списка фильмов конкретного режиссёра с сортировкой.

    public Collection<Film> getFilmsByDirector(long directorId, String sortBy) {
        String baseQuery = "SELECT " +
                "f.id, f.name, f.description, f.release_date, f.duration, " +
                "r.id AS mpa_id, r.mpa_name, " +
                "GROUP_CONCAT(DISTINCT g.id || ':' || g.name ORDER BY g.id SEPARATOR ',') AS genres_data, " +
                "ARRAY_AGG(DISTINCT fl.user_id) FILTER (WHERE fl.user_id IS NOT NULL) AS film_likes, " +
                "ARRAY_AGG(DISTINCT d.id || ':' || d.director_name) AS directors_data " +
                "FROM films f " +
                "LEFT JOIN ratings r ON f.mpa_id = r.id " +
                "LEFT JOIN films_genre fg ON f.id = fg.film_id " +
                "LEFT JOIN genres g ON fg.genre_id = g.id " +
                "LEFT JOIN film_likes fl ON f.id = fl.film_id " +
                "LEFT JOIN film_directors fd ON f.id = fd.film_id " +
                "LEFT JOIN directors d ON fd.director_id = d.id " +
                "WHERE d.id = ? " +
                "GROUP BY f.id, f.name, f.description, f.release_date, f.duration, r.id, r.mpa_name ";

        // Добавляем сортировку в зависимости от параметра sortBy
        switch (sortBy) {
            case "year":
                baseQuery += "ORDER BY f.release_date";
                break;
            case "likes":
                baseQuery += "ORDER BY COUNT(DISTINCT fl.user_id) DESC";
                break;
            default:
                throw new IllegalArgumentException("sortBy must be 'year' or 'likes'");
        }

        return findMany(baseQuery, directorId); // findMany из BaseRepository возвращает Collection<Film>
    }
}