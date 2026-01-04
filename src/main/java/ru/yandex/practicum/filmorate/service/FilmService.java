package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
        ComparatorByLikes comparator = new ComparatorByLikes();
        filmStorage.getFilm(1L).getLikes();
        return filmStorage.getFilms().stream()
                .filter(film -> film.getLikes() != null)
                .sorted(comparator)
                .limit(count)
                .toList();
    }
    static class ComparatorByLikes implements Comparator<Film> {
        @Override
        public int compare(Film film1, Film film2) {
            return film2.getLikes().size() - film1.getLikes().size();
        }
    }
}


