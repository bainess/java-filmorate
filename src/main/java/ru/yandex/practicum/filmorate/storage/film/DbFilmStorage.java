package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaName;

import java.sql.Timestamp;
import java.util.*;

@Primary
@Repository
public class DbFilmStorage extends BaseRepository<Film> implements FilmStorage {
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;
    private final DirectorStorage directorStorage;
    private final JdbcTemplate jdbc;

    private static final String FIND_BY_ID_QUERY = """
            SELECT
                f.id, f.name,
                f.description,\s
                f.release_date,
                f.duration,
                r.id AS mpa_id,
                r.mpa_name,\s
                STRING_AGG(DISTINCT g.id || ':' || g.name, ',') AS genres_data,\s
                STRING_AGG(DISTINCT CAST(fl.user_id AS VARCHAR), ',') AS film_likes,\s
                STRING_AGG(DISTINCT d.id || ':' || d.director_name, ',') AS directors_data\s
            FROM films f\s
            LEFT JOIN ratings r ON f.mpa_id = r.id\s
            LEFT JOIN films_genre fg ON f.id = fg.film_id\s
            LEFT JOIN genres g ON fg.genre_id = g.id
            LEFT JOIN film_likes fl ON f.id = fl.film_id\s
            LEFT JOIN film_directors fd ON f.id = fd.film_id\s
            LEFT JOIN directors d ON fd.director_id = d.id\s
            WHERE f.id = ?\s
            GROUP BY f.id, f.name, f.description, f.release_date, f.duration, r.id, r.mpa_name;""";

    private static final String FIND_ALL_QUERY = """
            SELECT
                f.id, f.name,
                f.description,\s
                f.release_date,
                f.duration,
                r.id AS mpa_id,
                r.mpa_name,\s
                STRING_AGG(DISTINCT g.id || ':' || g.name, ',') AS genres_data,\s
                STRING_AGG(DISTINCT CAST(fl.user_id AS VARCHAR), ',') AS film_likes,\s
                STRING_AGG(DISTINCT d.id || ':' || d.director_name, ',') AS directors_data\s
            FROM films f\s
            LEFT JOIN ratings r ON f.mpa_id = r.id\s
            LEFT JOIN films_genre fg ON f.id = fg.film_id\s
            LEFT JOIN genres g ON fg.genre_id = g.id
            LEFT JOIN film_likes fl ON f.id = fl.film_id\s
            LEFT JOIN film_directors fd ON f.id = fd.film_id\s
            LEFT JOIN directors d ON fd.director_id = d.id\s
            GROUP BY f.id, f.name, f.description, f.release_date, f.duration, r.id, r.mpa_name;""";

    private static final String INSERT_QUERY = "INSERT INTO films (name, description, release_date, duration, mpa_id)" +
            "VALUES (?, ?, ?, ?, ?)";

    private static final String INSERT_TO_FILM_GENRE = "INSERT INTO films_genre(film_id, genre_id) VALUES (?, ?)";
    private static final String INSERT_TO_FILM_DIRECTOR = "INSERT INTO film_directors(film_id, director_id) VALUES (?, ?)";
    private static final String UPDATE_FILM = "UPDATE films SET name=?, description=?, release_date=?, duration=?, mpa_id =? WHERE id=?";
    private static final String INSERT_LIKES
            = "MERGE INTO film_likes (film_id, user_id) KEY (film_id, user_id) VALUES (?, ?)";
    private static final String REMOVE_LIKE_QUERY = "DELETE FROM film_likes WHERE film_id = ? and user_id = ?";
    private static final String FIND_RECOMMENDATIONS_QUERY = """
             SELECT
                 f.id,
                 f.name,
                 f.description,
                 f.release_date,
                 f.duration,
                 r.id AS mpa_id,
                 r.mpa_name,
                 GROUP_CONCAT(DISTINCT g.id || ':' || g.name ORDER BY g.id SEPARATOR ',') AS genres_data,
                 GROUP_CONCAT(DISTINCT fl_all.user_id ORDER BY fl_all.user_id SEPARATOR ',') AS film_likes,
                 GROUP_CONCAT(DISTINCT d.id || ':' || d.director_name ORDER BY d.id SEPARATOR ',') AS directors_data
             FROM films f
             LEFT JOIN ratings r ON f.mpa_id = r.id
             LEFT JOIN films_genre fg ON f.id = fg.film_id
             LEFT JOIN genres g ON fg.genre_id = g.id
             LEFT JOIN film_likes fl_all ON f.id = fl_all.film_id
             LEFT JOIN film_directors fd ON f.id = fd.film_id
             LEFT JOIN directors d ON fd.director_id = d.id
             WHERE f.id IN (
                 SELECT DISTINCT fl.film_id
                 FROM film_likes fl
                 WHERE fl.user_id IN (
                     SELECT fl2.user_id
                     FROM film_likes fl1
                     JOIN film_likes fl2 ON fl1.film_id = fl2.film_id
                     WHERE fl1.user_id = ?
                         AND fl2.user_id != ?
                     GROUP BY fl2.user_id
                     HAVING COUNT(DISTINCT fl1.film_id) > 0
                     ORDER BY COUNT(DISTINCT fl1.film_id) DESC
                 )
                 AND fl.film_id NOT IN (
                     SELECT film_id FROM film_likes WHERE user_id = ?
                 )
             )
             GROUP BY f.id, f.name, f.description, f.release_date, f.duration, r.id, r.mpa_name
            \s""";
    private static final String DELETE_FILM_QUERY = "DELETE FROM films WHERE id = ?";

