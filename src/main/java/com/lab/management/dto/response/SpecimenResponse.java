package com.lab.management.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpecimenResponse {
    private Long specimenId;
    private String specimenType;
    private String collectionLocation;
    private LocalDateTime collectionDateTime;
    private String status;
}
