package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getFilms() {
        log.info("GET/films - Number of films: {}", films.size());
        return films.values();
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("POST/films - Creating new film: {}", film.getName());
//        if (film.getName() == null || film.getName().isEmpty() || film.getName().isBlank()) {
//            log.error("Film creation failed: Title is required: {}", film);
//            throw new ValidationException("Title is required");
//        }
//
//        if (film.getDescription().length() > 200) {
//            log.warn("Film creation failed: Description for film {} is too long: {}", film.getName(), film.getDescription().length());
//            throw new ValidationException("Max length is 200 symbols");
//        }
//
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Film creation failed: Film {} Invalid release date: {}", film.getName(), film.getReleaseDate());
            throw new ValidationException("Release date should be set after 28 Dec 1895");
        }
//
//        if (film.getDuration().isNegative() || film.getDuration().isZero()) {
//            log.warn("Film creation failed: Description for film {} is too long: {}", film.getName(), film.getDescription().length());
//            throw new ValidationException("Film length should be positive");
//        }

        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Film {} was successfully created", film.getName());
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
//        if (film.getName() == null || film.getName().isBlank()) {
//            log.error("Film is required");
//            throw new ValidationException("Title is required");
//        }
//
//        if (film.getDescription().length() > 200) {
//            log.warn("Film creation failed: Description for film {} is too long: {}", film.getName(), film.getDescription().length());
//            throw new ValidationException("Max length is 200 symbols");
//        }
//
//        if (film.getDuration().isNegative() || film.getDuration().isZero()) {
//            log.warn("Film creation failed: Description for film {} is too long: {}", film.getName(), film.getDescription().length());
//            throw new ValidationException("Film length should be positive");
//        }

        Film oldFilm = films.get(film.getId());
        oldFilm.setName(film.getName());
        oldFilm.setDuration(film.getDuration());
        oldFilm.setDescription(film.getDescription());
        oldFilm.setReleaseDate(film.getReleaseDate());
        log.info("Film was successfully updated");
        return oldFilm;
    }

    public long getNextId() {
        long maxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++maxId;
    }
}
