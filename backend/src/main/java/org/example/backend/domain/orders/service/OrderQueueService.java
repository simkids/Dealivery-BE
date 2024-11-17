package org.example.backend.domain.orders.service;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderQueueService {
    private final RestTemplate restTemplate;

    private final String QUEUE_URL = "/queue";

    public void exitQueue(Long boardIdx, Long userIdx) {
        // 요청 본문을 Map으로 생성
        Map<String, Long> requestBody = new HashMap<>();
        requestBody.put("boardIdx", boardIdx);
        requestBody.put("userIdx", userIdx);

        // RestTemplate으로 POST 요청 전송
        ResponseEntity<String> response = restTemplate.postForEntity(QUEUE_URL + "/delete", requestBody, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("[Order Exit Queue] Queue deletion successful");
        } else {
           log.info("[Order Exit Queue] Failed to delete queue token");
        }
    }
}
