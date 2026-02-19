package ru.yandex.practicum.filmorate.dto.film;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.ValidReleaseDate;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaName;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class NewFilmRequest {

    @NotBlank
    private String name;

    @Size(max = 200, message = "Description must be shorter than 200")
    private String description;

    @ValidReleaseDate
    private LocalDate releaseDate;

    @Positive
    private Integer duration;

    private List<Genre> genres = new ArrayList<>();

    private MpaName mpa;

    private List<Director> directors = new ArrayList<>(); //Добавлено
}
