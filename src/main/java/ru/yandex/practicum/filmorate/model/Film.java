package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.*;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class Film extends BaseUnit {
    @NotEmpty
    private String name;
    @Size(max = 200)
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @Min(1)
    private int duration;
    @JsonIgnore
    @NotNull
    @Builder.Default
    private Set<Integer> likes = new HashSet<>();
    @NotNull
    private Mpa mpa;
    @NotNull
    @Builder.Default
    private List<Genre> genres = new ArrayList<>();

    public int countLikes() {
        return likes.size();
    }
}