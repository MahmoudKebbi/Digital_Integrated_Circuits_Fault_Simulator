package com.example.faultsimulator.fault_simulator_service;

import com.example.faultsimulator.fault_simulator_model.CircuitConnection;
import com.example.faultsimulator.fault_simulator_model.CircuitGraph;
import com.example.faultsimulator.fault_simulator_model.gates.ANDGate;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CircuitFaultSimulatorServiceTest {

    // Utility method to load files
    private CircuitFaultSimulatorService initializeService(String fileName) throws Exception {
        CircuitFaultSimulatorService service = new CircuitFaultSimulatorService();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("Netlists/" + fileName);
        assertNotNull(inputStream, fileName + " not found in Netlists directory!");

        MockMultipartFile mockFile = new MockMultipartFile(
                "file", fileName, "text/plain", inputStream.readAllBytes()
        );

        service.parseFile(mockFile);
        return service;
    }

    @Test
    void parseFile() throws Exception {
        CircuitFaultSimulatorService service = initializeService("c17.bench.txt");

        CircuitGraph circuitGraph = service.getCircuitGraph();
        assertNotNull(circuitGraph, "CircuitGraph should not be null.");
        assertEquals(5, circuitGraph.getPrimaryInputs().size(), "Expected 5 primary inputs.");
        assertEquals(2, circuitGraph.getPrimaryOutputs().size(), "Expected 2 primary outputs.");
        assertEquals(6, circuitGraph.getGates().size(), "Expected 6 gates.");
    }

    @Test
    void evaluateCircuit() throws Exception {
        CircuitFaultSimulatorService service = new CircuitFaultSimulatorService();
        CircuitGraph circuitGraph = service.getCircuitGraph();

        // Define inputs and outputs
        CircuitConnection input1 = new CircuitConnection(1);
        CircuitConnection input2 = new CircuitConnection(2);
        CircuitConnection output = new CircuitConnection(3);

        circuitGraph.addPrimaryInput(input1);
        circuitGraph.addPrimaryInput(input2);
        circuitGraph.addPrimaryOutput(output);
        circuitGraph.addGate(new ANDGate(1, Arrays.asList(input1, input2), output));

        // Set input values and evaluate
        List<Boolean> inputValues = Arrays.asList(true, false);
        service.evaluateCircuit(inputValues);

        // Verify outputs
        List<Boolean> outputs = circuitGraph.getPrimaryOutputsValues();
        assertNotNull(outputs, "Outputs should not be null.");
        assertEquals(1, outputs.size(), "Expected 1 output.");
        assertFalse(outputs.get(0), "AND gate output should be false.");
    }

    @Test
    void parseC432File() throws Exception {
        CircuitFaultSimulatorService service = initializeService("c432.bench.txt");

        CircuitGraph circuitGraph = service.getCircuitGraph();
        System.out.println("Primary Inputs: " + circuitGraph.getPrimaryInputs().size());
        System.out.println("Primary Outputs: " + circuitGraph.getPrimaryOutputs().size());
        System.out.println("Total Gates: " + circuitGraph.getGates().size());

        assertEquals(36, circuitGraph.getPrimaryInputs().size(), "Expected 36 primary inputs.");
        assertEquals(7, circuitGraph.getPrimaryOutputs().size(), "Expected 7 primary outputs.");
        assertTrue(circuitGraph.getGates().size() >= 120, "Expected at least 120 gates.");
    }

    @Test
    void evaluateC432Circuit() throws Exception {
        CircuitFaultSimulatorService service = initializeService("c432.bench.txt");

        // Define a sample input vector for c432
        List<Boolean> inputValues = Arrays.asList(
                true, false, true, false, true, false, true, false,
                true, false, true, false, true, false, true, false,
                true, false, true, false, true, false, true, false,
                true, false, true, false, true, false, true, false,
                true, false, true, false
        );

        // Evaluate circuit
        service.evaluateCircuit(inputValues);

        // Verify outputs
        List<Boolean> outputs = service.getCircuitGraph().getPrimaryOutputsValues();
        assertNotNull(outputs, "Outputs should not be null.");
        assertEquals(7, outputs.size(), "Expected 7 outputs.");

        System.out.println("C432 Circuit Outputs: " + outputs);
    }
}
