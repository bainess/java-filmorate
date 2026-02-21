package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
import java.util.Optional;

public interface ReviewStorage {
    Optional<Review> getReview(Long id);

    Collection<Review> getReviews();

    Collection<Review> getReviewsByFilm(Long id, int count);

    Review createReview(Review review);

    Review updateReview(Review review);

    void removeReview(Long id);

    void addLikeOrDislike(Long reviewId, Long userId, boolean isLike);

    void removeLikeOrDislike(Long reviewId, Long userId);

    void removeDislike(Long reviewId, Long userId);
}
