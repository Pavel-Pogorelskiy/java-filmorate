package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.LikeReviewStorage;

@Component
@RequiredArgsConstructor
public class LikeReviewDbStorage implements LikeReviewStorage {
    public static final boolean IS_LIKE_TRUE = true;
    public static final boolean IS_LIKE_FALSE = false;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void putLike(int reviewId, int userId) {
        String sql = "insert into like_review values (?, ?, ?)";
        jdbcTemplate.update(sql, reviewId, userId, IS_LIKE_TRUE);
        String sqlUpdateUseful = "update reviews set useful = useful + 1 where review_id = ?";
        jdbcTemplate.update(sqlUpdateUseful, reviewId);
    }

    @Override
    public void pulDislike(int reviewId, int userId) {
        String sql = "insert into like_review values (?, ?, ?)";
        jdbcTemplate.update(sql, reviewId, userId, IS_LIKE_FALSE);
        String sqlUpdateUseful = "update reviews set useful = useful - 1 where review_id = ?";
        jdbcTemplate.update(sqlUpdateUseful, reviewId);
    }

    @Override
    public void removeLike(int reviewId, int userId) {
        String sql = "delete from like_review where reviewId = ? and user_id = ?";
        jdbcTemplate.update(sql, reviewId, userId);
        String sqlUpdateUseful = "update reviews set useful = useful - 1 where review_id = ?";
        jdbcTemplate.update(sqlUpdateUseful, reviewId);
    }

    @Override
    public void removeDislike(int reviewId, int userId) {
        String sql = "delete from like_review where reviewId = ? and user_id = ?";
        jdbcTemplate.update(sql, reviewId, userId);
        String sqlUpdateUseful = "update reviews set useful = useful + 1 where review_id = ?";
        jdbcTemplate.update(sqlUpdateUseful, reviewId);
    }
}
