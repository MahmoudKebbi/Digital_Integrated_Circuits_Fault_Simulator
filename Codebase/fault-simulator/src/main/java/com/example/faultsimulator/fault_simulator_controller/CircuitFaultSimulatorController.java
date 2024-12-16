package com.example.faultsimulator.fault_simulator_controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import com.example.faultsimulator.fault_simulator_service.CircuitFaultSimulatorService;

@RestController
@RequestMapping("/api/circuits")
public class CircuitFaultSimulatorController {


    private final CircuitFaultSimulatorService circuitService;
    @Autowired
    public CircuitFaultSimulatorController(CircuitFaultSimulatorService circuitService) {
        this.circuitService = circuitService;
    }

    // Endpoint to upload a file
    public ResponseEntity<String> simulateCircuit(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty. Please upload a valid file.");
            }
            // Parse the file and run the simulation
            String simulationResults = circuitService.runSimulation(file);

            // Return the simulation results as the response
            return ResponseEntity.ok("Simulation completed successfully. Results:\n" + simulationResults);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while running the simulation.");
        }
    }

}