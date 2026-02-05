package ru.yandex.practicum.filmorate.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {
    public static User mapToUser(NewUserRequest request) {
        User user = new User();
        user.setId(user.getId());
        user.setName(user.getName());
        user.setLogin(user.getLogin());
        user.setEmail(user.getEmail());
        user.setBirthday(user.getBirthday());

        return user;
    }

    public static UserDto mapToFilmDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        if (user.getLogin() != null) {
            dto.setLogin(user.getLogin());
        }
        dto.setEmail(user.getEmail());
        dto.setBirthday(user.getBirthday());

        return dto;
    }

    public static User UpdateUserFileds(UpdateUserRequest request, User user) {
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getLogin() != null) {
            user.setLogin(request.getLogin());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getBirthday() != null) {
            user.setBirthday(request.getBirthday());
        }

        return user;
    }
}
