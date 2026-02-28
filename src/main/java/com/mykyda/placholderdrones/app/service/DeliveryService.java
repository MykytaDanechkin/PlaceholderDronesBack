package com.mykyda.placholderdrones.app.service;

import com.mykyda.placholderdrones.app.database.entity.DroneLog;
import com.mykyda.placholderdrones.app.database.entity.DroneStatus;
import com.mykyda.placholderdrones.app.database.entity.OrderStatus;
import com.mykyda.placholderdrones.app.exception.OrderStatusException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DroneService droneService;

    private final DroneLogService droneLogService;

    private final OrderService orderService;

    @Transactional
    public void startDelivery(long orderId) {
        var order = orderService.findById(orderId);
        if (order.getOrderStatus() == OrderStatus.ORDERED || order.getOrderStatus() == OrderStatus.WAITING_FOR_PAYMENT) {
            var drone = droneService.getUnoccupied();
            drone.setStatus(DroneStatus.OCCUPIED);
            order.setOrderStatus(OrderStatus.ON_THE_WAY);
            var droneLog = DroneLog.builder()
                    .drone(drone)
                    .order(order)
                    .build();
            droneService.update(drone);
            orderService.update(order);
            droneLogService.save(droneLog);
        } else throw new OrderStatusException("Order is not suitable for delivery");
    }
}
