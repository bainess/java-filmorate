package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MpaName;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
public class MpaController {
    private final MpaService mpaService;

    @Autowired
    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping
    public ResponseEntity<List<MpaName>> getRatings() {
        return new ResponseEntity<>(mpaService.getMpaList(), HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<MpaName> getMpa(@PathVariable ("id") int id) {
        MpaName mpa = mpaService.getMpa(id);
        log.info("Rating by id{} is {}", id, mpa.getName());
        return new ResponseEntity<>(mpa,HttpStatus.OK);
    }
}
