package com.lab.management.repository;

import com.lab.management.entity.Reagent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

// VIOLATION: Code Sustainability - No index on reagentName, reagentCode
@Repository
public interface ReagentRepository extends JpaRepository<Reagent, Long> {

    // VIOLATION: Code Sustainability - No index despite frequent searches
    Optional<Reagent> findByReagentCode(String reagentCode);

    // VIOLATION: Code Sustainability - String status, full table scan
    List<Reagent> findByStatus(String status);

    // VIOLATION: Code Sustainability - No pagination for inventory list
    List<Reagent> findAll();
}
