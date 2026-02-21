package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final EventService eventService;

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
        eventService.createEvent(userId, "LIKE", "ADD", filmId);
    }

    public void removeLike(Long filmId, Long userId) {
        if (filmStorage.findFilm(filmId).isEmpty()) {
            throw new NotFoundException("Film " + filmId + " not found");
        }
        if (userStorage.getUser(userId).isEmpty()) {
            throw new NotFoundException("User " + userId + " not found");
        }
        filmStorage.removeLike(filmId, userId);
        eventService.createEvent(userId, "LIKE", "REMOVE", filmId);
    }

    public Collection<Film> getPopularFilms(Integer count, Integer genreId, Integer year) {
        List<Film> films = filmStorage.getFilms().stream()
                .filter(film -> genreId == null || film.getGenres().stream().anyMatch(genre -> genre.getId() == genreId))
                .filter(film -> year == null || film.getReleaseDate().getYear() == year)
                .sorted((film1, film2) -> film2.getLikes().size() - film1.getLikes().size())
                .toList();

        if (count != null) {
            films = films.stream().limit(count).toList();
        }
        return films;
    }
}


