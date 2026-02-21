package ru.yandex.practicum.filmorate.storage.film.db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.*;

import ru.yandex.practicum.filmorate.storage.user.db.DbUserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
class DbFilmStorageTest {

    @Autowired
    private DbFilmStorage filmStorage;

    @Autowired
    private DbUserStorage userStorage;

    @Autowired
    private DirectorStorage directorStorage;

    private Film testFilm;
    private Director testDirector;
    private User testUser;

    @BeforeEach
    void setUp() {
        // создаём режиссёра
        testDirector = new Director();
        testDirector.setName("Test Director");
        testDirector = directorStorage.createDirector(testDirector);

        MpaName mpa = new MpaName();
        mpa.setId(1);

        testFilm = new Film();
        testFilm.setName("Test Film");
        testFilm.setDescription("Test Description");
        testFilm.setReleaseDate(LocalDate.of(2020, 1, 1));
        testFilm.setDuration(120);
        testFilm.setMpa(mpa);
        testFilm.setDirectors(List.of(testDirector)); // ОБЯЗАТЕЛЬНО

        testUser = new User();
        testUser.setEmail("testuser@test.com");
        testUser.setLogin("testuser");
        testUser.setName("Test User");
        testUser.setBirthday(LocalDate.of(1990, 1, 1));
    }

    @Test
    void testCreateFilm() {
        Film created = filmStorage.createFilm(testFilm);

        assertThat(created).isNotNull();
        assertThat(created.getId()).isGreaterThan(0);
        assertThat(created.getDirectors()).isNotEmpty();
    }

    @Test
    void testFindFilmById() {
        Film created = filmStorage.createFilm(testFilm);

        Optional<Film> optionalFilm = filmStorage.findFilm(created.getId());

        assertThat(optionalFilm).isPresent();
        assertThat(optionalFilm.get().getId()).isEqualTo(created.getId());
    }

    @Test
    void testGetFilms() {
        filmStorage.createFilm(testFilm);

        var films = filmStorage.getFilms();

        assertThat(films).isNotEmpty();
    }

    @Test
    void testUpdateFilm() {
        Film created = filmStorage.createFilm(testFilm);

        created.setName("Updated Film");
        created.setDescription("Updated Description");
        created.setDuration(200);

        filmStorage.updateFilm(created);

        Film updated = filmStorage.findFilm(created.getId()).get();

        assertThat(updated.getName()).isEqualTo("Updated Film");
        assertThat(updated.getDescription()).isEqualTo("Updated Description");
        assertThat(updated.getDuration()).isEqualTo(200);
    }

    @Test
    void testAddLike() {
        Film created = filmStorage.createFilm(testFilm);

        User createdUser = userStorage.createUser(testUser);

        filmStorage.addLike(created.getId(), createdUser.getId());

        Film filmWithLike = filmStorage.findFilm(created.getId()).get();

        assertThat(filmWithLike.getLikes()).hasSize(1);
    }

    @Test
    void testGetFilmsByDirectorSortedByYear() {
        filmStorage.createFilm(testFilm);

        var films = filmStorage.getFilmsByDirector(testDirector.getId(), "year");

        assertThat(films).isNotEmpty();
    }

    @Test
    void testGetFilmsByDirectorSortedByLikes() {
        Film created = filmStorage.createFilm(testFilm);
        User createdUser = userStorage.createUser(testUser);

        filmStorage.addLike(created.getId(), createdUser.getId());

        var films = filmStorage.getFilmsByDirector(testDirector.getId(), "likes");

        assertThat(films).isNotEmpty();
    }
}