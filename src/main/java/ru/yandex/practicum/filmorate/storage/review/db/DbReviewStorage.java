package ru.yandex.practicum.filmorate.storage.review.db;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.Collection;
import java.util.Optional;

@Repository
public class DbReviewStorage extends BaseRepository<Review> implements ReviewStorage {
    private static final String FIND_BY_ID_QUERY = """
            SELECT
                r.id,
                r.content,
                r.is_positive,
                r.user_id,
                r.film_id,
                COALESCE(SUM(CASE WHEN ru.is_like = TRUE THEN 1\s
                                  WHEN ru.is_like = FALSE THEN -1\s
                                  ELSE 0 END), 0) AS useful
            FROM reviews r
            LEFT JOIN review_useful ru ON r.id = ru.review_id
            WHERE r.id = ?
            GROUP BY r.id, r.content, r.is_positive, r.user_id, r.film_id;""";
    private static final String FIND_ALL_QUERY = """
            SELECT
                r.id,
                r.content,
                r.is_positive,
                r.user_id,
                r.film_id,
                COALESCE(SUM(CASE WHEN ru.is_like = TRUE THEN 1\s
                                  WHEN ru.is_like = FALSE THEN -1\s
                                  ELSE 0 END), 0) AS useful
            FROM reviews r
            LEFT JOIN review_useful ru ON r.id = ru.review_id
            GROUP BY r.id, r.content, r.is_positive, r.user_id, r.film_id;""";
    private static final String FIND_REVIEWS_BY_FILM_QUERY = """
            SELECT
                r.id,
                r.content,
                r.is_positive,
                r.user_id,
                r.film_id,
                COALESCE(SUM(CASE WHEN ru.is_like = TRUE THEN 1
                                  WHEN ru.is_like = FALSE THEN -1
                                  ELSE 0 END), 0) AS useful
            FROM reviews r
            LEFT JOIN review_useful ru ON r.id = ru.review_id
            WHERE r.film_id = ?
            GROUP BY r.id, r.content, r.is_positive, r.user_id, r.film_id
            ORDER BY useful DESC
            LIMIT ?;""";
    private static final String INSERT_QUERY = "INSERT INTO reviews (content, is_positive, user_id, film_id)" +
            "VALUES (?, ?, ?, ?)";
    private static final String INSERT_USEFUL_QUERY = "INSERT INTO review_useful (review_id, user_id, is_like)" +
            "VALUES (?, ?, ?)";
    private static final String UPDATE_REVIEW_QUERY = "UPDATE reviews SET content = ?, is_positive = ?, user_id = ?, film_id = ?" +
            "WHERE id = ?";
    private static final String UPDATE_USEFUL_QUERY = "UPDATE review_useful SET is_like = ?" +
            "WHERE review_id = ? AND user_id = ?";
    private static final String REMOVE_REVIEW_QUERY = "DELETE FROM reviews WHERE id = ?";
    private static final String REMOVE_LIKE_QUERY = "DELETE FROM review_useful WHERE review_id = ? AND user_id = ?";
    private static final String REMOVE_DISLIKE_QUERY = """
            DELETE FROM review_useful
            WHERE review_id = ? AND user_id = ? AND is_like = false
            """;

    public DbReviewStorage(JdbcTemplate jdbc, RowMapper<Review> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Optional<Review> getReview(Long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    @Override
    public Collection<Review> getReviews() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Collection<Review> getReviewsByFilm(Long id, int count) {
        return findMany(FIND_REVIEWS_BY_FILM_QUERY, id, count);
    }

    @Override
    public Review createReview(Review review) {
        int defaultUseful = 0;
        Long id = insert(INSERT_QUERY,
                review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId()
        );

        review.setId(id);
        review.setUseful(defaultUseful);

        return review;
    }

    @Override
    public Review updateReview(Review review) {
        update(UPDATE_REVIEW_QUERY,
                review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId(),
                review.getId()
        );
        return review;
    }

    @Override
    public void removeReview(Long id) {
        update(REMOVE_REVIEW_QUERY, id);
    }


    @Override
    public void addLikeOrDislike(Long reviewId, Long userId, boolean isLike) {
        Optional<Boolean> isExistLike = checkLikeOrDislike(reviewId, userId);

        if (isExistLike.isEmpty()) {
            update(INSERT_USEFUL_QUERY,
                    reviewId,
                    userId,
                    isLike
            );
        } else {
            update(UPDATE_USEFUL_QUERY,
                    isLike,
                    reviewId,
                    userId
            );
        }
    }

    @Override
    public void removeLikeOrDislike(Long reviewId, Long userId) {
        update(REMOVE_LIKE_QUERY,
                reviewId,
                userId
        );
    }

    @Override
    public void removeDislike(Long reviewId, Long userId) {
        update(REMOVE_DISLIKE_QUERY,
                reviewId,
                userId
        );
    }

    public Optional<Boolean> checkLikeOrDislike(Long reviewId, Long userId) {
        String sql = "SELECT is_like FROM review_useful WHERE review_id = ? AND user_id = ?";
        try {
            return Optional.ofNullable(jdbc.queryForObject(sql, Boolean.class, reviewId, userId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
