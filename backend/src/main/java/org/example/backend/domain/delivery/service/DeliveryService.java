package org.example.backend.domain.delivery.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.domain.delivery.model.dto.DeliveryDto;
import org.example.backend.domain.delivery.model.entity.Delivery;
import org.example.backend.domain.delivery.repository.DeliveryRepository;
import org.example.backend.domain.user.model.entity.User;
import org.example.backend.domain.user.repository.UserRepository;
import org.example.backend.global.common.constants.BaseResponseStatus;
import org.example.backend.global.exception.InvalidCustomException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final UserRepository userRepository;


    public List<DeliveryDto.DeliveryResponse> getList(Long idx) {
        List<Delivery> deliveries = deliveryRepository.findAllByUserIdx(idx).orElseThrow(
                () -> new InvalidCustomException(BaseResponseStatus.USER_DELIVERY_LIST_FAIL)
        );

        return deliveries.stream().map(Delivery::toDeliveryResponse).toList();
    }

    @Transactional
    public void createDelivery(Long idx, DeliveryDto.CreateDeliveryRequest request) {
        //user 객체 조회
        User user = userRepository.findByIdx(idx).orElseThrow(
                () -> new InvalidCustomException(BaseResponseStatus.USER_DETAIL_FAIL_USER_NOT_FOUND)
        );
        //배송지 숫자가 10개 미만인지 체크
        if (user.getDeliveries().size() >= 10){
            throw new InvalidCustomException(BaseResponseStatus.USER_DELIVERY_ADD_FAIL_EXCEEDANCE_MAX_DELIVERY);
        }
        //요청으로 들어온 기본배송지 설정이 true인 경우 기존에 설정된 기본배송지 false로 변경
        if (request.getIsDefault()){
            if (isDefaultExist(user.getDeliveries())){
                Delivery defaultDelivery = getDefaultDelivery(user.getDeliveries());
                defaultDelivery.setIsDefault(false);
                //기존 기본배송지 false로 업데이트
                deliveryRepository.save(defaultDelivery);
            }
        }
        //요청으로 들어온 배송지 정보 저장
        deliveryRepository.save(request.toEntity(user));
    }

    //기존에 isDefault가 true인 배송지 존재유무 반환
    public boolean isDefaultExist(List<Delivery> deliveries) {
        return deliveries.stream()
                .anyMatch(Delivery::getIsDefault);
    }
    public Delivery getDefaultDelivery(List<Delivery> deliveries) {
        //받아온 리스트에서 isDefault가 true인 객체 반환
        return deliveries.stream()
                .filter(Delivery::getIsDefault)
                .findFirst()
                .orElse(null);
    }

    //배송지 삭제
    public void deleteDelivery(Long idx) {
        deliveryRepository.deleteById(idx);
    }

    //idx로 단건조회 후 존재여부 반환
    public boolean isExist(Long idx) {
        Optional<Delivery> optionalDelivery = deliveryRepository.findByIdx(idx);
        if (optionalDelivery.isEmpty()){
            return false;
        }
        return true;
    }

    //조회된 list중 해당 idx로 등록된 배송지가 있으면 배송지 반환
    public Delivery isExist(List<Delivery> deliveries, Long idx){
        for (Delivery d : deliveries){
            if (d.getIdx() == idx){
                return d;
            }
        }
        return null;
    }

    //회원정보로 배송지목록 조회
    //idx에 해당하는 배송지가 있는지 체크
    //기존 기본배송지 여부를 확인
    //있으면 false처리 후 넘겨받은 idx의 배송지를 true로 변경
    //저장
    @Transactional
    public void setDefault(Long idx,Long userIdx) {
        List<Delivery> deliveries = deliveryRepository.findAllByUserIdx(userIdx).orElseThrow(
                () -> new InvalidCustomException(BaseResponseStatus.USER_DELIVERY_EDIT_FAIL)
        );
        Delivery newDefaultDelivery = isExist(deliveries, idx);
        if (newDefaultDelivery == null){
            throw new InvalidCustomException(BaseResponseStatus.USER_DELIVERY_EDIT_FAIL);
        }
        if(isDefaultExist(deliveries)){
            //기존 기본배송지를 false로 변경 후 저장
            Delivery existingDefaultDelivery = getDefaultDelivery(deliveries);
            existingDefaultDelivery.setIsDefault(false);
            deliveryRepository.save(existingDefaultDelivery);
        }
        newDefaultDelivery.setIsDefault(true);
        deliveryRepository.save(newDefaultDelivery);
    }

    public void editDelivery(Long idx, DeliveryDto.EditDeliveryRequest request) {
        Delivery beforeDelivery = deliveryRepository.findByIdx(request.getIdx()).orElseThrow(
                () -> new InvalidCustomException(BaseResponseStatus.USER_DELIVERY_EDIT_FAIL)
        );
        if (beforeDelivery.getUser().getIdx() != idx){
            throw new InvalidCustomException(BaseResponseStatus.USER_DELIVERY_EDIT_FAIL_USER_NOT_MATCH);
        }
        beforeDelivery.updateEntity(request);
        deliveryRepository.save(beforeDelivery);
    }
}
