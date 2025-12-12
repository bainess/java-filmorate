package ru.yandex.practicum.filmorate.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

@Slf4j
public class ReleaseDateValidator implements ConstraintValidator<ValidReleaseDate, LocalDate> {
    private static final LocalDate RELEASE_DATE_MIN = LocalDate.of(1895, 12, 28);

    @Override
    public void initialize(ValidReleaseDate constrainAnnotation) {
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        log.warn("ReleaseDate Validation {}", value);
        if (value == null) {
            return true;
        }
        return value.isAfter(RELEASE_DATE_MIN);
    }

}
