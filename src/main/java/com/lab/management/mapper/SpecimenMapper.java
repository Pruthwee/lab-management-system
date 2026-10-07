package com.lab.management.mapper;

import com.lab.management.dto.request.SpecimenRequest;
import com.lab.management.dto.response.SpecimenResponse;
import com.lab.management.entity.Specimen;
import org.springframework.stereotype.Component;

@Component
public class SpecimenMapper {

    public Specimen toEntity(SpecimenRequest request) {
        if (request == null) {
            return null;
        }

        Specimen specimen = new Specimen();
        specimen.setSpecimenType(request.getSpecimenType());
        specimen.setCollectionLocation(request.getCollectionLocation());
        specimen.setSpecimenImage(request.getSpecimenImage());

        return specimen;
    }

    public SpecimenResponse toResponse(Specimen specimen) {
        if (specimen == null) {
            return null;
        }

        SpecimenResponse response = new SpecimenResponse();
        response.setSpecimenId(specimen.getSpecimenId());
        response.setSpecimenType(specimen.getSpecimenType());
        response.setCollectionLocation(specimen.getCollectionLocation());
        response.setCollectionDateTime(specimen.getCollectionDateTime());
        response.setStatus(specimen.getStatus());

        return response;
    }
}
