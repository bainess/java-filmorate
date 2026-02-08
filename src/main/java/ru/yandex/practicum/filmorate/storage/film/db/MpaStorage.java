package ru.yandex.practicum.filmorate.storage.film.db;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.model.MpaName;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class MpaStorage extends BaseRepository<MpaName> {
    private static final String FIND_MPA_QUERY = "SELECT id FROM ratings";

    public MpaStorage(JdbcTemplate jdbc, RowMapper<MpaName> mapper) {
        super(jdbc, mapper);
    }

    public List<Integer> getRatings() {
        return findMany(FIND_MPA_QUERY).stream().map(MpaName::getId).collect(Collectors.toList());
    }
}
