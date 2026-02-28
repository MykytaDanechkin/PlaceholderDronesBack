package com.mykyda.placholderdrones.app.service;

import com.mykyda.placholderdrones.app.DTO.demo.DroneLogDTO;
import com.mykyda.placholderdrones.app.DTO.demo.OrderDTO;
import com.mykyda.placholderdrones.app.database.entity.Drone;
import com.mykyda.placholderdrones.app.database.repository.DroneLogRepository;
import com.mykyda.placholderdrones.app.database.repository.DroneRepository;
import com.mykyda.placholderdrones.app.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DroneService {

    private final DroneRepository droneRepository;

    private final DroneLogRepository droneLogRepository;

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
        return DroneDTO.builder()
                .logs(logs)
                .status(drone.getStatus())
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
    public Drone getRandomReturning() {
        return droneRepository.getRandomReturning().orElse(null);
    }
}
