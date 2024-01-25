package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.db.FriendDbStorage;
import ru.yandex.practicum.filmorate.storage.db.UserDbStorage;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserDbStorage userStorage;
    @Autowired
    private FriendDbStorage friendStorage;

    public void addFriend(int userId, int friendId) {
        userStorage.validateId(userId);
        userStorage.validateId(friendId);
        friendStorage.addFriend(userId, friendId);
    }

    public List<User> getFriend(int userId) {
        userStorage.validateId(userId);
        return friendStorage.getFriend(userId);
    }

    public void deleteFriend(int userId, int friendId) {
        userStorage.validateId(userId);
        userStorage.validateId(friendId);
        friendStorage.deleteFriend(userId, friendId);
    }

    public List<User> commonFriends(int userId, int friendId) {
        userStorage.validateId(userId);
        userStorage.validateId(friendId);
        return friendStorage.commonFriends(userId, friendId);
    }
}
