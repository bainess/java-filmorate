package ru.yandex.practicum.filmorate.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.review.NewReviewRequest;
import ru.yandex.practicum.filmorate.dto.review.ReviewDto;
import ru.yandex.practicum.filmorate.dto.review.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.model.Review;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReviewMapper {

    public static Review mapToReview(NewReviewRequest request) {
        Review review = new Review();
        review.setContent(request.getContent());
        review.setIsPositive(request.getIsPositive());
        review.setUserId(request.getUserId());
        review.setFilmId(request.getFilmId());
        return review;
    }

    public static ReviewDto mapToReviewDto(Review review) {
        ReviewDto dto = new ReviewDto();
        dto.setId(review.getId());
        dto.setContent(review.getContent());
        dto.setIsPositive(review.getIsPositive());
        dto.setUserId(review.getUserId());
        dto.setFilmId(review.getFilmId());
        dto.setUseful(review.getUseful());
        return dto;
    }

    public static Review updateReviewFields(UpdateReviewRequest request, Review review) {
        if (request.hasContent()) {
            review.setContent(request.getContent());
        }
        if (request.hasIsPositive()) {
            review.setIsPositive(request.getIsPositive());
        }
        if (request.hasUserId()) {
            review.setUserId(request.getUserId());
        }
        if (request.hasFilmId()) {
            review.setFilmId(request.getFilmId());
        }
        return review;
    }
}