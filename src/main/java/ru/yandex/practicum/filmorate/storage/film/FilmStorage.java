package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
Collection<Film> getFilms();

Film updateFilm(Film film);

Film createFilm(Film film);
}
