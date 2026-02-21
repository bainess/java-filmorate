package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    private Long userId;
    private String eventType;
    private Long entityId;
    private String operation;

    private Long id;
    private Long timestamp;

}

