package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;

public class FilmServiceTest {
    FilmService filmService;
    FilmStorage filmStorage = new InMemoryFilmStorage();
    UserStorage userStorage = new InMemoryUserStorage();

    @BeforeEach
   void setUp() {
        filmService = new FilmService(filmStorage, userStorage);
        filmStorage.createFilm(Film.builder()
                .name("One")
                .description("Description 1")
                .releaseDate(LocalDate.of(1990,11,12))
                .duration(30)
                .build());
        filmStorage.createFilm(Film.builder()
                .name("Two")
                .description("Description 2")
                .releaseDate(LocalDate.of(1999,1,1))
                .duration(40)
                .build());
        filmStorage.createFilm(Film.builder()
                .name("Three")
                .description("Description 3")
                .releaseDate(LocalDate.of(1955,11,12))
                .duration(30)
                .build());
        filmStorage.createFilm(Film.builder()
                .name("Four")
                .description("Description 4")
                .releaseDate(LocalDate.of(1960,11,12))
                .duration(60)
                .build());
        filmStorage.createFilm(Film.builder()
                .name("Five")
                .description("Description")
                .releaseDate(LocalDate.of(1990,11,12))
                .duration(30)
                .build());
        filmStorage.createFilm(Film.builder()
                .name("Six")
                .description("Description")
                .releaseDate(LocalDate.of(1990,11,12))
                .duration(30)
                .build());
        filmStorage.createFilm(Film.builder()
                .name("Seven")
                .description("Description")
                .releaseDate(LocalDate.of(1990,11,12))
                .duration(30)
                .build());

        userStorage.createUser(User.builder().name("Oncy").build());
        userStorage.createUser(User.builder().name("Twicy").build());
        userStorage.createUser(User.builder().name("Thricy").build());
        userStorage.createUser(User.builder().name("Fourthy").build());
        userStorage.createUser(User.builder().name("Fifthy").build());
        userStorage.createUser(User.builder().name("Sixthy").build());
    }

    @Test
    void shouldSortFilmsByLikes() {
        //film 1
        filmService.addLike(1L, 1L);
        //film 2
        filmService.addLike(1L, 2L);
        filmService.addLike(2L, 2L);
        // film 3
        filmService.addLike(1L, 3L);
        filmService.addLike(2L, 3L);
        filmService.addLike(3L, 3L);
        // film 4
        filmService.addLike(1L, 4L);
        filmService.addLike(2L, 4L);
        filmService.addLike(3L, 4L);
        filmService.addLike(4L, 4L);
        filmService.addLike(5L, 4L);
        // film 5
        filmService.addLike(1L, 5L);
        filmService.addLike(1L, 5L);
        filmService.addLike(1L, 5L);
        filmService.addLike(1L, 5L);
        // film 6
        filmService.addLike(1L, 6L);
        filmService.addLike(2L, 6L);
        // film 7
        filmService.addLike(1L, 1L);
        filmService.addLike(2L, 1L);
        filmService.addLike(3L, 1L);

        int count = 1;
        Assertions.assertEquals(filmService.getPopularFilms(count).stream().findFirst().get().getLikes().size(), 5);
    }
}
