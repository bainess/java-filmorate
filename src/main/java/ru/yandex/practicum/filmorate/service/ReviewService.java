package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.review.NewReviewRequest;
import ru.yandex.practicum.filmorate.dto.review.ReviewDto;
import ru.yandex.practicum.filmorate.dto.review.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.db.DbFilmStorage;
import ru.yandex.practicum.filmorate.storage.review.db.DbReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.db.DbUserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ReviewService {
    private final DbReviewStorage dbReviewStorage;
    private final DbFilmStorage dbFilmStorage;
    private final DbUserStorage dbUserStorage;

    public Collection<ReviewDto> getReviews() {
        return dbReviewStorage.getReviews().stream()
                .map(ReviewMapper::mapToReviewDto)
                .collect(Collectors.toList());
    }

    public ReviewDto getReview(Long id) {
        Review review = dbReviewStorage.getReview(id).orElseThrow(() -> new NotFoundException("Review not found, id" + id));
        return ReviewMapper.mapToReviewDto(review);
    }

    public Collection<ReviewDto> getReviewsByFilm(Long id, int count) {
        return dbReviewStorage.getReviewsByFilm(id, count).stream()
                .map(ReviewMapper::mapToReviewDto)
                .collect(Collectors.toList());
    }

    public ReviewDto createReview(NewReviewRequest request) {
        Review review = ReviewMapper.mapToReview(request);
        checkFilmAndUserExistence(review.getFilmId(), review.getUserId());
        review = dbReviewStorage.createReview(review);
        return ReviewMapper.mapToReviewDto(review);
    }

    public ReviewDto updateReview(UpdateReviewRequest request) {
        Review updatedReview = dbReviewStorage.getReview(request.getReviewId())
                .map(review -> ReviewMapper.updateReviewFields(request, review))
                .orElseThrow(() -> new NotFoundException("Review not found"));
        updatedReview = dbReviewStorage.updateReview(updatedReview);
        return ReviewMapper.mapToReviewDto(updatedReview);
    }

    public void removeReview(Long id) {
        dbReviewStorage.getReview(id).orElseThrow(() -> new NotFoundException("Review not found, id=" + id));
        dbReviewStorage.removeReview(id);
    }

    public ReviewDto addLikeOrDislike(Long reviewId, Long userId, boolean isLike) {
        dbReviewStorage.getReview(reviewId).orElseThrow(() -> new NotFoundException("Review not found, id=" + reviewId));
        dbUserStorage.getUser(userId).orElseThrow(() -> new NotFoundException("User not found, userId=" + userId));
        dbReviewStorage.addLikeOrDislike(reviewId, userId, isLike);
        return getReview(reviewId);
    }

    public void removeLikeOrDislike(Long reviewId, Long userId) {
        dbReviewStorage.getReview(reviewId).orElseThrow(() -> new NotFoundException("Review not found, id=" + reviewId));
        dbUserStorage.getUser(userId).orElseThrow(() -> new NotFoundException("User not found, userId=" + userId));
        dbReviewStorage.removeLikeOrDislike(reviewId, userId);
    }

    public void removeDislike(Long reviewId, Long userId) {
        dbReviewStorage.getReview(reviewId).orElseThrow(() -> new NotFoundException("Review not found, id=" + reviewId));
        dbUserStorage.getUser(userId).orElseThrow(() -> new NotFoundException("User not found, userId=" + userId));
        dbReviewStorage.removeDislike(reviewId, userId);
    }

    private void checkFilmAndUserExistence(Long filmId, Long userId) {
        dbFilmStorage.findFilm(filmId).orElseThrow(() -> new NotFoundException("Film not found, filmId=" + filmId));
        dbUserStorage.getUser(userId).orElseThrow(() -> new NotFoundException("User not found, userId=" + userId));
    }
}
