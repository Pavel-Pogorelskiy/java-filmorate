package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class Director extends BaseUnit {
    @NotBlank
    @Size(max = 50)
    private String name;

    public Map<String, Object> directorRowMap() {
        Map<String, Object> directorRow = new HashMap<>();
        directorRow.put("name", name);
        return directorRow;
    }
}
