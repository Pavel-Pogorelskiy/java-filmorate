package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.memory.InMemoryUserStorage;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private InMemoryUserStorage userStorage;

    @GetMapping
    public List<User> getAll() {
        log.info("Получение списка всех пользователей {}", userStorage.getAll());
        return userStorage.getAll();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        log.info("Создание пользователя {}", user);
        return userStorage.create(user);
    }

    @PutMapping
    public User uptade(@Valid @RequestBody User user) {
        log.info("Обновление пользователя {}", user);
        return userStorage.uptade(user);
    }

    @GetMapping(value = "/{id}")
    public User getUserId(@PathVariable int id) {
        log.info("Получение пользователя {}", userStorage.get(id));
        return userStorage.get(id);
    }

    @GetMapping(value = "/{id}/friends")
    public List<User> getFriends(@PathVariable int id) {
        log.info("Получение списка друзей {}", userService.getFriend(id));
        return userService.getFriend(id);
    }

    @PutMapping(value = "/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id,@PathVariable int friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping(value = "/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable int id,@PathVariable int friendId) {
        userService.deleteFriend(id, friendId);
    }

    @GetMapping(value = "/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable int id,@PathVariable int otherId) {
        log.info("Получение списка общих друзей {}", userService.commonFriends(id, otherId));
        return userService.commonFriends(id, otherId);
    }
}