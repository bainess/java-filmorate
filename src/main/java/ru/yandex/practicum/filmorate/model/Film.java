package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;

import lombok.*;
import ru.yandex.practicum.filmorate.annotation.ValidReleaseDate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
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

    @NotEmpty
    private List<Director> directors = new ArrayList<>();   // Добавлено

    private List<Genre> genres = new ArrayList<>();

    private List<Long> likes = new ArrayList<>();

    public void addLikes(List<Long> userIds) {
        likes = userIds;
    }

    public List<Long> showLikes() {
        return likes;
    }

    public void addLike(Long userId) {
        likes.add(userId);
    }
}