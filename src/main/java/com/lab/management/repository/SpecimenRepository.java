package com.lab.management.repository;

import com.lab.management.entity.Specimen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

// VIOLATION: Code Sustainability - No pagination, findAll() without Pageable
// VIOLATION: Code Sustainability - N+1 query issue with eager loading
@Repository
public interface SpecimenRepository extends JpaRepository<Specimen, Long> {

    // VIOLATION: Code Sustainability - No index on status field, full table scan
    List<Specimen> findByStatus(String status);

    // VIOLATION: Code Sustainability - No pagination support for large result sets
    List<Specimen> findAll();

    // VIOLATION: Code Sustainability - Lazy loading means N+1 if not careful
    List<Specimen> findByCollectionLocationIgnoreCase(String location);
}
