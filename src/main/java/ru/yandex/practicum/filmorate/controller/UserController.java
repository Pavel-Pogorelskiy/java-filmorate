package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController extends BaseController<User> {
    @GetMapping
    @Override
    public List<User> getAll() {
        log.info("Получение списка всех пользователей {}", super.getMemory().values());
        return super.getAll();
    }

    @PostMapping
    @Override
    public User create(@Valid @RequestBody User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        log.info("Создание пользователя {}", user);
        return super.create(user);
    }

    @PutMapping
    @Override
    public User uptade(@Valid @RequestBody User user) {
        log.info("Обновление пользователя {}", user);
        return super.uptade(user);
    }

    @Override
    public void validate(User data) {
    }
}
