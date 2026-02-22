package ru.yandex.practicum.filmorate.storage.DbEventStorage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.model.Event;

import java.time.Instant;
import java.util.Collection;

@Repository
public class DbEventStorage extends BaseRepository<Event> {

    public DbEventStorage(JdbcTemplate jdbc, RowMapper<Event> mapper) {
        super(jdbc, mapper);
    }

    public Collection<Event> getEvents(Long userId) {
        String findUserEventsQuery = """
                SELECT\s
                    e.id,\s
                    e.ts,\s
                    e.user_id,\s
                    e.entity_id,\s
                    et.event_name,\s
                    eo.operation_name\s
                FROM events AS e\s
                LEFT JOIN event_types AS et ON e.event_type_id = et.id\s
                LEFT JOIN event_operations AS eo ON e.operation_id = eo.id\s
                WHERE user_id = ?\s
                ORDER BY e.ts""";

        return findMany(findUserEventsQuery, userId);
    }

    public Event saveEvent(Event event) {
        String saveEventQuery = """
                 INSERT INTO EVENTS\s
                     (ts,\s
                     user_id,\s
                     operation_id,\s
                     event_type_id, \s
                     entity_id)\s
                 VALUES \s
                     (CAST (? AS TIMESTAMP),\s
                     ?,
                     (SELECT id FROM event_operations WHERE operation_name = ?),\s
                     (SELECT id FROM event_types WHERE event_name = ?),
                     ?);
                \s""";
        Long id = insert(saveEventQuery,
                Instant.now(),
                event.getUserId(),
                event.getOperation(),
                event.getEventType(),
                event.getEntityId());
        event.setId(id);
        return event;
    }
}
