package com.mykyda.placholderdrones.app.service;

import com.mykyda.placholderdrones.app.DTO.demo.DroneDTO;
import com.mykyda.placholderdrones.app.DTO.demo.DroneLogDTO;
import com.mykyda.placholderdrones.app.DTO.demo.OrderDTO;
import com.mykyda.placholderdrones.app.database.entity.Drone;
import com.mykyda.placholderdrones.app.database.entity.Order;
import com.mykyda.placholderdrones.app.database.enums.DroneStatus;
import com.mykyda.placholderdrones.app.database.repository.DroneLogRepository;
import com.mykyda.placholderdrones.app.database.repository.DroneRepository;
import com.mykyda.placholderdrones.app.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DroneService {

    private final DroneRepository droneRepository;

    private final DroneLogRepository droneLogRepository;

    private final OrderService orderService;

    @Value("${placeholder-drones.delivery.origin.latitude}")
    private BigDecimal ORIGIN_LATITUDE;

    @Value("${placeholder-drones.delivery.origin.longitude}")
    private BigDecimal ORIGIN_LONGITUDE;

    public List<Drone> getAll() {
        return droneRepository.findAll();
    }

    @Transactional(readOnly = true)
    public DroneDTO getById(Long id) {
        var drone = droneRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No such drone with id " + id));
        var logs = new ArrayList<DroneLogDTO>();
        droneLogRepository.findAllByDroneIdOrderByFinishedAtDesc(drone.getId())
                .forEach(log -> logs.add(DroneLogDTO.builder()
                        .droneId(log.getDrone().getId())
                        .order(OrderDTO.builder()
                                .id(log.getOrder().getId())
                                .latitude(log.getOrder().getLatitude())
                                .longitude(log.getOrder().getLongitude())
                                .orderStatus(log.getOrder().getOrderStatus())
                                .receiverEmail(log.getOrder().getReceiverEmail())
                                .build())
                        .startedAt(log.getStartedAt())
                        .status(log.getDeliveryStatus())
                        .finishedAt(log.getFinishedAt())
                        .build()));
        var currentOrder = orderService.findById(drone.getLastOrderId());
        return DroneDTO.builder()
                .logs(logs)
                .status(drone.getStatus())
                .currentLatitude(drone.getCurrentLatitude())
                .currentLongitude(drone.getCurrentLongitude())
                .currentOrder(currentOrder)
                .progress(drone.getProgress())
                .build();
    }

    @Transactional(readOnly = true)
    public Drone getUnoccupied() {
        return droneRepository.getRandomUnoccupied()
                .orElseThrow(() -> new EntityNotFoundException("No free drones right now"));
    }

    public void update(Drone drone) {
        droneRepository.save(drone);
    }

    @Transactional(readOnly = true)
    public List<Drone> getAllReturning() {
        return droneRepository.getAllByStatusIs(DroneStatus.RETURNING);
    }

    @Transactional
    public void progressReturn(List<Drone> drones) {
        for (Drone drone : drones) {

            int newProgress = drone.getProgress() + 10;

            Order lastOrder = orderService.findById(drone.getLastOrderId());

            BigDecimal startLat = lastOrder.getLatitude();
            BigDecimal startLon = lastOrder.getLongitude();

            if (newProgress < 100) {

                BigDecimal progressFactor = BigDecimal.valueOf(newProgress)
                        .divide(BigDecimal.valueOf(100));

                BigDecimal deltaLat = ORIGIN_LATITUDE.subtract(startLat);
                BigDecimal deltaLon = ORIGIN_LONGITUDE.subtract(startLon);

                BigDecimal newLat = startLat
                        .add(deltaLat.multiply(progressFactor));

                BigDecimal newLon = startLon
                        .add(deltaLon.multiply(progressFactor));

                drone.setCurrentLatitude(newLat);
                drone.setCurrentLongitude(newLon);
                drone.setProgress(newProgress);

            } else {
                drone.setStatus(DroneStatus.FREE);
                drone.setProgress(0);

                drone.setCurrentLatitude(ORIGIN_LATITUDE);
                drone.setCurrentLongitude(ORIGIN_LONGITUDE);
            }

            droneRepository.save(drone);
        }
    }

    @Transactional
    public void createDrone(BigDecimal ORIGIN_LATITUDE, BigDecimal ORIGIN_LONGITUDE) {
        droneRepository.save(Drone.builder()
                .currentLatitude(ORIGIN_LATITUDE)
                .currentLongitude(ORIGIN_LONGITUDE)
                .build());
    }
}
