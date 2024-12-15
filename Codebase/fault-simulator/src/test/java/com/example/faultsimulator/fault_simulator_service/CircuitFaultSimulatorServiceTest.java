package com.example.faultsimulator.fault_simulator_service;

import com.example.faultsimulator.fault_simulator_model.CircuitConnection;
import com.example.faultsimulator.fault_simulator_model.gates.ANDGate;
import com.example.faultsimulator.fault_simulator_model.CircuitGraph;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.io.InputStream;

class CircuitFaultSimulatorServiceTest {
    @Test
    void parseFile() throws Exception {
        // Initialize service
        CircuitFaultSimulatorService service = new CircuitFaultSimulatorService();

        // Correct the path to the c17.bench.txt file under src/main/resources/Netlists/
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("Netlists/c17.bench.txt");

        if (inputStream == null) {
            throw new IllegalArgumentException("File not found!");
        }

        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "circuit.bench", "text/plain", inputStream.readAllBytes()
        );

        // Parse the file
        service.parseFile(mockFile);

        // Verify circuit graph structure
        CircuitGraph circuitGraph = service.getCircuitGraph();
        assertNotNull(circuitGraph, "CircuitGraph should not be null.");
        assertEquals(5, circuitGraph.getPrimaryInputs().size(), "There should be 5 primary inputs.");
        assertEquals(2, circuitGraph.getPrimaryOutputs().size(), "There should be 2 primary outputs.");
        assertEquals(6, circuitGraph.getGates().size(), "There should be 6 gates.");  // Correct method call

        // Print for debug (optional)
        System.out.println("Parsing Successful: Primary Inputs, Outputs, and Gates are as expected.");
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

        // Add an AND gate
        circuitGraph.addGate(new ANDGate(1, Arrays.asList(input1, input2), output));

        // Provide input values and evaluate the circuit
        List<Boolean> inputValues = Arrays.asList(true, false); // Inputs: true, false
        service.evaluateCircuit(inputValues);

        // Verify output
        List<Boolean> outputs = circuitGraph.getPrimaryOutputsValues();
        assertNotNull(outputs, "Outputs should not be null.");
        assertEquals(1, outputs.size(), "There should be 1 output.");
        assertEquals(false, outputs.get(0), "AND gate should output false for inputs true and false.");
    }

    @Test
    void getCircuitGraph() {
        // Initialize service
        CircuitFaultSimulatorService service = new CircuitFaultSimulatorService();

        // Retrieve the circuit graph
        CircuitGraph circuitGraph = service.getCircuitGraph();

        // Verify the circuit graph is not null
        assertNotNull(circuitGraph, "CircuitGraph should not be null.");
        assertTrue(circuitGraph instanceof CircuitGraph, "CircuitGraph should be of type CircuitGraph.");

        // Print debug info
        System.out.println("getCircuitGraph Test: CircuitGraph successfully retrieved.");
    }

    @Test
    void parseC432File() throws Exception {
        // Initialize service
        CircuitFaultSimulatorService service = new CircuitFaultSimulatorService();

        // Debug: Verify file path
        System.out.println("Loading c432.bench from resources...");

        // Load the c432.bench file
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("Netlists/c432.bench.txt");
        if (inputStream == null) {
            throw new IllegalArgumentException("c432.bench file not found in Netlists directory!");
        }

        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "c432.bench", "text/plain", inputStream.readAllBytes()
        );

        // Debug: Print file content
        System.out.println("File loaded successfully.");
        System.out.println(new String(mockFile.getBytes()));

        // Parse the file
        service.parseFile(mockFile);

        // Verify circuit graph structure
        CircuitGraph circuitGraph = service.getCircuitGraph();
        assertNotNull(circuitGraph, "CircuitGraph should not be null.");
        assertEquals(36, circuitGraph.getPrimaryInputs().size(), "There should be 36 primary inputs.");
        assertEquals(7, circuitGraph.getPrimaryOutputs().size(), "There should be 7 primary outputs.");
        assertEquals(120, circuitGraph.getGates().size(), "There should be 120 gates.");
    }


    @Test
    void evaluateC432Circuit() throws Exception {
        // Initialize service and parse c432.bench
        CircuitFaultSimulatorService service = new CircuitFaultSimulatorService();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("Netlists/c432.bench.txt");
        if (inputStream == null) {
            throw new IllegalArgumentException("c432.bench file not found in Netlists directory!");
        }

        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "c432.bench", "text/plain", inputStream.readAllBytes()
        );
        service.parseFile(mockFile);

        // Get the circuit graph
        CircuitGraph circuitGraph = service.getCircuitGraph();

        // Create test inputs
        List<Boolean> inputValues = Arrays.asList(
                true, false, true, false, true, false, true, false, // Inputs 1-8
                true, false, true, false, true, false, true, false, // Inputs 9-16
                true, false, true, false, true, false, true, false, // Inputs 17-24
                true, false, true, false, true, false, true, false, // Inputs 25-32
                true, false, true, false // Inputs 33-36
        );

        // Evaluate the circuit
        service.evaluateCircuit(inputValues);

        // Get outputs
        List<Boolean> outputs = circuitGraph.getPrimaryOutputsValues();
        assertNotNull(outputs, "Outputs should not be null.");
        assertEquals(7, outputs.size(), "There should be 7 outputs.");

        // Debug information (optional)
        System.out.println("C432 Evaluation Outputs: " + outputs);
    }

}
