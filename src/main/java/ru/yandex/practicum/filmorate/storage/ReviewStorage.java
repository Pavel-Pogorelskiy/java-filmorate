package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage extends AbstractStorage<Review> {
    List<Review> getDataForFilmId(Integer filmId, int count);
}
