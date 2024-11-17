package org.example.backend.global.validator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.example.backend.global.validator.TimeRangeValidator;

import jakarta.validation.Constraint;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TimeRangeValidator.class)
public @interface TimeRangeCheck {
	String message() default "시작 시간은 09:00 ~ 22:00 사이여야 합니다.";

	Class[] groups() default {};

	Class[] payload() default {};
}
