package org.example.backend.domain.delivery.model.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.backend.domain.delivery.model.dto.DeliveryDto;
import org.example.backend.domain.user.model.entity.User;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    private String name;

    private Boolean isDefault;

    private String address;

    private String addressDetail;

    private String postNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx")
    private User user;

    public DeliveryDto.DeliveryResponse toDeliveryResponse(){
        return DeliveryDto.DeliveryResponse.builder()
                .idx(this.idx)
                .isDefault(this.isDefault)
                .address(this.address)
                .addressDetail(this.addressDetail)
                .postNumber(this.postNumber)
                .name(this.name)
                .build();
    }

    public void updateEntity(DeliveryDto.EditDeliveryRequest request){
        this.idx = request.getIdx();
        this.name = request.getName();
        this.address = request.getAddress();
        this.addressDetail = request.getAddressDetail();
        this.isDefault = request.getIsDefault();
        this.postNumber = request.getPostNumber();
    }

    public void setIsDefault(Boolean asDefault) {
        isDefault = asDefault;
    }
}
