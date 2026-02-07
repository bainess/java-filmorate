package ru.yandex.practicum.filmorate.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class NewUserRequest {

    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Should not contain spaces")
    private String login;

    private String name;

    @Past(message = "Date of birth should be in the past")
    private LocalDate birthday;

}
