package ru.yandex.practicum.filmorate.storage.memory;

import ru.yandex.practicum.filmorate.exception.NotFoundDataException;
import ru.yandex.practicum.filmorate.model.BaseUnit;
import ru.yandex.practicum.filmorate.storage.AbstractStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class InMemoryBaseStorage<T extends BaseUnit> implements AbstractStorage<T> {
    private final Map<Integer, T> memory = new HashMap<>();
    private int generateId;
    @Override
    public T create(T data) {
        data.setId(++generateId);
        memory.put(data.getId(),data);
        return data;
    }

    @Override
    public T uptade(T data) {
        validateId(data.getId());
        memory.put(data.getId(),data);
        return data;
    }

    @Override
    public T get(int id) {
        validateId(id);
        return memory.get(id);
    }

    @Override
    public List<T> getAll() {
        return new ArrayList<>(memory.values());
    }

    @Override
    public void delete(int id) {
        memory.remove(id);
    }

    public void validateId(int id) {
        if (!memory.containsKey(id) || id < 0) {
            throw new NotFoundDataException("Данные с id = " + id + " не найдены");
        }
    }
}
