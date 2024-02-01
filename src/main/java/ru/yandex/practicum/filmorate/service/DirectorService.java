package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundDataException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.db.DirectorDbStorage;

import java.util.Collection;

@Slf4j
@Service
@AllArgsConstructor
public class DirectorService {

    private final DirectorDbStorage directorStorage;

    public Director get(int id) {
        if (directorStorage.isRegistered(id)) {
            log.info("Режиссер с id = {} возвращён.", id);
            return directorStorage.get(id);
        } else {
            throw new NotFoundDataException("Режиссер с id = " + id + " не найден в списке.");
        }
    }

    public Collection<Director> findAll() {
        Collection<Director> directors = directorStorage.findAll();
        log.info("Текущее количество режиссеров: {} Список возвращён.", directors.size());
        return directors;
    }

    public Director create(Director director) {
        directorStorage.create(director);
        log.info("Добавлен новый режиссер с id = {}", director.getId());
        return director;
    }

    public Director put(Director director) {
        if (get(director.getId()) != null) {
            log.info("Режиссер с id = {} обновлён.", director.getId());
            return directorStorage.put(director);
        } else {
            throw new NotFoundDataException("Режиссер с id = " + director.getId() + " не найден в списке.");
        }
    }

    public void delete(int id) {
        if (directorStorage.isRegistered(id)) {
            log.info("Режиссер с id = {} удален.", id);
            directorStorage.delete(id);
        } else {
            throw new NotFoundDataException("Режиссер с id = " + id + " не найден в списке.");
        }
    }

}
