package ru.yandex.practicum.filmorate.storage.event;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.model.Event;

import java.time.Instant;
import java.util.Collection;

@Repository
public class EventStorage extends BaseRepository<Event> {
    public EventStorage(JdbcTemplate jdbc, RowMapper<Event> eventMapper) {
        super(jdbc, eventMapper);
    }

    public Long saveEvent(Event event) {
        final String insertEventQuery = "INSERT INTO EVENTS(" +
                    "ts," +
                    " user_id, " +
                    "operation_id, " +
                    "event_type_id, " +
                    "entity_id) " +
                "VALUES (" +
                    "CAST (? AS TIMESTAMP), " +
                    "?, " +
                    "(SELECT id FROM event_operations WHERE operation_name = ?), " +
                    "(SELECT id FROM event_types WHERE event_name = ?)," +
                    " ?)";

        Long id = insert(insertEventQuery,
                Instant.now(),
                event.getUserId(),
                event.getOperation(),
                event.getEventType(),
                event.getEntityId());
        return id;
    }

    public Collection<Event> getEventsByUser(Long userId) {
        String findEventsQuery = "SELECT " +
                    "e.id," +
                    " e.ts," +
                    " e.user_id," +
                    " e.entity_id, " +
                    "et.event_name, " +
                    "eo.operation_name " +
                "FROM events AS e " +
                "LEFT JOIN event_types AS et ON e.event_type_id = et.id " +
                "LEFT JOIN event_operations AS eo ON e.operation_id = eo.id" +
                " WHERE user_id = ?" +
                " ORDER BY e.ts";

        return findMany(findEventsQuery, userId);
    }
}
