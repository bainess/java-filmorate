package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;

import lombok.*;
import ru.yandex.practicum.filmorate.annotation.ValidReleaseDate;
//import ru.yandex.practicum.filmorate.annotation.ValidReleaseDate;

import java.time.LocalDate;

/**
 * Film.
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Film {

    private Long id;

    @NotBlank
    private String name;

    @Size(max = 200, message = "Description must be shoter than 200")
    private String description;

    @ValidReleaseDate
    private LocalDate releaseDate;

    @Positive
    private Integer duration;
}
