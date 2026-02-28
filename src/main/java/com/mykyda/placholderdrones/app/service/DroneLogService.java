package com.mykyda.placholderdrones.app.service;

import com.mykyda.placholderdrones.app.database.entity.DroneLog;
import com.mykyda.placholderdrones.app.database.repository.DroneLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DroneLogService {

    private final DroneLogRepository droneLogRepository;

    public List<DroneLog> getByDroneId(Long id) {
        return droneLogRepository.findAllByDroneIdOrderByFinishedAtDesc(id);
    }

    public void save(DroneLog droneLog) {
        droneLogRepository.save(droneLog);
    }
}
