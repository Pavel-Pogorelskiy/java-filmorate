package ru.yandex.practicum.filmorate.storage.memory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

@Component
public class InMemoryUserStorage extends InMemoryBaseStorage<User> implements UserStorage {
}
