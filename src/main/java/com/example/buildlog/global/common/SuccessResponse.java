package com.example.buildlog.global.common;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

@NoArgsConstructor
@Getter
public class SuccessResponse<T> {
	private int status;
	private String message;
	private T data;

	@Builder
	public SuccessResponse(int status, String message, @Nullable T data) {
		this.status = status;
		this.message = message;
		this.data = data;
	}

	public static SuccessResponse<Void> of(HttpStatus status, String message) {
		return SuccessResponse.<Void>builder()
				.status(status.value())
				.message(message)
				.build();
	}

	public static <T> SuccessResponse<T> of(HttpStatus status, String message, T data) {
		return SuccessResponse.<T>builder()
				.status(status.value())
				.message(message)
				.data(data)
				.build();
	}
}
