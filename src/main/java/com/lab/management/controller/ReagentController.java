package com.lab.management.controller;

import com.lab.management.entity.Reagent;
import com.lab.management.service.inventory.ReagentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reagents")
public class ReagentController {

    @Autowired
    private ReagentService reagentService;

    // VIOLATION: Code Sustainability - No pagination
    @GetMapping
    public ResponseEntity<List<Reagent>> getAllReagents() {
        return ResponseEntity.ok(reagentService.getAllReagents());
    }

    // VIOLATION: Code Sustainability - String status field
    @GetMapping("/low-stock")
    public ResponseEntity<List<Reagent>> getLowStockReagents() {
        return ResponseEntity.ok(reagentService.getLowStockReagents());
    }

    // VIOLATION: Code Sustainability - No caching for inventory lookups
    @GetMapping("/{reagentCode}/quantity")
    public ResponseEntity<Integer> getQuantity(@PathVariable String reagentCode) {
        Integer quantity = reagentService.getReagentQuantity(reagentCode);
        return ResponseEntity.ok(quantity);
    }
}
