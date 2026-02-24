package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    Long eventId;
    Long userId;
    String eventType;
    Long timestamp;
    Long entityId;
    String operation;
}