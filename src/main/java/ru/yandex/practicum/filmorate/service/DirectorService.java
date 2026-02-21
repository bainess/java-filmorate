package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.film.db.DirectorStorage;

import java.util.List;

@Service
public class DirectorService {
    private final DirectorStorage directorStorage;

    public DirectorService(DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    // Получение всех режиссёров
    public List<Director> getAllDirectors() {
        return directorStorage.getAllDirectors();
    }

    // Получение режиссёра по id
    public Director getDirectorById(long id) {
        return directorStorage.getDirectorById(id)
                .orElseThrow(() -> new NotFoundException("Director with id " + id + " not found"));
    }

    // Создание режиссёра
    public Director createDirector(Director director) {
        return directorStorage.createDirector(director);
    }

    // Обновление режиссёра
    public Director updateDirector(Director director) {
        if (directorStorage.getDirectorById(director.getId()).isEmpty()) {
            throw new NotFoundException("Director with id " + director.getId() + " not found");
        }
        return directorStorage.updateDirector(director);
    }

    // Удаление режиссёра
    public void deleteDirector(long id) {
        if (directorStorage.getDirectorById(id).isEmpty()) {
            throw new NotFoundException("Director with id " + id + " not found");
        }
        directorStorage.deleteDirector(id);
    }
}