package com.mykyda.placholderdrones.app.DTO;

import com.mykyda.placholderdrones.app.database.entity.KitType;
import com.mykyda.placholderdrones.app.database.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderPutDTO {

    String receiverEmail;

    BigDecimal latitude;

    BigDecimal longitude;

    KitType kitType;

    OrderStatus orderStatus;

}
