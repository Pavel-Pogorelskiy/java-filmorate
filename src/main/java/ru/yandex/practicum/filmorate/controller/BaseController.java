package ru.yandex.practicum.filmorate.controller;

import ru.yandex.practicum.filmorate.exception.NotFoundDataException;
import ru.yandex.practicum.filmorate.model.BaseUnit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseController<T extends BaseUnit> {

    private final Map<Integer, T> memory = new HashMap<>();
    private int generateId;

    public List<T> getAll() {
        return new ArrayList<>(memory.values());
    }

    public T create(T data) {
        validate(data);
        data.setId(++generateId);
        memory.put(data.getId(),data);
        return data;
    }

    public T uptade(T data) {
        if (!memory.containsKey(data.getId())) {
            throw new NotFoundDataException("Данные не найдены");
        }
        validate(data);
        memory.put(data.getId(),data);
        return data;
    }

    public abstract void validate(T data);

    public Map<Integer, T> getMemory() {
        return memory;
    }
}

