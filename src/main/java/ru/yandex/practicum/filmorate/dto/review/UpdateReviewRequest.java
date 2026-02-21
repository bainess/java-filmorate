package ru.yandex.practicum.filmorate.dto.review;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateReviewRequest {

    private Long reviewId;

    @NotBlank(message = "Invalid content format")
    private String content;

    private Boolean isPositive;

    private Long userId;

    private Long filmId;

    public boolean hasContent() {
        return content != null && !content.isBlank();
    }

    public boolean hasIsPositive() {
        return isPositive != null;
    }

    public boolean hasUserId() {
        return userId != null;
    }

    public boolean hasFilmId() {
        return filmId != null;
    }
}