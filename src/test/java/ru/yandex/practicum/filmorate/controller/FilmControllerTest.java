package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

public class FilmControllerTest {
    private  FilmController controller;

    @BeforeEach
    void setUp() {
        controller = new FilmController();
    }

    @Test
    void shouldCreateFilmAllValidField() {
        Film film = Film.builder()
                .name("Film")
                .description("Film description")
                .releaseDate(LocalDate.now())
                .duration(30)
                .build();

        controller.createFilm(film);
        Assertions.assertEquals(film.getName(),controller.getFilms().stream().findFirst().get().getName());
        Assertions.assertEquals(film.getDescription(),controller.getFilms().stream().findFirst().get().getDescription());
        Assertions.assertEquals(film.getReleaseDate(),controller.getFilms().stream().findFirst().get().getReleaseDate());
        Assertions.assertEquals(film.getDuration(),controller.getFilms().stream().findFirst().get().getDuration());
    }

    @Test
    void shouldNoTCreateFilmNameInvalid() {
        Film film = Film.builder()
                .name("")
                .description("Film description")
                .releaseDate(LocalDate.now())
                .duration(30)
                .build();

        controller.createFilm(film);
        Assertions.assertNotEquals(0,controller.getFilms().size());
    }


    @Test
    void shouldUpdateFilm() {

        Film film = Film.builder()
                .name("Film")
                .description("Film description")
                .releaseDate(LocalDate.of(1990, 11,12))
                .duration(30)
                .build();

        Film film2 = Film.builder()
                .id(1L)
                .name("Film 2 ")
                .description("Film description 2 ")
                .releaseDate(LocalDate.of(1995, 5,6))
                .duration(90)
                .build();

        controller.createFilm(film);
        controller.updateFilm(film2);

        Assertions.assertEquals(film2, controller.getFilms().stream().findFirst().get());
    }

    @Test
    void shouldReturnAllFilms() {
        Film film = Film.builder()
                .name("Film")
                .description("Film description")
                .releaseDate(LocalDate.of(1990, 11,12))
                .duration(30)
                .build();

        Film film2 = Film.builder()
                .id(1L)
                .name("Film 2 ")
                .description("Film description 2 ")
                .releaseDate(LocalDate.of(1995, 5,6))
                .duration(90)
                .build();

        controller.createFilm(film);
        controller.createFilm(film2);

        Assertions.assertEquals(2, controller.getFilms().size());
    }
}
