package ru.yandex.practicum.filmorate.storage.film.db;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.model.MpaName;

import java.util.List;
import java.util.Optional;

@Repository
public class MpaStorage extends BaseRepository<MpaName> {
    private static final String FIND_MPA_QUERY = "SELECT * FROM ratings";
    private static final String FIND_GET_MPA_QUERY = "SELECT * FROM ratings WHERE id =?";

    public MpaStorage(JdbcTemplate jdbc, RowMapper<MpaName> mapper) {
        super(jdbc, mapper);
    }

    public List<MpaName> getRatings() {
        return findMany(FIND_MPA_QUERY);
    }

    public Optional<MpaName> getRating(int id) {
        return findOne(FIND_GET_MPA_QUERY, id);
    }
}
