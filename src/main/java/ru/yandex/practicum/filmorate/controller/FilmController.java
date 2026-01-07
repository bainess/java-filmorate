package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public ResponseEntity<Collection<Film>> getFilms() {
        try {
            log.info("GET/films - Number of films: {}", films.size());
            return new ResponseEntity<>(films.values(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @PostMapping
    public ResponseEntity<Film> createFilm(@Valid @RequestBody Film film) {
        log.info("POST/films - Creating new film: {}", film.getName());
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Film {} was successfully created", film.getName());
        return new ResponseEntity<>(film, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film film) {

        Film oldFilm = films.get(film.getId());
        oldFilm.setName(film.getName());
        oldFilm.setDuration(film.getDuration());
        oldFilm.setDescription(film.getDescription());
        oldFilm.setReleaseDate(film.getReleaseDate());
        log.info("Film was successfully updated");
        return new ResponseEntity<>(oldFilm, HttpStatus.OK);
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