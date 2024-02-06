package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public Review createReview(@Valid @RequestBody Review data) {
        log.info("Create review {}.", data);
        return reviewService.createReview(data);
    }

    @PutMapping
    public Review updateReview(@Valid @RequestBody Review data) {
        log.info("Update review {}.", data);
        return reviewService.updateReview(data);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable int id) {
        reviewService.deleteReview(id);
        log.info("Review whist id = {} was deleted.", id);
    }

    @GetMapping("/{id}")
    public Review getReview(@PathVariable Integer id) {
        log.info("Get review with id = {}.", id);
        return reviewService.getReview(id);
    }

    @GetMapping
    public List<Review> getDataForFilmId(@RequestParam(defaultValue = "0") Integer filmId,
                                         @RequestParam(defaultValue = "10") Integer count) {
        if (filmId == 0) {
            log.info("Список всех отзывов: {}.", reviewService.getDataForFilmId(filmId, count));
        } else {
            log.info("Список отзывов для фильма с id = {}: {}.", filmId, reviewService.getDataForFilmId(filmId, count));
        }
        return reviewService.getDataForFilmId(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void putLike(@PathVariable int id, @PathVariable int userId) {
        reviewService.putLike(id, userId);
        log.info("User with id = {} put like review with id = {}.", userId, id);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void putDislike(@PathVariable int id, @PathVariable int userId) {
        reviewService.putDislike(id, userId);
        log.info("User with id = {} put dislike review with id = {}.", userId, id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable int id, @PathVariable int userId) {
        reviewService.removeLike(id, userId);
        log.info("User with id = {} remove like review with id = {}.", userId, id);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislike(@PathVariable int id, @PathVariable int userId) {
        reviewService.removeDislike(id, userId);
        log.info("User with id = {} remove dislike review with id = {}.", userId, id);
    }
}
