package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;

import lombok.*;
import ru.yandex.practicum.filmorate.annotation.ValidReleaseDate;

import java.time.LocalDate;
import java.util.HashSet;
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

    private Set<Long> likes = new HashSet<>();

    public Long addLike(Long id) {
        if (likes == null) {
            likes = new HashSet<>();
        }
        likes.add(id);
        return id;
    }
}
