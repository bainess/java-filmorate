package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;

import java.util.Collection;

@Service
public class EventService {
    private final EventStorage eventStorage;

    @Autowired
    public EventService(EventStorage eventStorage) {
        this.eventStorage = eventStorage;
    }

    public Event createEvent(Long userId, Integer eventTypeId, Integer operationId) {
        Event event = new Event();
    }

    public Collection<Event> getEvents(Long userId) {
       return eventStorage.getEventsByUser(userId);
    }
}
