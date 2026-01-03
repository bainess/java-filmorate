package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;

public class UserControllerTest {
    private UserController controller;
    InMemoryUserStorage userStorage = new InMemoryUserStorage();
    UserService userService = new UserService(userStorage);

    @BeforeEach
    void setUp() {
        controller = new UserController(userStorage, userService);
    }

    @Test
    void shouldAddUserAllFieldsValid() {
        User user = new User();
        user.setName("Name");
        user.setLogin("Login");
        user.setEmail("mail@mail.ru");
        user.setBirthday(LocalDate.of(1980, 12,11));

        controller.createUser(user);

        User userSaved = controller.getUsers().getBody().stream().findFirst().get();
        Assertions.assertEquals(user.getName(), userSaved.getName());
        Assertions.assertEquals(user.getEmail(), userSaved.getEmail());
        Assertions.assertEquals(user.getLogin(), userSaved.getLogin());
        Assertions.assertEquals(user.getBirthday(), userSaved.getBirthday());
    }

    @Test
    void shouldUpdateAllFields() {
        User user = new User();
        user.setName("Name");
        user.setLogin("Login");
        user.setEmail("mail@mail.ru");
        user.setBirthday(LocalDate.of(1980, 12,11));

        controller.createUser(user);

        User uzer = new User();
        uzer.setId(1L);
        uzer.setBirthday(LocalDate.of(1999,3,22));
        uzer.setEmail("updated@mail.com");
        uzer.setLogin("newLogin");
        uzer.setName("newName");

        controller.updateUser(uzer);

        Assertions.assertEquals(uzer, controller.getUsers().getBody().stream().findFirst().get());
    }

    @Test
    void shouldReturnAllUsers() {
        User user = new User();
        user.setName("Name");
        user.setLogin("Login");
        user.setEmail("mail@mail.ru");
        user.setBirthday(LocalDate.of(1980, 12,11));

        User uzer = new User();
        uzer.setId(1L);
        uzer.setBirthday(LocalDate.of(1999,3,22));
        uzer.setEmail("updated@mail.com");
        uzer.setLogin("newLogin");
        uzer.setName("newName");

        controller.createUser(user);
        controller.createUser(uzer);

        Assertions.assertEquals(2, controller.getUsers().getBody().size());
    }
}
