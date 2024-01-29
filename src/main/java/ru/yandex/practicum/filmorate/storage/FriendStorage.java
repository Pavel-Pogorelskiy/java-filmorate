package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendStorage {
    void addFriend(int userId, int friendId);

    List<User> getFriend(int userId);

    void deleteFriend(int userId, int friendId);

    List<User> commonFriends(int id, int otherId);
}
