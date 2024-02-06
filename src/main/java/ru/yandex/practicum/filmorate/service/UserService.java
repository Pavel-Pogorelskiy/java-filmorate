package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final FriendStorage friendStorage;
    private final EventService eventService;

    public void addFriend(int userId, int friendId) {
        userStorage.validateId(userId);
        userStorage.validateId(friendId);
        friendStorage.addFriend(userId, friendId);

        eventService.addFriendEvent(userId, friendId, Event.EventOperation.ADD);
    }

    public List<User> getFriend(int userId) {
        userStorage.validateId(userId);
        return friendStorage.getFriend(userId);
    }

    public void deleteFriend(int userId, int friendId) {
        userStorage.validateId(userId);
        userStorage.validateId(friendId);
        friendStorage.deleteFriend(userId, friendId);

        eventService.addFriendEvent(userId, friendId, Event.EventOperation.REMOVE);
    }

    public List<User> commonFriends(int userId, int friendId) {
        userStorage.validateId(userId);
        userStorage.validateId(friendId);
        return friendStorage.commonFriends(userId, friendId);
    }

    public List<Film> recommendationsFilms(int userId) {
        userStorage.validateId(userId);
        return filmStorage.recommendationsFilms(userId);
    }
}
