package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;

@Slf4j
@RestController
@Validated
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Film> getFilm(@PathVariable Long id) {
        return new ResponseEntity<>(filmService.getFilm(id), HttpStatus.OK);
    }

    @PutMapping("/{filmId}/like/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addLike(@PathVariable Long filmId,
                        @PathVariable Long userId) {
        filmService.addLike(userId, filmId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void removeLike(@PathVariable Long filmId,
                           @PathVariable Long userId) {
        filmService.removeLike(userId, filmId);
    }

    @GetMapping("/popular")
    public ResponseEntity<Collection<Film>> getPopularFilms(@RequestParam(defaultValue = "10") @Positive int count) {
        return new ResponseEntity<>(filmService.getPopularFilms(count), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Collection<Film>> getFilms() {
        try {
            log.info("GET/films - Number of films: {}", filmService.getFilms().size());
            return new ResponseEntity<>(filmService.getFilms(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @PostMapping
    public ResponseEntity<Film> createFilm(@Valid @RequestBody Film film) {
        log.info("POST/films - Creating new film: {}", film.getName());
        Film newFilm = filmService.createFilm(film);
        log.info("Film {} was successfully created", film.getName());
        return new ResponseEntity<>(newFilm, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film film) {

        Film updatedFilm = filmService.updateFilm(film);
        log.info("Film was successfully updated");
        return new ResponseEntity<>(updatedFilm, HttpStatus.OK);
    }
}