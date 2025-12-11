package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Film.
 */

@Data
@Builder
public class Film {

    long id;

    @NotBlank
    String name;

    @Size(max = 200, message = "Description must be shoter than 200")
    String description;
    LocalDate releaseDate;

    @Positive
    int duration;
}
