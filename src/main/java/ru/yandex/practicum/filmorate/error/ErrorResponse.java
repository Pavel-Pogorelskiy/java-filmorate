package ru.yandex.practicum.filmorate.error;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ErrorResponse {
    private String error;
}
