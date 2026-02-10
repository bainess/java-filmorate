package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.db.GenreStorage;
import ru.yandex.practicum.filmorate.storage.film.db.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<FilmDto> getFilms() {
        return filmStorage.getFilms().stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public FilmDto createFilm(NewFilmRequest request) {
        Film film = FilmMapper.mapToFilm(request);
        film = filmStorage.createFilm(film);
        return FilmMapper.mapToFilmDto(film);
    }

    public FilmDto getFilmById(Long id) {
        return filmStorage.findFilm(id)
                .map(FilmMapper::mapToFilmDto)
                .orElseThrow(() -> new NotFoundException("Film not found"));
    }

    public FilmDto updateFilm(Long id, UpdateFilmRequest request) {
        Film updatedFilm = filmStorage.findFilm(id)
                .map(film -> FilmMapper.updateFilmFields(request, film))
                .orElseThrow(() -> new NotFoundException("Film not found"));
        updatedFilm = filmStorage.updateFilm(updatedFilm);
        return FilmMapper.mapToFilmDto(updatedFilm);
    }

    public void addLike(Long filmId, Long userId) {
        if (filmStorage.findFilm(filmId).isEmpty()) {
            throw new NotFoundException("Film " + filmId + " not found");
        }
        if (userStorage.getUser(userId).isEmpty()) {
            throw new NotFoundException("User " + userId + " not found");
        }
        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        if (filmStorage.findFilm(filmId).isEmpty()) {
            throw new NotFoundException("Film " + filmId + " not found");
        }
        if (userStorage.getUser(userId).isEmpty()) {
            throw new NotFoundException("User " + userId + " not found");
        }
        filmStorage.removeLike(filmId, userId);
    }

    public Collection<Film> getPopularFilms(int count) {
        return filmStorage.getFilms().stream()
                .sorted((film1, film2) -> film2.getLikes().size() - film1.getLikes().size())
                .limit(count)
                .toList();
    }
}


