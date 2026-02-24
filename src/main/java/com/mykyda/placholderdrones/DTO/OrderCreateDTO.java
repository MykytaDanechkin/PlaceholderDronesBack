package com.mykyda.placholderdrones.DTO;

import com.mykyda.placholderdrones.database.entity.KitType;
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
}
