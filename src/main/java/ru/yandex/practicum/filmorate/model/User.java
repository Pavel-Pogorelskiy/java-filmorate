package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class User extends BaseUnit {
    @NotEmpty
    @Email
    private String email;
    @NotBlank
    @Size(max = 50)
    private String login;
    @Size(max = 50)
    private String name;
    @PastOrPresent
    private LocalDate birthday;
    @NotNull
    @Builder.Default
    @JsonIgnore
    private Set<Integer> friends = new HashSet<>();
}