package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/genres")
public class GenreController {
    @Autowired
    private GenreService genreService;
    @GetMapping
    public List<Genre> getAll() {
        log.info("Получение списка всех жанров {}", genreService.getAll());
        return genreService.getAll();
    }

    @GetMapping(value = "/{id}")
    public Genre getGenreId(@PathVariable int id) {
        log.info("Получение жанра {}", genreService.get(id));
        return genreService.get(id);
    }
}
