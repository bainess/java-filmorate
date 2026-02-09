package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.util.*;

@Service
public class FilmService {
    private FilmStorage filmStorage;
    private UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film getFilm(Long id) {
        return filmStorage.getFilm(id);
    }

    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        if (filmStorage.getFilm(film.getId()) == null) {
            throw new NotFoundException("Film not found");
        }
        return filmStorage.updateFilm(film);
    }

    public List<User> getLikes(Long id) {
        Set<Long> usersLiked = filmStorage.getFilm(id).getLikes();

       return userStorage.getUsers().stream().filter(user -> usersLiked.contains(user.getId())).toList();
    }

    public int addLike(Long userId, Long filmId) {
        if (filmStorage.getFilm(filmId) == null) {
            throw new NotFoundException("Film " + filmId + " not found");
        }
        if (userStorage.getUser(userId) == null) {
            throw new NotFoundException("User " + userId + " not found");
        }
        filmStorage.getFilm(filmId).addLike(userId);
        return filmStorage.getFilm(filmId).getLikes().size();
    }

    public int removeLike(Long userId, Long filmId) {
        if (filmStorage.getFilm(filmId) == null) {
            throw new NotFoundException("Film " + filmId + " not found");
        }
        if (userStorage.getUser(userId) == null) {
            throw new NotFoundException("User " + userId + " not found");
        }
        filmStorage.getFilm(filmId).getLikes().remove(userId);
        return filmStorage.getFilm(filmId).getLikes().size();
    }

    public Collection<Film> getPopularFilms(int count) {
        return filmStorage.getFilms().stream()
                .sorted((film1, film2) -> film2.getLikes().size() - film1.getLikes().size())
                .limit(count)
                .toList();
    }
}


