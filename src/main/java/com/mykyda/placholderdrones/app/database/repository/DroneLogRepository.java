package com.mykyda.placholderdrones.app.database.repository;

import com.mykyda.placholderdrones.app.database.entity.DroneLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DroneLogRepository extends JpaRepository<DroneLog, Long> {
    List<DroneLog> findAllByDroneIdOrderByFinishedAtDesc(Long droneId);
}
