package ru.yandex.practicum.filmorate.storage.film.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.MpaName;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
public class MpaStorageTest {
    @Autowired
    private MpaStorage mpaStorage;

    @Test
    public void testGetRatings() {
        List<MpaName> ageRatings = mpaStorage.getRatings();
        assertThat(ageRatings).isNotEmpty();
    }
}
