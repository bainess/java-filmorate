package ru.yandex.practicum.filmorate.dal.mappers.user;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@Component
public class EventRowMapper implements RowMapper<Event> {

    @Override
    public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
        Event event = new Event();
        event.setId(rs.getLong("id"));
        event.setUserId(rs.getLong("user_id"));
        event.setEntityId(rs.getLong("entity_id"));
        event.setOperation(rs.getString("operation_name"));
        event.setEventType(rs.getString("event_name"));

        Timestamp ts = rs.getTimestamp("ts");
        event.setTimestamp(ts.toInstant().toEpochMilli());
        return event;
    }
}
