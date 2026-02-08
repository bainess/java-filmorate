package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {

    Optional<Film> findFilm(Long id);

    Collection<Film> getFilms();

    Film updateFilm(Film film);

    Film createFilm(Film film);

    void addLike(Long filmId, Long userId);
}
