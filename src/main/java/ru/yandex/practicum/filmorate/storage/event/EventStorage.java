package ru.yandex.practicum.filmorate.storage.event;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

@Repository
public class EventStorage extends BaseRepository<Event> {
    public EventStorage(JdbcTemplate jdbc, RowMapper<Event> eventMapper) {
        super(jdbc, eventMapper);
    }

    public Long saveEvent(Event event) {
        String INSERT_EVENT_QUERY = "INSERT INTO events (ts, user_id, event_type_id, operation_id, entity_id) VALUES (?, ?, ?, ?)";

        Long id = insert(INSERT_EVENT_QUERY,
                event.getTimestamp(),
                event.getUserId(),
                event.getEventType(),
                event.getOperationId(),
                event.getEntityId());
        return id;
    }

    public Collection<Event> getEventsByUser(Long userId) {
       return null;


    };
}
