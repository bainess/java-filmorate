package ru.yandex.practicum.filmorate.storage.film.db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaName;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.db.DbUserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
class DbFilmStorageTest {
    @Autowired
    private DbFilmStorage filmStorage;

    @Autowired
    private DbUserStorage userStorage;

    private Film testFilm;
    private User testUser;

    @BeforeEach
    void setUp() {
        MpaName mpa = new MpaName();
        mpa.setId(1);

        testFilm = new Film();
        testFilm.setName("Test Film");
        testFilm.setDescription("Test Description");
        testFilm.setReleaseDate(LocalDate.of(2020, 1, 1));
        testFilm.setDuration(120);
        testFilm.setMpa(mpa);

        testUser = new User();
        testUser.setEmail("testuser@test.com");
        testUser.setLogin("testuser");
        testUser.setName("Test User");
        testUser.setBirthday(LocalDate.of(1990, 1, 1));
    }

    @Test
    public void testFindFilmById() {
        filmStorage.createFilm(testFilm);
        Optional<Film> userOptional = filmStorage.findFilm(1L);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
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
        filmStorage.createFilm(testFilm);
        User newUser = userStorage.createUser(testUser);

        filmStorage.addLike(1L, 1L);
        Film film = filmStorage.findFilm(1L).get();

        assertThat(film.getLikes()).hasSize(1);

        filmStorage.addLike(1L, newUser.getId());
        film = filmStorage.findFilm(1L).get();

        assertThat(film.getLikes().size()).isEqualTo(2);
    }
}
