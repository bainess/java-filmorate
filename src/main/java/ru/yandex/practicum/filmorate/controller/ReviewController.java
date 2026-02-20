package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.review.NewReviewRequest;
import ru.yandex.practicum.filmorate.dto.review.ReviewDto;
import ru.yandex.practicum.filmorate.dto.review.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/reviews")
@AllArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewDto> createReview(@Valid @RequestBody NewReviewRequest request) {
        return new ResponseEntity<>(reviewService.createReview(request), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<ReviewDto> updateReview(@Valid @RequestBody UpdateReviewRequest review) {
        return new ResponseEntity<>(reviewService.updateReview(review), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable Long id) {
        reviewService.removeReview(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewDto> getReview(@Valid @PathVariable Long id) {
        return new ResponseEntity<>(reviewService.getReview(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Collection<ReviewDto>> getReviews(
            @RequestParam(required = false) Long filmId,
            @RequestParam(required = false, defaultValue = "10") Integer count) {

        if (filmId != null) {
            return new ResponseEntity<>(reviewService.getReviewsByFilm(filmId, count), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(reviewService.getReviews(), HttpStatus.OK);
        }
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<ReviewDto> addLike(@PathVariable Long id, @PathVariable Long userId) {
        return new ResponseEntity<>(reviewService.addLikeOrDislike(id, userId, true), HttpStatus.OK);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public ResponseEntity<ReviewDto> addDislike(@PathVariable Long id, @PathVariable Long userId) {
        return new ResponseEntity<>(reviewService.addLikeOrDislike(id, userId, false), HttpStatus.OK);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLikeOrDislike(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.removeLikeOrDislike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.removeDislike(id, userId);
    }
}
