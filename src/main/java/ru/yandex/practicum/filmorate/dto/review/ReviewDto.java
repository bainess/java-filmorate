package ru.yandex.practicum.filmorate.dto.review;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReviewDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank(message = "Invalid content format")
    private String content;

    private Boolean isPositive;
    private Long userId;
    private Long filmId;
    private Integer useful;
}