package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/directors")
public class DirectorController {
    private final DirectorService directorService;

    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    // GET /directors - список всех режиссёров
    @GetMapping
    public ResponseEntity<List<Director>> getAllDirectors() {
        return new ResponseEntity<>(directorService.getAllDirectors(), HttpStatus.OK);
    }

    // GET /directors/{id} - получение режиссёра по id
    @GetMapping("/{id}")
    public ResponseEntity<Director> getDirector(@PathVariable("id") long id) {
        return new ResponseEntity<>(directorService.getDirectorById(id), HttpStatus.OK);
    }

    // POST /directors - создание режиссёра
    @PostMapping
    public ResponseEntity<Director> createDirector(@RequestBody Director director) {
        Director created = directorService.createDirector(director);
        log.info("Director {} was created", created.getName());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // PUT /directors - обновление режиссёра
    @PutMapping
    public ResponseEntity<Director> updateDirector(@RequestBody Director director) {
        Director updated = directorService.updateDirector(director);
        log.info("Director {} was updated", updated.getName());
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    // DELETE /directors/{id} - удаление режиссёра
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDirector(@PathVariable("id") long id) {
        directorService.deleteDirector(id);
        log.info("Director with id {} was deleted", id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}