package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaName;
import ru.yandex.practicum.filmorate.storage.film.db.MpaStorage;

import java.util.List;

@Service
public class MpaService {
    private final MpaStorage mpaStorage;

    public MpaService(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }
    public MpaName getMpa(int id) {
        if (!mpaStorage.getRatings().stream().map(MpaName::getId).toList().contains(id)) {
            throw new NotFoundException("mpa with id " + id + " not found");
        }
        return mpaStorage.getRating(id).get();
    }

    public List<MpaName> getMpaList() {
        return mpaStorage.getRatings();
    }
}
