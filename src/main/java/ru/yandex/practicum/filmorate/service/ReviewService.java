package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.db.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.db.LikeReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.db.ReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.db.UserDbStorage;

import java.util.List;

@Service
public class ReviewService {
    ReviewDbStorage reviewStorage;
    FilmDbStorage filmStorage;
    UserDbStorage userStorage;
    LikeReviewDbStorage likeReviewDbStorage;

    public ReviewService(ReviewDbStorage reviewStorage,
                         UserDbStorage userStorage,
                         FilmDbStorage filmStorage,
                         LikeReviewDbStorage likeReviewDbStorage) {
        this.reviewStorage = reviewStorage;
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.likeReviewDbStorage = likeReviewDbStorage;
    }


    public Review createReview(Review data) {
        filmStorage.validateId(data.getFilmId());
        userStorage.validateId(data.getUserId());
        return reviewStorage.create(data);
    }

    public Review updateReview(Review data) {
        reviewStorage.validateId(data.getId());
        filmStorage.validateId(data.getFilmId());
        userStorage.validateId(data.getUserId());
        return reviewStorage.uptade(data);
    }

    public void deleteReview(int reviewId) {
        reviewStorage.delete(reviewId);
    }

    public Review getReview(int reviewId) {
        return reviewStorage.get(reviewId);
    }

    public List<Review> getDataForFilmId(Integer filmId, Integer count) {
        return reviewStorage.getDataForFilmId(filmId, count);
    }

    public void putLike(int reviewId, int userId) {
        reviewStorage.validateId(reviewId);
        userStorage.validateId(userId);
        likeReviewDbStorage.putLike(reviewId, userId);
    }

    public void putDislike(int reviewId, int userId) {
        reviewStorage.validateId(reviewId);
        userStorage.validateId(userId);
        likeReviewDbStorage.pulDislike(reviewId, userId);
    }

    public void removeLike(int reviewId, int userId) {
        reviewStorage.validateId(reviewId);
        userStorage.validateId(userId);
        likeReviewDbStorage.removeLike(reviewId, userId);
    }

    public void removeDislike(int reviewId, int userId) {
        reviewStorage.validateId(reviewId);
        userStorage.validateId(userId);
        likeReviewDbStorage.removeDislike(reviewId, userId);
    }
}
