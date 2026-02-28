package com.mykyda.placholderdrones.app.service;

import com.mykyda.placholderdrones.app.database.enums.DroneStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DroneConsumerService {

    private final DeliveryService deliveryService;

    private final OrderService orderService;

    private final DroneService droneService;

    @Scheduled(fixedDelay = 120000)
    public void completeDelivery(){
        var order = orderService.getRandomOnTheWayOrder();
        if (order == null){
            log.debug("No orders to process");
        } else {
            deliveryService.finishDelivery(order);
        }
    }

    @Scheduled(fixedDelay = 120000, initialDelay = 120000)
    public void droneReturn(){
        var drone = droneService.getRandomReturning();
        if (drone == null){
            log.debug("No returning drones");
        } else {
            drone.setStatus(DroneStatus.FREE);
            droneService.update(drone);
        }
    }
}
