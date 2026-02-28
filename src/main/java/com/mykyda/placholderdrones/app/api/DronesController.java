package com.mykyda.placholderdrones.app.api;

import com.mykyda.placholderdrones.app.database.entity.Drone;
import com.mykyda.placholderdrones.app.database.entity.DroneLog;
import com.mykyda.placholderdrones.app.service.DroneDTO;
import com.mykyda.placholderdrones.app.service.DroneLogService;
import com.mykyda.placholderdrones.app.service.DroneService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/drones")
public class DronesController {

    private final DroneService droneService;

    private final DroneLogService droneLogService;

    @GetMapping
    public List<Drone> findAll() {
        return droneService.getAll();
    }

    @GetMapping("/{id}")
    public DroneDTO findByDroneId(@PathVariable Long id) {
        return droneService.getById(id);
    }

    @GetMapping("/log/{id}")
    public List<DroneLog> findLogsByDroneId(@PathVariable Long id) {
        return droneLogService.getByDroneId(id);
    }
}
