package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;


import java.util.Collection;

@Service
@AllArgsConstructor
public class EventService {
    private final EventStorage eventStorage;

    public Event createEvent(Long userId, String eventTypeId, String operation, Long entityId) {
        Event event = Event.builder()
                .userId(userId)
                .eventType(eventTypeId.toUpperCase())
                .operation(operation.toUpperCase())
                .entityId(entityId)
                .build();
        System.out.println(event.getTimestamp());
        Long id = eventStorage.saveEvent(event);
        event.setId(id);
        return event;
    }

    public Collection<Event> getEvents(Long userId) {
       return eventStorage.getEventsByUser(userId);
    }
}
