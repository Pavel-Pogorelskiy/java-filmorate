package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
public class MpaController {
    @Autowired
    private MpaService mpaService;

    @GetMapping
    public List<Mpa> getAll() {
        log.info("Получение списка всех рейтингов {}", mpaService.getAll());
        return mpaService.getAll();
    }

    @GetMapping(value = "/{id}")
    public Mpa getMpaId(@PathVariable int id) {
        log.info("Получение рейтинга {}", mpaService.get(id));
        return mpaService.get(id);
    }
}
