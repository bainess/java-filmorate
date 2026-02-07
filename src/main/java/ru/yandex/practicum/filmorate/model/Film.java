package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;

import lombok.*;
import ru.yandex.practicum.filmorate.annotation.ValidReleaseDate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    private MpaName mpa;
    private List<Genre> genres = new ArrayList<>();
    private int likes;
}
