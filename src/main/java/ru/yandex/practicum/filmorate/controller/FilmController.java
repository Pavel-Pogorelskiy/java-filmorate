package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidateDateException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController extends BaseController <Film> {
    private final static LocalDate DATE_FIRST_RELEASE = LocalDate.of(1895, 12, 28);

    @GetMapping
    @Override
    public List<Film> getAll() {
        log.info("Получение списка всех фильмов  {}", super.getMemory().values());
        return super.getAll();
    }

    @PostMapping
    @Override
    public Film create(@Valid @RequestBody Film film) {
        log.info("Добавление фильма {}", film);
        return super.create(film);
    }

    @PutMapping
    @Override
    public Film uptade(@Valid @RequestBody Film film) {
        log.info("Обновление фильма {}", film);
        return super.uptade(film);
    }

    @Override
    public void validate(Film data) {
        if (data.getReleaseDate().isBefore(DATE_FIRST_RELEASE)) {
            throw new ValidateDateException("Неверно указана дата релиза фильма. Минимальное значение 28.12.1895");
        }
    }
}
