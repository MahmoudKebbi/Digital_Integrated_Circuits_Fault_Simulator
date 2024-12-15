package com.example.faultsimulator.fault_simulator_service;

import com.example.faultsimulator.fault_simulator_model.CircuitConnection;
import com.example.faultsimulator.fault_simulator_model.CircuitGraph;
import com.example.faultsimulator.fault_simulator_model.gates.ANDGate;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class CircuitFaultSimulatorServiceTest {

    @Test
    void parseFile() throws Exception {
        // Initialize service
        CircuitFaultSimulatorService service = new CircuitFaultSimulatorService();

        // Create a mock ISCAS benchmark file
        String fileContent = """
        INPUT(1)
        INPUT(2)
        OUTPUT(3)
        3 = AND(1, 2)
        """;
        MockMultipartFile mockFile = new MockMultipartFile("file", "circuit.bench", "text/plain", fileContent.getBytes());

        // Parse the file
        service.parseFile(mockFile);

        // Verify circuit graph structure
        CircuitGraph circuitGraph = service.getCircuitGraph();
        assertEquals(2, circuitGraph.getPrimaryInputs().size(), "There should be 2 primary inputs.");
        assertEquals(1, circuitGraph.getPrimaryOutputs().size(), "There should be 1 primary output.");
        assertEquals(1, circuitGraph.getGates().size(), "There should be 1 gate.");
    }


    @Test
    void evaluateCircuit() throws Exception {
        // Initialize service and graph
        CircuitFaultSimulatorService service = new CircuitFaultSimulatorService();
        CircuitGraph circuitGraph = service.getCircuitGraph();

        // Add primary inputs
        CircuitConnection input1 = new CircuitConnection(1);
        CircuitConnection input2 = new CircuitConnection(2);
        circuitGraph.addPrimaryInput(input1);
        circuitGraph.addPrimaryInput(input2);

        // Add primary output
        CircuitConnection output = new CircuitConnection(3);
        circuitGraph.addPrimaryOutput(output);

        // Add gate (AND gate)
        circuitGraph.addGate(new ANDGate(1, Arrays.asList(input1, input2), output));

        // Simulate circuit with inputs
        List<Boolean> inputValues = Arrays.asList(true, false); // Input: true, false
        service.evaluateCircuit(inputValues);

        // Verify output
        List<Boolean> outputs = circuitGraph.getPrimaryOutputsValues();
        assertEquals(Arrays.asList(false), outputs, "The AND gate should output false for inputs true and false.");
    }

    @Test
    void getCircuitGraph() {
//        TODO
    }
}



