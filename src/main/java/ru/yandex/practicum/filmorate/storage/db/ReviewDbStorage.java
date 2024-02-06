package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundDataException;
import ru.yandex.practicum.filmorate.exception.ValidateDateException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {
    public static final int USEFUL_FOR_NEW_REVIEW = 0;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Review create(Review data) {
        validate(data);
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                .withTableName("reviews")
                .usingGeneratedKeyColumns("review_id");

        Map<String, Object> params = Map.of(
                "content", data.getContent(),
                "isPositive", data.getIsPositive(),
                "user_id", data.getUserId(),
                "film_id", data.getFilmId(),
                "useful", USEFUL_FOR_NEW_REVIEW);

        data.setId(simpleJdbcInsert.executeAndReturnKey(params).intValue());
        return data;
    }

    @Override
    public Review uptade(Review data) {
        validate(data);
        int id = data.getId();
        String sql = "update reviews set content = ?, isPositive = ? where review_id = ?";

        int rowsUpdated = jdbcTemplate.update(
                sql,
                data.getContent(),
                data.getIsPositive(),
                id);

        if (rowsUpdated <= 0) {
            throw new NotFoundDataException(String.format("Review with id = %s not found", id));
        }
        return get(id);
    }

    @Override
    public Review get(int id) {
        String sql = "select * from reviews where review_id = ?";

        List<Review> reviews = jdbcTemplate.query(sql, this::createReview, id);

        if (reviews.size() != 1) {
            throw new NotFoundDataException(String.format("Review with id = %s is not single", id));
        }
        return reviews.get(0);
    }

    @Override
    public List<Review> getAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Review> getDataForFilmId(Integer filmId, int count) {
        String sql;
        List<Review> reviews = new LinkedList<>();

        if (filmId == 0) {
            sql = "select * from reviews order by useful desc limit ?";
            reviews = jdbcTemplate.query(sql, this::createReview, count);
        } else {
            sql = "select * from reviews where film_id = ? order by useful desc limit ?";
            reviews = jdbcTemplate.query(sql, this::createReview, filmId, count);
        }

        return reviews;
    }

    @Override
    public void delete(int id) {
        String sql = "delete from reviews where review_id = ?";
        jdbcTemplate.update(sql, id);
    }

    private Review createReview(ResultSet rs, int rowNum) throws SQLException {
        return Review.builder()
                .id(rs.getInt("review_id"))
                .content(rs.getString("content"))
                .isPositive(rs.getBoolean("isPositive"))
                .userId(rs.getInt("user_id"))
                .filmId(rs.getInt("film_id"))
                .useful(rs.getInt("useful"))
                .build();
    }

    public void validateId(int id) {
        String sql = "select review_id from reviews where review_id = ?";
        List<Integer> getIdList = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("review_id"), id);
        if (getIdList.size() == 0) {
            throw new NotFoundDataException(String.format("Review with id = %s not found", id));
        }
    }

    private void validate(Review review) {
        if (review.getContent() == null) {
            throw new ValidateDateException("Invalid review: content should not be null.");
        } else if (review.getUserId() == null) {
            throw new ValidateDateException("Invalid review: userId should not be null.");
        } else if (review.getFilmId() == null) {
            throw new ValidateDateException("Invalid review: filmId should not be null.");
        } else if (review.getIsPositive() == null) {
            throw new ValidateDateException("Invalid review: isPositive should not be null.");
        }
    }
}
