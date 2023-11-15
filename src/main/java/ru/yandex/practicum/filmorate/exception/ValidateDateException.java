package ru.yandex.practicum.filmorate.exception;

public class ValidateDateException extends RuntimeException {
    public ValidateDateException(String message) {
        super(message);
    }
}
