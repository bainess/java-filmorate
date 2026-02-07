package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserFriend;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.storage.user.db.DbFriendsStorage;
import ru.yandex.practicum.filmorate.storage.user.db.DbUserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private UserStorage userStorage;
    private DbFriendsStorage friendStorage;

    @Autowired
    public UserService(UserStorage userStorage, DbFriendsStorage friendStorage) {

        this.userStorage = userStorage;
        this.friendStorage = friendStorage;
    }

    public UserDto getUserById(Long id) {
        UserDto dto = userStorage.getUser(id)
                .map(UserMapper::mapToUserDto)
                .orElseThrow(() -> new NotFoundException("User not found"));
        dto.setFriends(friendStorage.getFriends(id));
//        User user = userStorage.getUser(id).get();
//        user.getFriends_ids().stream().map(fid -> user.getFriends()
//                .add(userStorage.getUser(fid).get()))
//                .collect(Collectors.toCollection(HashSet::new));
//
//        UserDto dto =  Optional.of(user)
//                .map(UserMapper::mapToUserDto)
//                .orElseThrow(() -> new NotFoundException("User not found"));

        return dto;
    }

    public Collection<UserDto> getUsers() {
        return new ArrayList<>(userStorage.getUsers())
                .stream()
                .map(UserMapper::mapToUserDto)
                .toList();
//        ArrayList<User> users = new ArrayList<>(userStorage.getUsers());
//        for (User user : users) {
//            for (Long fid : user.getFriends_ids()) {
//                Set<User> friends = new HashSet<>(get);
//            }
//        }
////        for (User i : userStorage.getUsers()) {
////            System.out.println(i.getId());
////        }
//
////        return new ArrayList<>(userStorage.getUsers())
////                .stream()
////                .map(UserMapper::mapToUserDto)
////                .toList();
//
//        return new HashSet<>(userStorage.getUsers())
//                .stream().map(user -> getUserById(user.getId()))
//                .collect(Collectors.toCollection(HashSet::new));
    }

    public UserDto updateUser(Long id, UpdateUserRequest request) {
        if (userStorage.getUser(id).isEmpty()) {throw new NotFoundException("User not found"); }
            User updatedUser = userStorage.getUser(id)
                    .map(user -> UserMapper.updateUserFields(request, user))
                    .orElseThrow(() -> new NotFoundException("User not found"));
            updatedUser = userStorage.updateUser(updatedUser);
            return UserMapper.mapToUserDto(updatedUser);
    }


    public List<UserDto> getCommonFriends(Long userId, Long friendId) {
        return userStorage.getUser(userId).get().getFriends().stream()
                .filter(id -> userStorage.getUser(friendId).get().getFriends().contains(id))
                .map(user -> userStorage.getUser(user.getId()))
                .map(user -> UserMapper.mapToUserDto(user.get()))
                .toList();
    }

    public Collection<UserFriend> getFriends(Long id) {
        return userStorage.getUser(id).get().getFriends();
//        if (userStorage.getUser(id).isEmpty()) {
//            throw new NotFoundException("User was not found");
//        }
//        Set<User> friendsIds = userStorage.getUser(id).get().getFriends();
//        return new ArrayList<>(userStorage.getUsers().stream()
//                .filter(user1 -> friendsIds.contains(user1.getId()))
//                .map(UserMapper::mapToUserDto)
//                .toList());
    }

    public UserDto createUser(NewUserRequest request) {
        User user = UserMapper.mapToUser(request);
        user = userStorage.createUser(user);
        return UserMapper.mapToUserDto(user);
    }

    public UserFriend setFriendship(Long user_id, Long friend_id) {
        if (userStorage.getUser(user_id) == null) {
            throw new NotFoundException("User" + user_id + " was not found");
        }
        if (userStorage.getUser(friend_id) == null) {
            throw new NotFoundException("User" + friend_id + " was not found");
        }
        UserFriend friend = new UserFriend();
        friend.setId(friend_id);
        addFriend(user_id, friend_id);
        return friend;
    }

    private void addFriend(Long user, Long friend) {
        userStorage.saveFriend(user, friend);
    }

    public void removeFromFriends(Long user1, Long user2) {
        if (userStorage.getUser(user1) == null) {
            throw new NotFoundException("User" + user1 + " was not found");
        }
        if (userStorage.getUser(user2) == null) {
            throw new NotFoundException("User" + user2 + " was not found");
        }
        removeFriend(user1, user2);
        removeFriend(user2, user1);
    }

    private Long removeFriend(Long userId, Long friendId) {
        userStorage.getUser(userId).get().getFriends().remove(friendId);
        return friendId;
    }

}
