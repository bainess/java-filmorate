package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    private Long eventId;
    private Long userId;
    private String eventType;
    private Long timestamp;
    private Long entityId;
    private String operation;
}