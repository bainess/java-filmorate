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
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Primary
@Repository
public class DbFilmStorage extends BaseRepository<Film> implements FilmStorage {
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;
    private final DirectorStorage directorStorage; // добавлено хранилище режиссёров

    // Изменяем SQL-запросы, добавляем выборку режиссёров
    private static final String FIND_BY_ID_QUERY = "SELECT\n" +
            "    f.id, f.name,\n" +
            "    f.description, \n" +
            "    f.release_date,\n" +
            "    f.duration,\n" +
            "    r.id AS mpa_id,\n" +
            "    r.mpa_name, \n" +
            "    STRING_AGG(DISTINCT g.id || ':' || g.name, ',') AS genres_data, \n" +
            "    STRING_AGG(DISTINCT CAST(fl.user_id AS VARCHAR), ',') AS film_likes, \n" +
            "    STRING_AGG(DISTINCT d.id || ':' || d.director_name, ',') AS directors_data \n" +
            "FROM films f \n" +
            "LEFT JOIN ratings r ON f.mpa_id = r.id \n" +
            "LEFT JOIN films_genre fg ON f.id = fg.film_id \n" +
            "LEFT JOIN genres g ON fg.genre_id = g.id\n" +
            "LEFT JOIN film_likes fl ON f.id = fl.film_id \n" +
            "LEFT JOIN film_directors fd ON f.id = fd.film_id \n" +
            "LEFT JOIN directors d ON fd.director_id = d.id \n" +
            "WHERE f.id = ? \n" +
            "GROUP BY f.id, f.name, f.description, f.release_date, f.duration, r.id, r.mpa_name;";

    private static final String FIND_ALL_QUERY = "SELECT\n" +
            "    f.id, f.name,\n" +
            "    f.description, \n" +
            "    f.release_date,\n" +
            "    f.duration,\n" +
            "    r.id AS mpa_id,\n" +
            "    r.mpa_name, \n" +
            "    STRING_AGG(DISTINCT g.id || ':' || g.name, ',') AS genres_data, \n" +
            "    STRING_AGG(DISTINCT CAST(fl.user_id AS VARCHAR), ',') AS film_likes, \n" +
            "    STRING_AGG(DISTINCT d.id || ':' || d.director_name, ',') AS directors_data \n" +
            "FROM films f \n" +
            "LEFT JOIN ratings r ON f.mpa_id = r.id \n" +
            "LEFT JOIN films_genre fg ON f.id = fg.film_id \n" +
            "LEFT JOIN genres g ON fg.genre_id = g.id\n" +
            "LEFT JOIN film_likes fl ON f.id = fl.film_id \n" +
            "LEFT JOIN film_directors fd ON f.id = fd.film_id \n" +
            "LEFT JOIN directors d ON fd.director_id = d.id \n" +
            "GROUP BY f.id, f.name, f.description, f.release_date, f.duration, r.id, r.mpa_name;";

    private static final String INSERT_QUERY = "INSERT INTO films (name, description, release_date, duration, mpa_id)" +
            "VALUES (?, ?, ?, ?, ?)";

    private static final String INSERT_TO_FILM_GENRE = "INSERT INTO films_genre(film_id, genre_id) VALUES (?, ?)";
    private static final String INSERT_TO_FILM_DIRECTOR = "INSERT INTO film_directors(film_id, director_id) VALUES (?, ?)"; // добавлено
    private static final String UPDATE_FILM = "UPDATE films SET name=?, description=?, release_date=?, duration=?, mpa_id =? WHERE id=?";
    private static final String UPDATE_FILM_GENRE = "UPDATE films_genre SET genre_id=? WHERE film_id=?";
    private static final String UPDATE_FILM_DIRECTOR = "DELETE FROM film_directors WHERE film_id=?"; // добавлено при обновлении сначала удаляем старых режиссёров
    private static final String INSERT_LIKES = "INSERT INTO film_likes(film_id, user_id) VALUES(?, ?)";
    private static final String REMOVE_LIKE_QUERY = "DELETE FROM film_likes WHERE film_id = ? and user_id = ?";
    private static final String FIND_RECOMMENDATIONS_QUERY = """
    SELECT\s
        f.*,\s
        r.mpa_name,\s
        r.id AS mpa_id,
        GROUP_CONCAT(DISTINCT g.id || ':' || g.name ORDER BY g.id SEPARATOR ',') AS genres_data,
        ARRAY_AGG(DISTINCT fl_all.user_id) FILTER (WHERE fl_all.user_id IS NOT NULL) AS film_likes
    FROM films f
    LEFT JOIN ratings r ON f.mpa_id = r.id
    LEFT JOIN films_genre fg ON f.id = fg.film_id
    LEFT JOIN genres g ON fg.genre_id = g.id
    LEFT JOIN film_likes fl_all ON f.id = fl_all.film_id
    WHERE f.id IN (
        SELECT fl_other.film_id
        FROM film_likes fl_other
        WHERE fl_other.user_id = (
            SELECT fl2.user_id
            FROM film_likes fl1
            JOIN film_likes fl2 ON fl1.film_id = fl2.film_id
            WHERE fl1.user_id = ? AND fl2.user_id != ?
            GROUP BY fl2.user_id
            ORDER BY COUNT(fl1.film_id) DESC
            LIMIT 1
        )
        AND fl_other.film_id NOT IN (SELECT film_id FROM film_likes WHERE user_id = ?)
    )
    GROUP BY f.id, r.mpa_name, r.id
   \s""";

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
                Timestamp.valueOf(film.getReleaseDate().atStartOfDay()), // исправлено
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );

        update(UPDATE_FILM_DIRECTOR, film.getId());

        // Обновление режиссёров сначала удаляем старые, потом вставляем новые
        List<Director> directors = film.getDirectors();
        if (directors != null && !directors.isEmpty()) {
            for (Director director : film.getDirectors()) {
                update(INSERT_TO_FILM_DIRECTOR, film.getId(), director.getId());
            }
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
                "STRING_AGG(DISTINCT g.id || ':' || g.name, ',') AS genres_data, " +
                "STRING_AGG(DISTINCT CAST(fl.user_id AS VARCHAR), ',') AS film_likes, " +
                "STRING_AGG(DISTINCT d.id || ':' || d.director_name, ',') AS directors_data " +
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

    @Override
    public Collection<Film> getRecommendations(Long userId) {
        return findMany(FIND_RECOMMENDATIONS_QUERY, userId, userId, userId);
    }
}
