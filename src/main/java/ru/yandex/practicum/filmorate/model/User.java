package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;

    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Should not contain spaces")
    private String login;

    @Setter
    private String name;

    @Past(message = "Date of birth should be in the past")
    private LocalDate birthday;

    private Set<User> friends = new HashSet<>();

    public String getName() {
        if (name == null || name.isBlank()) {
            name = login;
        }
        return name;
    }

    public void removeFriend(Long userId) {
        User uf = friends.stream().filter(friend -> Objects.equals(friend.getId(), userId)).findFirst().get();
        friends.remove(uf);
    }
}

