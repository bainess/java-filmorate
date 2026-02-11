package ru.yandex.practicum.filmorate.dto.film;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.ValidReleaseDate;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaName;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class UpdateFilmRequest {

    private long id;

    @NotBlank
    private String name;

    @Size(max = 500, message = "Description must be shoter than 200")
    private String description;

    @ValidReleaseDate
    private LocalDate releaseDate;

    @Positive
    private Integer duration;


    private MpaName mpa;


    private List<Genre> genres = new ArrayList<>();

    private List<Long> likes = new ArrayList<>();

    public boolean hasName() {
        return ! (name == null || name.isBlank());
    }

    public boolean hasDescription() {
        return ! (name == null || name.isBlank());
    }

    public boolean hasReleaseDate() {
        return ! (releaseDate == null);
    }

    public boolean hasDuration() {
        return ! (duration == null || duration < 0);
    }

    public boolean hasMpa() {
        return ! (mpa == null);
    }

    public boolean hasGenre() {
        return ! (genres.isEmpty());
    }

    public boolean hasLikes() {
        return ! (likes == null);
    }
}
