package org.example.backend.global.validator;

import java.time.LocalDateTime;

import org.example.backend.global.common.constants.BaseResponseStatus;
import org.example.backend.global.exception.InvalidCustomException;
import org.example.backend.global.validator.annotation.MinuteCheck;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MinuteValidator implements ConstraintValidator<MinuteCheck, LocalDateTime> {
	@Override
	public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
		if (!(value.getMinute() == 0 || value.getMinute() == 30)) {
			throw new InvalidCustomException(BaseResponseStatus.PRODUCT_BOARD_REGISTER_FAIL_INVALID_MINUTE);
		}
		return true;
	}
}
