package ru.yandex.practicum.filmorate.storage.film.db;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

//Новый класс
@Repository
public class DirectorStorage extends BaseRepository<Director> {

    private static final String FIND_ALL_DIRECTORS = "SELECT * FROM directors ORDER BY id";
    private static final String FIND_DIRECTOR_BY_ID = "SELECT * FROM directors WHERE id = ?";
    private static final String INSERT_DIRECTOR = "INSERT INTO directors(director_name) VALUES(?)";
    private static final String UPDATE_DIRECTOR = "UPDATE directors SET director_name=? WHERE id=?";
    private static final String DELETE_DIRECTOR = "DELETE FROM directors WHERE id=?";

    public DirectorStorage(JdbcTemplate jdbc, RowMapper<Director> mapper) {
        super(jdbc, mapper);
    }

    public List<Director> getAllDirectors() {
        return findMany(FIND_ALL_DIRECTORS);
    }

    public Optional<Director> getDirectorById(long id) {
        return findOne(FIND_DIRECTOR_BY_ID, id);
    }

    public Director createDirector(Director director) {
        long id = insert(INSERT_DIRECTOR, director.getName());
        director.setId(id);
        return director;
    }

    public Director updateDirector(Director director) {
        if (!getDirectorById(director.getId()).isPresent()) {
            throw new NotFoundException("Director not found");
        }
        update(UPDATE_DIRECTOR, director.getName(), director.getId());
        return director;
    }

    public void deleteDirector(long id) {
        if (!getDirectorById(id).isPresent()) {
            throw new NotFoundException("Director not found");
        }
        update(DELETE_DIRECTOR, id);
    }
}