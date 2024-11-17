package org.example.backend.domain.user.service;

import freemarker.template.Template;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.backend.domain.user.model.dto.UserAuthTokenDto;
import org.example.backend.domain.user.model.entity.UserAuthToken;
import org.example.backend.domain.user.repository.UserAuthTokenRepository;
import org.example.backend.global.common.constants.BaseResponseStatus;
import org.example.backend.global.exception.InvalidCustomException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
@Service
@RequiredArgsConstructor
public class UserAuthTokenService {

    @Value("${project.mail.url}")
    private String mailUrl;
    private final String TOKEN_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private final UserAuthTokenRepository userAuthTokenRepository;
    private final JavaMailSender mailSender;
    private final FreeMarkerConfigurer freemarkerConfigurer;

    //요청으로 받은 이메일로 코드 생성 및 저장 후 인증메일 발송
    @Transactional
    public Boolean doAuth(UserAuthTokenDto.UserEmailAuthRequest request){
        //요청한 이메일로 생성된 토큰 전부 삭제
        userAuthTokenRepository.deleteAllByEmail(request.getEmail());
        //인증토큰 생성
        String token = generateToken();
        LocalDateTime expiredTime = getTokenExpiry(10);
        UserAuthToken userAuthToken = userAuthTokenRepository.save(request.toEntity(token, expiredTime));
        if (userAuthToken == null) {
            throw new InvalidCustomException(BaseResponseStatus.EMAIL_VERIFY_FAIL_CAN_NOT_CREATE);
        }
        //DB에 토큰 저장이 잘 되면 메일 전송
        if (!sendEmail(request.getEmail(), token,expiredTime)){
            throw new InvalidCustomException(BaseResponseStatus.EMAIL_VERIFY_FAIL_CAN_NOT_SEND);
        }
        return true;
    }

    //6자리 인증코드 생성
    public String generateToken() {
        int tokenLength = 6;
        Random random = new Random();
        StringBuilder token = new StringBuilder();
        for (int i = 0; i < tokenLength; i++) {
            token.append(TOKEN_CHARS.charAt(random.nextInt(TOKEN_CHARS.length())));
        }
        return token.toString();
    }

    // 토큰 유효기간 설정 (10분)
    public LocalDateTime getTokenExpiry(Integer time) {
        return LocalDateTime.now().plusMinutes(time);
    }

    // 토큰 유효성 검증 메서드
    public Boolean isTokenValid(String token, String email) {
        UserAuthToken userAuthToken = userAuthTokenRepository.findByEmail(email).orElseThrow(
                () -> new InvalidCustomException(BaseResponseStatus.USER_SIGNUP_FAIL_INVALID_EMAIL_CODE)
        );
        if (!LocalDateTime.now().isBefore(userAuthToken.getExpiredAt())){
            throw new InvalidCustomException(BaseResponseStatus.EMAIL_VERIFY_FAIL_EXPIRED);
        }
        if (!token.equals(userAuthToken.getToken())){
            throw new InvalidCustomException(BaseResponseStatus.EMAIL_VERIFY_FAIL_INCORRECT);
        }
        return true;
    }

    public Boolean sendEmail(String email, String token, LocalDateTime expiredTime) throws RuntimeException {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분");
            String formattedExpiredTime = expiredTime.format(formatter);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            helper.setTo(email);
            helper.setSubject("[Dealivey] 이메일 인증 요청");

            // 템플릿 내부에서 처리한 변수값 매핑
            Map<String, Object> model = new HashMap<>();
            model.put("token", token);
            model.put("email", email);
            model.put("formattedExpiredTime", formattedExpiredTime);

            // 메일로 전송할 템플릿 렌더링
            // 디렉토리 지정한 configure파일에서 객체 얻어와서 해당 객체로 템플릿 찾아서 얻어온다.
            Template template = freemarkerConfigurer.getConfiguration().getTemplate("email-template.html");
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
            helper.setText(html, true); // Set HTML content

            mailSender.send(message);
            return true;
        } catch (Exception e) {
            throw new InvalidCustomException(BaseResponseStatus.EMAIL_VERIFY_FAIL_CAN_NOT_SEND);
        }
    }
}
