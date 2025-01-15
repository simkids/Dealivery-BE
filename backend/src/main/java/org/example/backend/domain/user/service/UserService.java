package org.example.backend.domain.user.service;


import lombok.RequiredArgsConstructor;
import org.example.backend.domain.user.model.dto.UserDto;
import org.example.backend.domain.user.model.entity.User;
import org.example.backend.domain.delivery.repository.DeliveryRepository;
import org.example.backend.domain.user.repository.UserRepository;
import org.example.backend.global.common.constants.BaseResponseStatus;
import org.example.backend.global.exception.InvalidCustomException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final DeliveryRepository deliveryRepository;
    // private final BCryptPasswordEncoder passwordEncoder;

    //등록된 계정이 있는지 검사
    public void isExist(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()){
            throw new InvalidCustomException(BaseResponseStatus.USER_SIGNUP_FAIL_ALREADY_EXIST);
        }
    }

    public UserDto.UserDetailResponse getDetail(String email, Long idx){
        User user = userRepository.findByEmailAndIdx(email,idx).orElseThrow(
                () -> new InvalidCustomException(BaseResponseStatus.USER_DETAIL_FAIL_USER_NOT_FOUND)
        );
        return user.toUserDetailResponse();
    }

    @Transactional
    public boolean signup(UserDto.UserSignupRequest request) {
        User newUser = userRepository.save(request.toEntity(request.getPassword()));
        //회원가입시 입력한 주소를 기본배송지로 배송지목록에 추가
        deliveryRepository.save(request.toDeliveryEntity(newUser));
        return true;
    }


    @Transactional
    public boolean socialSignup(UserDto.SocialSignupRequest socialSignupRequest) {
        User newUser = userRepository.save(socialSignupRequest.toEntity());
        deliveryRepository.save(socialSignupRequest.toDeliveryEntity(newUser));
        return true;
    }

    public void editDetail(Long idx, UserDto.UserDetailEditRequest request) {
        User user = userRepository.findByIdx(idx).orElseThrow(
                () -> new InvalidCustomException(BaseResponseStatus.USER_DETAIL_EDIT_FAIL_USER_NOT_FOUND)
        );
        user.editDetail(request);
        userRepository.save(user);
    }
}
