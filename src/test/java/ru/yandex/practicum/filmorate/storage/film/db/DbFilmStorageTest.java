package ru.yandex.practicum.filmorate.storage.film.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaName;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.db.DbUserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
class DbFilmStorageTest {
    @Autowired
    private DbFilmStorage filmStorage;

    @Autowired
    private MpaStorage mpaStorage;

    @Autowired
    private DbUserStorage userStorage;

    private Film testFilm;
    private MpaName testMpa;
    private User testUser;

    @BeforeEach
    void setUp() {
        MpaName mpa = new MpaName(1);

        testFilm = new Film();
        testFilm.setName("Test Film");
        testFilm.setDescription("Test Description");
        testFilm.setReleaseDate(LocalDate.of(2020, 1, 1));
        testFilm.setDuration(120);
        testFilm.setMpa(mpa);
        // testFilm = filmDbStorage.createFilm(testFilm);

        testUser = new User();
        testUser.setEmail("testuser@test.com");
        testUser.setLogin("testuser");
        testUser.setName("Test User");
        testUser.setBirthday(LocalDate.of(1990, 1, 1));
        // testUser = userDbStorage.createUser(testUser);
    }

    @Test
    public void testFindFilmById() {
        Optional<Film> filmOptional = filmStorage.findFilm(1L);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    public void testGetFilms() {
        Collection<Film> films = filmStorage.getFilms();
        filmStorage.createFilm(testFilm);

        assertThat(films.size() == 2);
    }

    @Test
    public void testUpdateFilm() {
        testFilm = filmStorage.createFilm(testFilm);
        testFilm.setName("Updated Film " + System.currentTimeMillis());
        testFilm.setDescription("Updated Description");
        testFilm.setDuration(200);

        Film updated = filmStorage.updateFilm(testFilm);

        assertThat(updated.getName()).isEqualTo(testFilm.getName());
        assertThat(updated.getDescription()).isEqualTo("Updated Description");
        assertThat(updated.getDuration()).isEqualTo(200);

        Film fetched = filmStorage.findFilm(testFilm.getId()).get();
        assertThat(fetched.getName()).isEqualTo(testFilm.getName());
    }

    @Test
    public void testCreateFilm() {
        Film created = filmStorage.createFilm(testFilm);

        assertThat(created)
                .isNotNull()
                .extracting(Film::getName)
                .isNotNull();
        assertThat(created.getId()).isGreaterThan(0);
    }

    @Test
    public void testAddLike() {
        User newUser = userStorage.createUser(testUser);

        filmStorage.addLike(1L, 1L);
        Film film = filmStorage.findFilm(1L).get();
        assertThat(film.getLikes() == 1);

        filmStorage.addLike(1L, newUser.getId());
        film = filmStorage.findFilm(1L).get();
        assertThat(film.getLikes() == 2);
    }
}
