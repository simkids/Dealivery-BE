package org.example.backend.domain.board.service;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductBoardQueueService {
	private final RestTemplate restTemplate;

	public Boolean createQueue(Long productBoardIdx, LocalDateTime endedAt) {
		String url = "/queue/create?productBoardIdx=" + productBoardIdx + "&endedAt=" + endedAt;
		ResponseEntity<Boolean> result = restTemplate.getForEntity(url, Boolean.class);
		return result.getBody();
	}
}
