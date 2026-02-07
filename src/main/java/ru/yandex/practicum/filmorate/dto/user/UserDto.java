package ru.yandex.practicum.filmorate.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserFriend;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class UserDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Should not contain spaces")
    private String login;

    private String name;

    @Past(message = "Date of birth should be in the past")
    private LocalDate birthday;


    private Set<UserFriend> friends = new HashSet<>();
}
