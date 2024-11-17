package org.example.backend.global.validator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.example.backend.global.validator.MinuteValidator;

import jakarta.validation.Constraint;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MinuteValidator.class)
public @interface MinuteCheck {
	String message() default "시작/종료 시간은 정각(00분) 또는 30분이어야 합니다.";

	Class[] groups() default {};

	Class[] payload() default {};
}
