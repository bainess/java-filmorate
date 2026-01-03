package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final InMemoryFilmStorage filmStorage;

    @Autowired
    public FilmController(InMemoryFilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    @GetMapping
    public ResponseEntity<Collection<Film>> getFilms() {
        try {
            log.info("GET/films - Number of films: {}", filmStorage.getFilms().size());
            return new ResponseEntity<>(filmStorage.getFilms(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @PostMapping
    public ResponseEntity<Film> createFilm(@Valid @RequestBody Film film) {
        log.info("POST/films - Creating new film: {}", film.getName());
        Film newFilm = filmStorage.createFilm(film);
        log.info("Film {} was successfully created", film.getName());
        return new ResponseEntity<>(newFilm, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film film) {

        Film updatedFilm = filmStorage.updateFilm(film);
        log.info("Film was successfully updated");
        return new ResponseEntity<>(updatedFilm, HttpStatus.OK);
    }
}