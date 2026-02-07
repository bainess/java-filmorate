//package ru.yandex.practicum.filmorate.controller;
//
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
//import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
//import ru.yandex.practicum.filmorate.dto.user.UserDto;
//import ru.yandex.practicum.filmorate.model.User;
//import ru.yandex.practicum.filmorate.service.UserService;
//import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
//
//import java.time.LocalDate;
//
//public class UserControllerTest {
//    private UserController controller;
//    InMemoryUserStorage userStorage = new InMemoryUserStorage();
//    UserService userService = new UserService(userStorage);
//
//    @BeforeEach
//    void setUp() {
//        controller = new UserController(userService);
//    }
//
//    @Test
//    void shouldAddUserAllFieldsValid() {
//        NewUserRequest user = NewUserRequest.builder()
//                .name("Name")
//                .login("Login")
//                .email("mail@mail.ru")
//                .birthday(LocalDate.of(1980, 12,11))
//                .build();
//
//        controller.createUser(user);
//
//        UserDto userSaved = controller.getUsers().getBody().stream().findFirst().get();
//        Assertions.assertEquals(user.getName(), userSaved.getName());
//        Assertions.assertEquals(user.getEmail(), userSaved.getEmail());
//        Assertions.assertEquals(user.getLogin(), userSaved.getLogin());
//        Assertions.assertEquals(user.getBirthday(), userSaved.getBirthday());
//    }
//
//    @Test
//    void shouldUpdateAllFields() {
//        NewUserRequest user = NewUserRequest.builder()
//                .name("Name")
//                .login("Login")
//                .email("mail@mail.ru")
//                .birthday(LocalDate.of(1980, 12,11))
//                .build();
//
//        controller.createUser(user);
//
//        UpdateUserRequest uzer = UpdateUserRequest.builder()
//                .id(1L)
//                .birthday(LocalDate.of(1999,3,22))
//                .email("updated@mail.com").login("newLogin")
//                .name("newName")
//                .build();
//
//        controller.updateUser(uzer);
//
//        Assertions.assertEquals(uzer, controller.getUsers().getBody().stream().findFirst().get());
//    }
//
//    @Test
//    void shouldReturnAllUsers() {
//        NewUserRequest user = NewUserRequest.builder()
//                .name("Name")
//                .login("Login")
//                .email("mail@mail.ru")
//                .birthday(LocalDate.of(1980, 12,11))
//                .build();
//
//        NewUserRequest uzer = NewUserRequest.builder()
//                .birthday(LocalDate.of(1999, 3,22))
//                .email("updated@mail.com")
//                .login("newLogin")
//                .name("newName")
//                .build();
//
//        controller.createUser(user);
//        controller.createUser(uzer);
//
//        Assertions.assertEquals(2, controller.getUsers().getBody().size());
//    }
//}
