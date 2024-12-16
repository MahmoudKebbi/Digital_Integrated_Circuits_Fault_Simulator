package com.example.faultsimulator.fault_simulator_service;

import com.example.faultsimulator.fault_simulator_model.CircuitGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CircuitFaultSimulatorServiceTest {

    private CircuitFaultSimulatorService service;

    @BeforeEach
    void setUp() throws Exception {
        service = new CircuitFaultSimulatorService();
        // Assuming a valid benchmark file is parsed for testing
        String testBenchContent = """
                INPUT(1)
                INPUT(2)
                INPUT(3)
                OUTPUT(4)
                OUTPUT(5)
                4 = AND(1,2)
                5 = OR(2,3)
                """;
        service.parseTestFile(testBenchContent);
    }

    /**
     * Parses a test benchmark string directly for testing purposes.
     */
    private void parseTestFile(String content) throws Exception {
        service.parseFile(new MockMultipartFile("test.bench", content.getBytes()));
    }

    @Test
    void testEvaluateFaultFreeCircuit() throws Exception {
        // Define test vector: Inputs for the circuit
        List<Boolean> testVector = Arrays.asList(true, false, true);

        // Run fault-free evaluation
        List<Boolean> outputs = service.evaluateFaultFreeCircuit(testVector);

        // Expected outputs for the given test vector
        assertEquals(Arrays.asList(false, true), outputs, "Fault-free evaluation failed");
    }

    @Test
    void testRunFaultSimulation() throws Exception {
        // Define a test vector
        List<Boolean> testVector = Arrays.asList(true, false, true);

        // Run fault simulation
        Map<String, List<Boolean>> faultResults = service.runFaultSimulation(testVector);

        // Golden outputs (fault-free)
        List<Boolean> goldenOutputs = service.evaluateFaultFreeCircuit(testVector);
        System.out.println("Golden Outputs: " + goldenOutputs);

        // Verify that fault results differ from the golden outputs for injected faults
        faultResults.forEach((faultKey, faultyOutputs) -> {
            System.out.println("Fault: " + faultKey + ", Outputs: " + faultyOutputs);
            assertNotEquals(goldenOutputs, faultyOutputs, "Fault at " + faultKey + " did not affect outputs!");
        });
    }

    @Test
    void testGenerateFaultList() {
        List<CircuitFaultSimulatorService.Fault> faultList = service.generateFaultList();

        // Verify that the fault list contains expected stuck-at-0 and stuck-at-1 faults
        assertFalse(faultList.isEmpty(), "Fault list is empty!");
        faultList.forEach(fault ->
                System.out.println("Fault: Node " + fault.getNodeId() + ", Stuck Value: " + fault.getStuckValue()));
    }
}
