package ru.yandex.practicum.filmorate.exception;

public class NotFoundDataException extends RuntimeException {
    public NotFoundDataException(String message) {
        super(message);
    }
}
