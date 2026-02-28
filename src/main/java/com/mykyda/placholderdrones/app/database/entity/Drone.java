package com.mykyda.placholderdrones.app.database.entity;

import com.mykyda.placholderdrones.app.database.enums.DroneStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "drone")
public class Drone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private DroneStatus status = DroneStatus.FREE;

    @Builder.Default
    private int progress = 0;
}
