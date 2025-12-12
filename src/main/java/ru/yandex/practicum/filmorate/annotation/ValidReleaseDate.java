package ru.yandex.practicum.filmorate.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.annotation.*;
import java.time.LocalDate;

@Documented
@Constraint(validatedBy = {ReleaseDateValidator.class})
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidReleaseDate {
    String message() default "Invalid release date";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
