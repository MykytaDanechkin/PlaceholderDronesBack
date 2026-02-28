package com.mykyda.placholderdrones.app.service;

import com.mykyda.placholderdrones.app.database.entity.DroneLog;
import com.mykyda.placholderdrones.app.database.entity.Order;
import com.mykyda.placholderdrones.app.database.enums.DeliveryStatus;
import com.mykyda.placholderdrones.app.database.enums.DroneStatus;
import com.mykyda.placholderdrones.app.database.enums.OrderStatus;
import com.mykyda.placholderdrones.app.exception.OrderStatusException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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

    @Transactional
    public void progressDelivery(List<Order> orders) {
        for (Order order : orders) {
            var log = droneLogService.getByOrderId(order.getId());
            if (log == null) {
                order.setOrderStatus(OrderStatus.ORDERED);
                orderService.update(order);
            } else {
                var drone = log.getDrone();
                drone.setProgress(drone.getProgress() + 10);
                if (drone.getProgress() >= 100) {
                    drone.setStatus(DroneStatus.RETURNING);
                    drone.setProgress(0);

                    order.setOrderStatus(OrderStatus.DELIVERED);
                    orderService.update(order);

                    log.setFinishedAt(LocalDateTime.now());
                    log.setDeliveryStatus(DeliveryStatus.FINISHED);
                    droneLogService.save(log);
                }
                droneService.update(drone);
            }
        }
    }
}
