package ru.yandex.practicum.filmorate.storage.film.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
public class GenreStorageTest {
    @Autowired
    private GenreStorage genreStorage;

    @Test
    public void testGetRatings() {
        List<Integer> genres = genreStorage.getGenres();
        assertThat(genres).isNotEmpty();
    }
}
