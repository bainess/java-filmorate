package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.model.comparator.FilmComparatorByLikes;
import java.util.*;

@Service
public class FilmService {
    FilmComparatorByLikes comparator = new FilmComparatorByLikes();
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

    public Collection<Film> getFilms() { return filmStorage.getFilms();}

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public List<User> getLikes(Long id) {
        Set<Long> usersLiked = filmStorage.getFilm(id).getLikes();

       return userStorage.getUsers().stream().filter(user -> usersLiked.contains(user.getId())).toList();
    }

    public int addLike(Long userId, Long filmId) {
        filmStorage.getFilm(filmId).addLike(userId);
        return filmStorage.getFilm(filmId).getLikes().size();
    }

    public int removeLike(Long userId, Long filmId) {
        filmStorage.getFilm(filmId).getLikes().remove(userId);
        return filmStorage.getFilm(filmId).getLikes().size();
    }

    public Collection<Film> getPopularFilms(int count) {
        return filmStorage.getFilms().stream()
                // без фильтра фильмы без лайков при сравнении падают в NullPointerException
                .filter(film -> film.getLikes() != null)
                .sorted(comparator)
                .limit(count)
                .toList();
    }
}


