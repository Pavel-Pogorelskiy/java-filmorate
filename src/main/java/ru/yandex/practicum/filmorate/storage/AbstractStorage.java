package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.BaseUnit;

import java.util.List;

public interface AbstractStorage<T extends BaseUnit> {
    T create(T data);
    T uptade(T data);
    T get(int id);
    List<T> getAll();
    void delete(int id);

}
