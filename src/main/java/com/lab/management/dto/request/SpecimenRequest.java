package com.lab.management.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpecimenRequest {
    private String specimenType;
    private String collectionLocation;
    private byte[] specimenImage;
    // VIOLATION: Code Sustainability - No validation annotations
}
