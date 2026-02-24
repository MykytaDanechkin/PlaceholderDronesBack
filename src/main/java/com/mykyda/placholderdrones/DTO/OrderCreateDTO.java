package com.mykyda.placholderdrones.DTO;

import com.mykyda.placholderdrones.database.entity.KitType;
import com.mykyda.placholderdrones.database.entity.Order;
import com.mykyda.placholderdrones.database.entity.OrderStatus;
import com.mykyda.placholderdrones.database.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreateDTO {

    String email;

    BigDecimal targetLongitude;

    BigDecimal targetLatitude;

    KitType kitType;

    public Order toOrder(){
        return Order.builder()
                .orderStatus(OrderStatus.ORDERED)
                .receiverEmail(email)
                .targetLongitude(targetLongitude)
                .targetLatitude(targetLatitude)
                .kitType(kitType)
                .paymentStatus(PaymentStatus.NOT_PAID)
                .build();
    }
}
