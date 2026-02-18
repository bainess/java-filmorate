package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

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

    @GetMapping
    public ResponseEntity<Collection<FilmDto>> getFilms() {
        log.info("GET/films - Number of films: {}", filmService.getFilms().size());
        return new ResponseEntity<>(filmService.getFilms(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<FilmDto> createFilm(@Valid @RequestBody NewFilmRequest request) {
        log.info("POST/films - Creating new film: {}", request.getName());
        FilmDto newFilm = filmService.createFilm(request);
        log.info("Film {} was successfully created", request.getName());
        return new ResponseEntity<>(newFilm, HttpStatus.CREATED);
    }

    @GetMapping("/{filmId}")
    public ResponseEntity<FilmDto> getFilm(@PathVariable("filmId") Long filmId) {
        log.info("Film with " + filmService.getFilmById(filmId) + " was found");
        return new ResponseEntity<>(filmService.getFilmById(filmId), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<FilmDto> updateFilm(@Valid @RequestBody UpdateFilmRequest request) {
        log.info("Request film update{}", request);
        FilmDto updatedFilm = filmService.updateFilm(request.getId(), request);
        log.info("Film was successfully updated");
        return new ResponseEntity<>(updatedFilm, HttpStatus.OK);
    }

    @GetMapping("/popular")
    public ResponseEntity<Collection<Film>> getPopularFilms(@RequestParam(required = false) @Positive Integer count,
                                                            @RequestParam(required = false) Integer genreId,
                                                            @RequestParam(required = false) Integer year) {
        return new ResponseEntity<>(filmService.getPopularFilms(count, genreId, year), HttpStatus.OK);
    }

    @PutMapping("/{filmId}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void addLikes(@PathVariable("filmId") Long filmId, @PathVariable("userId") Long userId) {
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeLikes(@PathVariable("filmId") Long filmId, @PathVariable("userId") Long userId) {
        filmService.removeLike(filmId, userId);
    }

    // GET /films/director/{directorId}?sortBy=[year,likes] - список фильмов режиссёра с сортировкой
    @GetMapping("/director/{directorId}")
    public ResponseEntity<Collection<FilmDto>> getFilmsByDirector(
            @PathVariable("directorId") long directorId,
            @RequestParam(name = "sortBy", required = false, defaultValue = "year") String sortBy) {
        return new ResponseEntity<>(filmService.getFilmsByDirector(directorId, sortBy), HttpStatus.OK);
    }
}