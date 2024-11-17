package org.example.backend.global.common.constants;

import java.util.Arrays;

import org.example.backend.global.exception.InvalidCustomException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;

@Getter
public enum CategoryType {
	FOOD("식품"),
	CLOTHES("의류"),
	BEAUTY("뷰티"),
	LIFE("라이프");


	private final String type;

	CategoryType(String type) {
		this.type = type;
	}

	@JsonCreator
	public static CategoryType from(String type) {
		System.out.println("Received category type: " + type);
		return Arrays.stream(CategoryType.values())
			.filter(category -> category.getCategoryType().equals(type))
			.findFirst()
			.orElseThrow(() -> new InvalidCustomException(BaseResponseStatus.PRODUCT_BOARD_REGISTER_FAIL_INVALID_CATEGORY));
	}

	@JsonValue
	public String getCategoryType() {
		return type;
	}
}
