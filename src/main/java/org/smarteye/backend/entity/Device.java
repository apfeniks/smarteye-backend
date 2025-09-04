package org.smarteye.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "devices")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Device {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String type;
    private String serial;
    private String status;
}
