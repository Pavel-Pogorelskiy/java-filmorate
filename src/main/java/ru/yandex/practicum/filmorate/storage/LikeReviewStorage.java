package ru.yandex.practicum.filmorate.storage;

public interface LikeReviewStorage {
    void putLike(int reviewId, int userId);

    void pulDislike(int reviewId, int userId);

    void removeLike(int reviewId, int userId);

    void removeDislike(int reviewId, int userId);
}
