package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;

    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Should not contain spaces")
    private String login;

    private String name;

    @Past(message = "Date of birth should be in the past")
    private LocalDate birthday;

    private Set<Long> friends = new HashSet<>();

    public Long setFriends(Long id) {
        friends.add(id);
        return id;
    }

}

