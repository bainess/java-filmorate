package ru.yandex.practicum.filmorate.dto.film;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.ValidReleaseDate;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class FilmDto {
    private Long id;

    @NotBlank
    private String name;

    @Size(max = 500, message = "Description must be shoter than 200")
    private String description;

    @ValidReleaseDate
    private LocalDate releaseDate;

    @Positive
    private Integer duration;

    @NotBlank
    private String rating;

    @NotBlank
    private String genre;

    private Set<Long> likes = new HashSet<>();
}