    public DbFilmStorage(JdbcTemplate jdbc, RowMapper<Film> filmMapper,
                         MpaStorage mpaStorage, GenreStorage genreStorage,
                         DirectorStorage directorStorage) {
        super(jdbc, filmMapper);
        this.jdbc = jdbc;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
        this.directorStorage = directorStorage;
    }

    public Collection<Film> getFilms() {
        return findMany(FIND_ALL_QUERY);
    }

    public Film createFilm(Film film) {
        if (!mpaStorage.getRatings().stream().map(MpaName::getId).toList().contains(film.getMpa().getId())) {
            throw new NotFoundException("Invalid rating");
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

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            List<Object[]> batchArgs = new ArrayList<>();
            for (Genre genre : film.getGenres()) {
                if (genre != null) {
                    batchArgs.add(new Object[]{film.getId(), genre.getId()});
                }
            }
            if (!batchArgs.isEmpty()) {
                jdbc.batchUpdate(INSERT_TO_FILM_GENRE, batchArgs);
            }
        }

        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            List<Object[]> batchArgs = new ArrayList<>();
            for (Director director : film.getDirectors()) {
                if (director != null) {
                    batchArgs.add(new Object[]{film.getId(), director.getId()});
                }
            }
            if (!batchArgs.isEmpty()) {
                jdbc.batchUpdate(INSERT_TO_FILM_DIRECTOR, batchArgs);
            }
        }

