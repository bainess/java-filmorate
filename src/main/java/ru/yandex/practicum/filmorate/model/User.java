package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class User {
    private Long id;

    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Should not contain spaces")
    private String login;

    private String name;

    @Past(message = "Date of birth should be in the past")
    private LocalDate birthday;

    private Set<Long> friends; // при инициализации коллекции в поле, падает NullPointerException

    public Long setFriends(Long id) {
        if (friends == null) {
            friends = new HashSet<>();
        }
        friends.add(id);
        return id;
    }

}

