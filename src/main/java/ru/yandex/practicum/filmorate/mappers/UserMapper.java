package ru.yandex.practicum.filmorate.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {
    public static User mapToUser(NewUserRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setLogin(request.getLogin());
        user.setEmail(request.getEmail());
        user.setBirthday(request.getBirthday());

        return user;
    }

    public static UserDto mapToUserDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        if (user.getLogin() != null) {
            dto.setLogin(user.getLogin());
        }
        dto.setEmail(user.getEmail());
        dto.setBirthday(user.getBirthday());
        dto.setFriends(user.getFriends());
        return dto;
    }

    public static User updateUserFields(UpdateUserRequest request, User user) {
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
