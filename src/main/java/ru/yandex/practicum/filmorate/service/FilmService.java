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
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final EventService eventService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage, EventService eventService) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.eventService = eventService;
    }

    public Collection<FilmDto> getFilms(Integer genre, Integer year) {
        return filmStorage.getFilms(genre, year).stream()
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
        eventService.createEvent(userId, "ADD", "LIKE", filmId);
        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        if (filmStorage.findFilm(filmId).isEmpty()) {
            throw new NotFoundException("Film " + filmId + " not found");
        }
        if (userStorage.getUser(userId).isEmpty()) {
            throw new NotFoundException("User " + userId + " not found");
        }
        eventService.createEvent(userId, "REMOVE", "LIKE", filmId);
        filmStorage.removeLike(filmId, userId);
    }

    public Collection<Film> getPopularFilms(Integer count, Integer genreId, Integer year) {
        Collection<Film> films = filmStorage.getFilms(genreId, year);

        if (count != null) {
            films = films.stream().limit(count).toList();
        }
        return films;
    }

    public Collection<FilmDto> getFilmsByDirector(long directorId, String sortBy) {
        return filmStorage.getFilmsByDirector(directorId, sortBy).stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public void deleteFilm(Long id) {
        filmStorage.findFilm(id)
                .orElseThrow(() -> new NotFoundException("Film with id " + id + " not found"));
        filmStorage.deleteFilm(id);
    }

    public Collection<FilmDto> getCommonFilms(Long userId, Long friendId) {
        return filmStorage.getCommonFilms(userId, friendId).stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public Collection<FilmDto> searchFilms(String query, String by) {
        return filmStorage.searchFilms(query, by).stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }
}
