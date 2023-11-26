package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(of = {"id"})
@SuperBuilder
@NoArgsConstructor
public abstract class BaseUnit {
    private int id;
}