        return film;
    }

    public Optional<Film> findFilm(Long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    public Film updateFilm(Film film) {
        Optional<Film> existingFilm = findFilm(film.getId());
        if (existingFilm.isEmpty()) {
            throw new NotFoundException("Film with id " + film.getId() + " not found");
        }

        update(
                UPDATE_FILM,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate() != null ? Timestamp.valueOf(film.getReleaseDate().atStartOfDay()) : null,
                film.getDuration(),
                film.getMpa() != null ? film.getMpa().getId() : null,
                film.getId()
        );

        update("DELETE FROM films_genre WHERE film_id = ?", film.getId());

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            List<Object[]> batchArgs = new ArrayList<>();
            for (Genre genre : film.getGenres()) {
                if (genre != null) {
                    batchArgs.add(new Object[]{film.getId(), genre.getId()});
                }
            }
            if (!batchArgs.isEmpty()) {
                jdbc.batchUpdate(INSERT_TO_FILM_GENRE, batchArgs);
            }
        }

        update("DELETE FROM film_directors WHERE film_id = ?", film.getId());

        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            List<Object[]> batchArgs = new ArrayList<>();
            for (Director director : film.getDirectors()) {
                if (director != null) {
                    batchArgs.add(new Object[]{film.getId(), director.getId()});
                }
            }
            if (!batchArgs.isEmpty()) {
                jdbc.batchUpdate(INSERT_TO_FILM_DIRECTOR, batchArgs);
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

    public Collection<Film> getFilmsByDirector(long directorId, String sortBy) {
        String sql = """
                SELECT
                    f.id, f.name, f.description, f.release_date, f.duration,
                    r.id AS mpa_id, r.mpa_name,
                    STRING_AGG(DISTINCT g.id || ':' || g.name, ',') AS genres_data,
                    STRING_AGG(DISTINCT CAST(fl.user_id AS VARCHAR), ',') AS film_likes,
                    STRING_AGG(DISTINCT d2.id || ':' || d2.director_name, ',') AS directors_data
                FROM films f
                LEFT JOIN ratings r ON f.mpa_id = r.id
                LEFT JOIN films_genre fg ON f.id = fg.film_id
                LEFT JOIN genres g ON fg.genre_id = g.id
                LEFT JOIN film_likes fl ON f.id = fl.film_id
                LEFT JOIN film_directors fd ON f.id = fd.film_id
                LEFT JOIN directors d2 ON fd.director_id = d2.id
                WHERE fd.director_id = ?
                GROUP BY f.id, f.name, f.description, f.release_date, f.duration, r.id, r.mpa_name
                """;

        if ("year".equals(sortBy)) {
            sql += " ORDER BY f.release_date";
        } else if ("likes".equals(sortBy)) {
            sql += " ORDER BY COUNT(DISTINCT fl.user_id) DESC";
        } else {
            throw new IllegalArgumentException("sortBy must be 'year' or 'likes'");
        }

        Collection<Film> films = findMany(sql, directorId);

        if (films.isEmpty()) {
            throw new NotFoundException("Director with id " + directorId + " not found");
        }

        return films;
    }

    @Override
    public Collection<Film> getRecommendations(Long userId) {
        try {
            String checkUserQuery = "SELECT COUNT(*) FROM users WHERE id = ?";
            Integer userCount = jdbc.queryForObject(checkUserQuery, Integer.class, userId);
            if (userCount == null || userCount == 0) {
                return Collections.emptyList();
            }

            String checkLikesQuery = "SELECT COUNT(*) FROM film_likes WHERE user_id = ?";
            Integer likeCount = jdbc.queryForObject(checkLikesQuery, Integer.class, userId);

            if (likeCount == null || likeCount == 0) {
                return Collections.emptyList();
            }

            return findMany(FIND_RECOMMENDATIONS_QUERY, userId, userId, userId);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public void deleteFilm(Long id) {
        update(DELETE_FILM_QUERY, id);
    }

    @Override
    public Collection<Film> getCommonFilms(Long userId, Long friendId) {
        String findCommonFilms = """
                        SELECT
                            f.*,
                            r.mpa_name,
                            (SELECT STRING_AGG(CONCAT(g.id, ':', g.name), ',')
                             FROM films_genre fg
                             JOIN genres g ON fg.genre_id = g.id
                             WHERE fg.film_id = f.id) as genres_data,
                            (SELECT STRING_AGG(CONCAT(d.id, ':', d.director_name), ',')
                             FROM film_directors fd
                             JOIN directors d ON fd.director_id = d.id
                             WHERE fd.film_id = f.id) as directors_data,
                            (SELECT STRING_AGG(fl.user_id::text, ',')
                             FROM film_likes fl
                             WHERE fl.film_id = f.id) as film_likes,
                            (SELECT COUNT(*)
                             FROM film_likes fl
                             WHERE fl.film_id = f.id) as likes_count
                        FROM films f
                        LEFT JOIN ratings r ON f.mpa_id = r.id
                        WHERE f.id IN (
                            SELECT film_id
                            FROM film_likes
                            WHERE user_id = ?
                            INTERSECT
                            SELECT film_id
                            FROM film_likes
                            WHERE user_id = ?
                        )
                        ORDER BY likes_count DESC;
                """;
        return findMany(findCommonFilms, userId, friendId);
    }

    @Override
    public Collection<Film> searchFilms(String query, String by) {
        if (query == null || query.isBlank() || by == null || by.isBlank()) {
            throw new IllegalArgumentException("query and by must be non-empty");
        }

        String[] byFields = Arrays.stream(by.toLowerCase().split(","))
                .map(String::trim)
                .toArray(String[]::new);

        boolean searchTitle = Arrays.asList(byFields).contains("title");
        boolean searchDirector = Arrays.asList(byFields).contains("director");

        if (!searchTitle && !searchDirector) {
            throw new IllegalArgumentException("by must contain 'title' or 'director'");
        }

        String likeQuery = "%" + query.toLowerCase() + "%";

        List<String> conditions = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        if (searchTitle) {
            conditions.add("LOWER(f.name) LIKE ?");
            params.add(likeQuery);
        }
        if (searchDirector) {
            conditions.add("LOWER(d.director_name) LIKE ?");
            params.add(likeQuery);
        }
        String whereClause = " WHERE " + String.join(" OR ", conditions);

        String sql = """
                SELECT DISTINCT
                    f.id,
                    f.name,
                    f.description,
                    f.release_date,
                    f.duration,
                    r.id AS mpa_id,
                    r.mpa_name,
                    STRING_AGG(DISTINCT g.id || ':' || g.name, ',') AS genres_data,
                    STRING_AGG(DISTINCT CAST(fl.user_id AS VARCHAR), ',') AS film_likes,
                    STRING_AGG(DISTINCT d.id || ':' || d.director_name, ',') AS directors_data,
                    COALESCE(lc.likes_count, 0) AS likes_count
                FROM films f
                LEFT JOIN ratings r ON f.mpa_id = r.id
                LEFT JOIN films_genre fg ON f.id = fg.film_id
                LEFT JOIN genres g ON fg.genre_id = g.id
                LEFT JOIN film_likes fl ON f.id = fl.film_id
                LEFT JOIN (
                    SELECT film_id, COUNT(user_id) AS likes_count
                    FROM film_likes
                    GROUP BY film_id
                ) lc ON f.id = lc.film_id
                LEFT JOIN film_directors fd ON f.id = fd.film_id
                LEFT JOIN directors d ON fd.director_id = d.id
                """ + whereClause + """
                GROUP BY f.id, f.name, f.description, f.release_date, f.duration, r.id, r.mpa_name, lc.likes_count
                ORDER BY likes_count DESC, f.name
                """;

        return findMany(sql, params.toArray());
    }

}
