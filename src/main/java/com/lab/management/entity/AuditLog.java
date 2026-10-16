package com.lab.management.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

// VIOLATION: Code Sustainability - No indexes on frequently searched audit fields
@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long logId;

    private String action;  // No @Index despite frequent searches
    private String entityName;
    private String entityId;
    private LocalDateTime timestamp;
    private String performedBy;
    private String details;
}
