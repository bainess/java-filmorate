package ru.yandex.practicum.filmorate.model;

import lombok.*;

import java.sql.Timestamp;
import java.time.Instant;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {
    private Long id;
    private Timestamp timestamp = Timestamp.valueOf(String.valueOf(Instant.now()));
    private Long userId;
    private String eventType;
    private Long entityId;
    private Integer operationId;
}

