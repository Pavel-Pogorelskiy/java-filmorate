package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeReviewStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikeReviewStorage likeReviewDbStorage;
    private final EventService eventService;

    public Review createReview(Review data) {
        filmStorage.validateId(data.getFilmId());
        userStorage.validateId(data.getUserId());

        data = reviewStorage.create(data);
        eventService.addReviewEvent(data.getUserId(), data.getId(), Event.EventOperation.ADD);

        return data;
    }

    public Review updateReview(Review data) {
        reviewStorage.validateId(data.getId());
        filmStorage.validateId(data.getFilmId());
        userStorage.validateId(data.getUserId());

        data = reviewStorage.uptade(data);
        eventService.addReviewEvent(data.getUserId(), data.getId(), Event.EventOperation.UPDATE);

        return data;
    }

    public void deleteReview(int reviewId) {

        Review review = getReview(reviewId);

        reviewStorage.delete(reviewId);

        eventService.addReviewEvent(review.getUserId(), reviewId, Event.EventOperation.REMOVE);
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
