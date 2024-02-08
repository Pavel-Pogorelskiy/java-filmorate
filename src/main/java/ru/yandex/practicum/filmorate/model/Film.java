package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
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
    @Size(max = 100)
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
    private List<Integer> likes = new ArrayList<>();
    private Float mark;
    @NotNull
    private Mpa mpa;
    @NotNull
    @Builder.Default
    private List<Genre> genres = new ArrayList<>();
    @NotNull
    @Builder.Default
    private Set<Director> directors = new HashSet<>();
}