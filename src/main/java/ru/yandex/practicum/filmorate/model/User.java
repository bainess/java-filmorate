package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
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

    private String name;

    @Past(message = "Date of birth should be in the past")
    private LocalDate birthday;

    private Set<UserFriend> friends = new HashSet<>();

    /*public Set<Long> setFriendsList(HashSet<Long> friendsList) {
        this.friends = friendsList;
        return friends;
    } */
//    private Set<Long> friends_ids = new HashSet<>();

    public Long addFriendToList(Long user) {
        UserFriend  uf = new UserFriend();
        uf.setId(user);
        friends.add(uf);
        return user;
    }

}

