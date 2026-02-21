package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Optional<Film> findFilm(Long id) {
        return Optional.of(films.get(id));
    }

    @Override
    public Collection<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film createFilm(Film film) {
        Film newFilm = Film.builder()
                .name(film.getName())
                .duration(film.getDuration())
                .releaseDate(film.getReleaseDate())
                .description(film.getDescription())
                .likes(film.getLikes())
                .build();
        newFilm.setId(getNextId());
        films.put(newFilm.getId(), newFilm);
        return newFilm;
    }

    @Override
    public Film updateFilm(Film film) {
        Film oldfilm = films.get(film.getId());
        oldfilm.setName(film.getName());
        oldfilm.setDescription(film.getDescription());
        oldfilm.setDuration(film.getDuration());
        oldfilm.setReleaseDate(film.getReleaseDate());
        return oldfilm;
    }

    private long getNextId() {
        long maxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++maxId;
    }

    public void addLike(Long filmId, Long userId) {
        films.get(filmId).addLike(userId);
    }

    public void removeLike(Long filmId, Long userId) {
        films.get(filmId).addLike(userId);
    }

    @Override
    public Collection<Film> getFilmsByDirector(long directorId, String sortBy) {
        return null;
    }

    public Collection<Film> getRecommendations(Long userId) {
        return new ArrayList<>();
    }

    @Override
    public void deleteFilm(Long id) {
        films.remove(id);
    }
}
