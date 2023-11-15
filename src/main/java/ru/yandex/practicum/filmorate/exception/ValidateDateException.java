package ru.yandex.practicum.filmorate.exception;

public class ValidateDateException extends RuntimeException {
    public ValidateDateException () {}

    public ValidateDateException (String message) {
        super(message);
    }
}
