package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidateDateException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private FilmController filmController;


    @BeforeEach
    void setUp() {
        filmController = new FilmController();
    }

    @Test
    void validateException() {
        Film film = Film.builder()
                .name("Film")
                .description("Description")
                .releaseDate(LocalDate.of(1800, 1,1))
                .duration(100)
                .build();
        Assertions.assertThrows(
                ValidateDateException.class,
                () -> filmController.validate(film), "Не правильная работа валидации");
    }

    @Test
    void validateNormal() {
        Film film = Film.builder()
                .name("Film")
                .description("Description")
                .releaseDate(LocalDate.of(1900, 1, 1))
                .duration(100)
                .build();
        filmController.validate(film);
    }
}