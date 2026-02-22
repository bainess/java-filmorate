package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.DbEventStorage.DbEventStorage;

import java.util.Collection;


@Service
@AllArgsConstructor
public class EventService {
    public final DbEventStorage eventStorage;

    public Collection<Event> getUserFeed(Long userId) {
        return eventStorage.getEvents(userId);
    }

    public Event createEvent(Long userId, String operation, String eventType, Long enityId) {
        Event event = Event.builder()
                .userId(userId)
                .entityId(enityId)
                .operation(operation)
                .eventType(eventType)
                .build();
        return eventStorage.saveEvent(event);
    }
}
