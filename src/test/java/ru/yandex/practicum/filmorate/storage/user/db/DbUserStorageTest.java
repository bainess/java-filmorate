package ru.yandex.practicum.filmorate.storage.user.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
class DbUserStorageTest {
    @Autowired
    private DbUserStorage userStorage;

    private User testUser;
    private User friendUser;

    @Test
    public void testGetUser() {
        Optional<User> userOptional = userStorage.getUser(1L);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    public void testGetUsers() {
        User testingUser = userStorage.getUser(1L).get();
        Collection<User> users = userStorage.getUsers();

        assertThat(users)
                .isNotEmpty()
                .anyMatch(user -> user.getId().equals(testingUser.getId()));
    }

    @Test
    public void testCreateUser() {
        User newUser = new User();
        newUser.setEmail("new@test.com");
        newUser.setLogin("new_user");
        newUser.setName("New User");
        newUser.setBirthday(LocalDate.of(1995, 3, 20));

        User created = userStorage.createUser(newUser);

        assertThat(created)
                .isNotNull()
                .extracting(User::getName)
                .isEqualTo("New User");
        assertThat(created.getId()).isGreaterThan(0);
    }

    @Test
    public void testUpdateUser() {
        User testUser = userStorage.getUser(1L).get();
        testUser.setName("Updated Name");
        testUser.setEmail("updated@test.com");

        User updated = userStorage.updateUser(testUser);

        assertThat(updated.getName()).isEqualTo(testUser.getName());
        assertThat(updated.getEmail()).isEqualTo(testUser.getEmail());

        User fetched = userStorage.getUser(testUser.getId()).get();
        assertThat(fetched.getName()).isEqualTo(testUser.getName());
    }

    @Test
    public void testSaveFriend() {
        User newUser = new User();
        newUser.setEmail("new@test.com");
        newUser.setLogin("new_user");
        newUser.setName("New User");
        newUser.setBirthday(LocalDate.of(1995, 3, 20));

        User user1 = userStorage.createUser(newUser);
        userStorage.saveFriend(1L, user1.getId());

        User user2 = userStorage.getUser(1L).get();
        assertThat(user2.getFriends())
                .anyMatch(user -> user.getId().equals(user1.getId()));
    }

    @Test
    public void testRemoveFromFriends() {
        User newUser = new User();
        newUser.setEmail("new@test.com");
        newUser.setLogin("new_user");
        newUser.setName("New User");
        newUser.setBirthday(LocalDate.of(1995, 3, 20));

        User user1 = userStorage.createUser(newUser);
        userStorage.saveFriend(1L, user1.getId());
        userStorage.removeFromFriends(1L, user1.getId());
        User user2 = userStorage.getUser(1L).get();

        assertThat(user2.getFriends())
                .noneMatch(user -> user.getId().equals(user1.getId()));

        assertThat(user1.getFriends())
                .noneMatch(user -> user.getId().equals(1L));
    }
}
