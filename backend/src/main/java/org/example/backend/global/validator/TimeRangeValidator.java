package org.example.backend.global.validator;

import java.time.LocalDateTime;

import org.example.backend.global.common.constants.BaseResponseStatus;
import org.example.backend.global.exception.InvalidCustomException;
import org.example.backend.global.validator.annotation.TimeRangeCheck;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TimeRangeValidator implements ConstraintValidator<TimeRangeCheck, LocalDateTime> {
	@Override
	public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
		if (value.isBefore(value.withHour(9).withMinute(0).withSecond(0).withNano(0)) ||
		value.isAfter(value.withHour(22).withMinute(0).withSecond(0).withNano(0))) {
			throw new InvalidCustomException(BaseResponseStatus.PRODUCT_BOARD_REGISTER_FAIL_INVALID_START_TIME);
		}
		return true;
	}
}
