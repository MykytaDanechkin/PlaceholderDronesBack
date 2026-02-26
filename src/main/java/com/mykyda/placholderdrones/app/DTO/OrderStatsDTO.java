package com.mykyda.placholderdrones.app.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatsDTO {

    long totalOrders;

    double totalTax;

    long totalPending;
}
