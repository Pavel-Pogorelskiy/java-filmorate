package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/directors")
public class DirectorController {
    private final DirectorService directorService;

    @PostMapping
    public Director create(@RequestBody @Valid Director director) {
        return directorService.create(director);
    }

    @PutMapping
    public Director put(@RequestBody @Valid Director director) {
        return directorService.put(director);
    }

    @GetMapping("/{id}")
    public Director get(@PathVariable int id) {
        return directorService.get(id);
    }

    @GetMapping
    public Collection<Director> findAll() {
        return directorService.findAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        directorService.delete(id);
    }
}
