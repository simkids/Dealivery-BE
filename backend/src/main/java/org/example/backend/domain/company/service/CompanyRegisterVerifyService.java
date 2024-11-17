package org.example.backend.domain.company.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.backend.domain.company.model.dto.CompanyDto;
import org.example.backend.domain.company.model.entity.CompanyRegisterVerify;
import org.example.backend.domain.company.repository.CompanyRegisterVerifyRepository;
import org.example.backend.global.common.constants.BaseResponseStatus;
import org.example.backend.global.exception.InvalidCustomException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.springframework.http.HttpMethod.*;

@Service
@RequiredArgsConstructor
public class CompanyRegisterVerifyService {
    @Value("${api.nts-businessman.v1.validate.serviceKey}")
    private String serviceKey;
    @Value("${api.nts-businessman.v1.validate.url}")
    private String apiUrl;
    private final CompanyRegisterVerifyRepository companyRegisterVerifyRepository;


    @Transactional
    public Boolean verifyRegNumber(CompanyDto.CompanySignupRequest request) {
        CompanyDto.RegNumberVerifyRequest regNumberVerifyRequest = new CompanyDto.RegNumberVerifyRequest(
            request.getRegNumber(), request.getName(), request.getOpenedAt().toString()
        );
        //요청에서 받아온 값을 통해 공공API로 요청 전송
        ResponseEntity<String> responseEntity = sendPostRequest(regNumberVerifyRequest);
        //응답 받아서 값 검증
        verify(responseEntity);
        //정상이면 DB에서 값 삭제후 재 생성
        companyRegisterVerifyRepository.deleteByRegNumber(request.getRegNumber());
        companyRegisterVerifyRepository.save(request.toEntity());

        return true;
    }

    //url로 사업자등록여부 판단하는 http 요청을 전송하는 메소드
    public ResponseEntity<String> sendPostRequest(CompanyDto.RegNumberVerifyRequest requestBody) {
        //요청보낼 RestTemplage 객체 생성
        RestTemplate restTemplate = new RestTemplate();

        // 헤더 설정 (JSON 형식)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(HttpHeaders.AUTHORIZATION, "Infuser " + serviceKey);

        // 요청 본문을 JSON 배열로 감싸기
        Map<String, Object> requestBodyWrapper = new HashMap<>();
        List<CompanyDto.RegNumberVerifyRequest> businessesList = new ArrayList<>();
        businessesList.add(requestBody);
        requestBodyWrapper.put("businesses", businessesList);
        // 요청 본문과 헤더 설정
        HttpEntity<Object> request = new HttpEntity<>(requestBodyWrapper, headers);

        // POST 요청 전송
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                apiUrl,
                request,
                String.class);
            //응답 반환
            return response;
        }catch (RuntimeException e){
            //실패시 예외처리
            throw new InvalidCustomException(BaseResponseStatus.USER_SIGNUP_FAIL_UNAUTHORIZED_REG_NUMBER);
        }
    }

    //api호출을 분석하고 판별하는 함수
    public void verify(ResponseEntity<String> response) {
        //받은 응답의 body에서 valid값 추출 (01이면 인증완료, 나머지는 실패)
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);
        JsonArray dataArray = jsonObject.getAsJsonArray("data");
        JsonObject dataObject = dataArray.get(0).getAsJsonObject();
        String valid = dataObject.get("valid").getAsString();
        //실패했으면 예외처리, 성공했으면 처리x
        if (!valid.equals("01")){
            throw new InvalidCustomException(BaseResponseStatus.USER_SIGNUP_FAIL_UNAUTHORIZED_REG_NUMBER);
        };
    }

}