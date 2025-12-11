package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.ToString;

import java.time.Instant;
import java.time.LocalDate;

@Data
public class User {
    long id;

    @Email(message = "Invalid email format")
    String email;

    @NotBlank(message = "Should not contain spaces")
    String login;

    String name;

    @Past(message = "Date of birth should be in the past")
    LocalDate birthday;
}

