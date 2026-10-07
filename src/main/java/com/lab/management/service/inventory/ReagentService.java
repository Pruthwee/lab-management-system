package com.lab.management.service.inventory;

import com.lab.management.entity.Reagent;
import com.lab.management.repository.ReagentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReagentService {

    @Autowired
    private ReagentRepository reagentRepository;

    // VIOLATION: Code Sustainability - No pagination
    public List<Reagent> getAllReagents() {
        return reagentRepository.findAll();
    }

    // VIOLATION: Code Sustainability - String status instead of enum
    public List<Reagent> getLowStockReagents() {
        List<Reagent> all = reagentRepository.findByStatus("Low Stock");
        return all;  // Could filter application-side
    }

    // VIOLATION: Code Sustainability - No caching for inventory checks
    public Integer getReagentQuantity(String reagentCode) {
        Optional<Reagent> reagent = reagentRepository.findByReagentCode(reagentCode);
        return reagent.map(Reagent::getQuantityOnHand).orElse(0);  // No cache
    }
}
