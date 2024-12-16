package com.example.faultsimulator.fault_simulator_service;

import com.example.faultsimulator.fault_simulator_model.CircuitConnection;
import com.example.faultsimulator.fault_simulator_model.CircuitGraph;
import com.example.faultsimulator.fault_simulator_model.gates.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class CircuitFaultSimulatorService {

    private final CircuitGraph circuitGraph = new CircuitGraph();

    /**
     * Runs full simulation, parses file then runs serial and parallel fault
     * simulations and gives the results
     */
    public String runSimulation(MultipartFile file) {
        return "";
    }

    /**
     * Generates a list of stuck-at faults for all connections in the circuit.
     */
    public List<Fault> generateFaultList() {
        List<Fault> faultList = new ArrayList<>();

        for (CircuitConnection connection : circuitGraph.getCircuitConnections().values()) {
            faultList.add(new Fault(connection.getId(), false)); // Stuck-at-0
            faultList.add(new Fault(connection.getId(), true));  // Stuck-at-1
        }

        return faultList;
    }

    /**
     * Evaluates the circuit without faults using a given test vector.
     */
    public List<Boolean> evaluateFaultFreeCircuit(List<Boolean> inputValues) throws Exception {
        // Reset all stuck faults
        for (CircuitConnection connection : circuitGraph.getCircuitConnections().values()) {
            connection.setStuck(false, false);
        }

        circuitGraph.evaluate(inputValues);
        List<Boolean> test = circuitGraph.getPrimaryOutputsValues();
        return test;
    }

    /**
     * Runs fault simulation by injecting faults and comparing results to the fault-free circuit.
     */

    public Map<String, List<Boolean>> runSerialFaultSimulation() throws Exception {
        // List to store undetectable faults
        List<Fault> undetectableFaults = new ArrayList<>();
        int detectedFaultsCount = 0;

        // Generate all possible test vectors
        int primaryInputSize = circuitGraph.getPrimaryInputs().size();
        List<List<Boolean>> testVectors = generateAllTestVectors(primaryInputSize);

        // Generate all possible faults
        List<Fault> faultList = generateFaultList();

        // Map to store results for each fault
        Map<String, List<Boolean>> faultResults = new HashMap<>();

        // Start timer
        long startTime = System.currentTimeMillis();

        // Iterate over each fault
        for (Fault fault : faultList) {
            boolean faultDetected = false;

            // Iterate over all test vectors
            for (List<Boolean> testVector : testVectors) {
                // Step 1: Simulate fault-free circuit
                List<Boolean> faultFreeOutputs = evaluateFaultFreeCircuit(testVector);

                // Step 2: Inject the fault
                CircuitConnection connection = circuitGraph.getCircuitConnections().get(fault.getNodeId());
                if (connection != null) {
                    connection.setStuck(true, fault.getStuckValue());
                } else {
                    throw new IllegalArgumentException("Invalid fault node ID: " + fault.getNodeId());
                }

                // Step 3: Simulate faulty circuit
                circuitGraph.evaluate(testVector);
                List<Boolean> faultyOutputs = circuitGraph.getPrimaryOutputsValues();

                // Step 4: Compare fault-free and faulty outputs
                if (!faultFreeOutputs.equals(faultyOutputs)) {
                    faultDetected = true;
                    detectedFaultsCount++;

                    // Store the result for this fault
                    String faultKey = "Node " + fault.getNodeId() + "_StuckAt" + (fault.getStuckValue() ? "1" : "0");
                    faultResults.put(faultKey, faultyOutputs);

                    break; // Stop testing this fault as it is detected
                }

                // Clear the fault for the next test
                connection.setStuck(false, false);
            }

            // If fault was not detected by any test vector, add it to undetectable faults
            if (!faultDetected) {
                undetectableFaults.add(fault);
            }
        }

        // Stop timer
        long endTime = System.currentTimeMillis();
        double totalTimeInSeconds = (endTime - startTime) / 1000.0;

        // Calculate fault coverage
        int totalFaults = faultList.size();
        double faultCoverage = (double) detectedFaultsCount / totalFaults;

        // Print simulation results
        System.out.println("Simulation Time: " + totalTimeInSeconds + " seconds");
        System.out.println("Fault Coverage: " + (faultCoverage * 100) + "%");
        System.out.println("Total Faults: " + totalFaults);
        System.out.println("Detected Faults: " + detectedFaultsCount);
        System.out.println("Undetectable Faults: " + undetectableFaults.size());

        return faultResults;
    }

    /**
     * Runs fault simulation by injecting faults in parallel using Java Multithreading and comparing results to the fault-free circuit.
     */
    public Map<String, List<Boolean>> runParallelFaultSimulation() throws Exception {
        // List to store undetectable faults
        List<Fault> undetectableFaults = Collections.synchronizedList(new ArrayList<>());
        AtomicInteger detectedFaultsCount = new AtomicInteger(0);

        // Generate all possible test vectors
        int primaryInputSize = circuitGraph.getPrimaryInputs().size();
        List<List<Boolean>> testVectors = generateAllTestVectors(primaryInputSize);

        // Generate all possible faults
        List<Fault> faultList = generateFaultList();

        // Map to store results for each fault
        Map<String, List<Boolean>> faultResults = Collections.synchronizedMap(new HashMap<>());

        // Start timer
        long startTime = System.currentTimeMillis();

        // Create a thread pool
        int numThreads = Runtime.getRuntime().availableProcessors(); // Use available CPU cores
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        // Submit tasks for parallel execution
        for (Fault fault : faultList) {
            executor.submit(() -> {
                boolean faultDetected = false;

                try {
                    // Iterate over all test vectors
                    for (List<Boolean> testVector : testVectors) {
                        // Step 1: Simulate fault-free circuit
                        List<Boolean> faultFreeOutputs = evaluateFaultFreeCircuit(testVector);

                        // Step 2: Inject the fault
                        CircuitConnection connection = circuitGraph.getCircuitConnections().get(fault.getNodeId());
                        if (connection != null) {
                            connection.setStuck(true, fault.getStuckValue());
                        } else {
                            throw new IllegalArgumentException("Invalid fault node ID: " + fault.getNodeId());
                        }

                        // Step 3: Simulate faulty circuit
                        circuitGraph.evaluate(testVector);
                        List<Boolean> faultyOutputs = circuitGraph.getPrimaryOutputsValues();

                        // Step 4: Compare fault-free and faulty outputs
                        if (!faultFreeOutputs.equals(faultyOutputs)) {
                            faultDetected = true;
                            detectedFaultsCount.incrementAndGet();

                            // Store the result for this fault
                            String faultKey = "Node " + fault.getNodeId() + "_StuckAt" + (fault.getStuckValue() ? "1" : "0");
                            faultResults.put(faultKey, faultyOutputs);

                            break; // Stop testing this fault as it is detected
                        }

                        // Clear the fault for the next test
                        connection.setStuck(false, false);
                    }

                    // If fault was not detected by any test vector, add it to undetectable faults
                    if (!faultDetected) {
                        undetectableFaults.add(fault);
                    }
                } catch (Exception e) {
                    e.printStackTrace(); // Handle any unexpected exceptions
                }
            });
        }

        // Shutdown executor and wait for all tasks to finish
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS); // Set a long timeout to ensure all tasks finish

        // Stop timer
        long endTime = System.currentTimeMillis();
        double totalTimeInSeconds = (endTime - startTime) / 1000.0;

        // Calculate fault coverage
        int totalFaults = faultList.size();
        double faultCoverage = (double) detectedFaultsCount.get() / totalFaults;

        // Print simulation results
        System.out.println("Parallel Simulation Time: " + totalTimeInSeconds + " seconds");
        System.out.println("Fault Coverage: " + (faultCoverage * 100) + "%");
        System.out.println("Total Faults: " + totalFaults);
        System.out.println("Detected Faults: " + detectedFaultsCount.get());
        System.out.println("Undetectable Faults: " + undetectableFaults.size());

        return faultResults;
    }

    /**
     * Generates all possible test vectors for a given number of inputs.
     */
    private List<List<Boolean>> generateAllTestVectors(int inputSize) {
        int numVectors = 1 << inputSize; // 2^inputSize
        List<List<Boolean>> testVectors = new ArrayList<>();

        for (int i = 0; i < numVectors; i++) {
            List<Boolean> vector = new ArrayList<>();
            for (int j = inputSize - 1; j >= 0; j--) {
                vector.add((i & (1 << j)) != 0); // Add true or false based on bit
            }
            testVectors.add(vector);
        }

        return testVectors;
    }


    /**
     * Parses the uploaded benchmark file.
     */
    public void parseFile(MultipartFile file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                parseLine(line.trim());
            }
        }
        validateParsedCircuit(); // Additional circuit validation
        System.out.println("Parsing completed successfully.");
    }

    private void parseLine(String line) {
        line = line.split("#")[0].trim(); // Remove comments
        if (line.isEmpty()) return;

        if (line.startsWith("INPUT")) {
            int inputId = extractId(line);
            CircuitConnection temp = new CircuitConnection(inputId);
            circuitGraph.addPrimaryInput(temp);
            circuitGraph.addCircuitConnection(temp);
        } else if (line.startsWith("OUTPUT")) {
            int outputId = extractId(line);
            CircuitConnection temp = new CircuitConnection(outputId);
            circuitGraph.addPrimaryOutput(temp);
            circuitGraph.addCircuitConnection(temp);
        } else if (line.contains("=")) {
            parseGateLine(line);
        } else {
            throw new IllegalArgumentException("Invalid line format: " + line);
        }
    }

    private void parseGateLine(String line) {
        try {
            String[] parts = line.split("=");
            int outputId = Integer.parseInt(parts[0].trim());

            String gateInfo = parts[1].trim();
            String gateType = gateInfo.substring(0, gateInfo.indexOf('(')).trim();
            String[] inputIds = gateInfo.substring(gateInfo.indexOf('(') + 1, gateInfo.indexOf(')'))
                    .split("\\s*,\\s*");

            List<CircuitConnection> inputs = new ArrayList<>();

            for (String id : inputIds) {

                if(circuitGraph.getCircuitConnections().containsKey(Integer.parseInt(id.trim()))){
                    CircuitConnection temp2= circuitGraph.getCircuitConnections().get(Integer.parseInt(id.trim()));
                    inputs.add(temp2);
                }
                else {
                    CircuitConnection temp = new CircuitConnection(Integer.parseInt(id.trim()));
                    circuitGraph.addCircuitConnection(temp);
                    inputs.add(temp);
                }
            }



            CircuitConnection output;

            if(circuitGraph.getCircuitConnections().containsKey(outputId)){
                CircuitConnection temp2= circuitGraph.getCircuitConnections().get(outputId);
                output = temp2;
            } else {
                output = new CircuitConnection(outputId);
                circuitGraph.addCircuitConnection(output);
            }
            circuitGraph.addGate(createGate(outputId, gateType, inputs, output));
        } catch (Exception e) {
            System.err.println("Error parsing gate line: " + line);
            e.printStackTrace();
        }
    }

    private int extractId(String line) {
        return Integer.parseInt(line.replaceAll("\\D+", ""));
    }

    private Gate createGate(int id, String type, List<CircuitConnection> inputs, CircuitConnection output) {
        return switch (type.toUpperCase()) {
            case "AND" -> new ANDGate(id, inputs, output);
            case "NAND" -> new NANDGate(id, inputs, output);
            case "OR" -> new ORGate(id, inputs, output);
            case "NOR" -> new NORGate(id, inputs, output);
            case "XOR" -> new XORGate(id, inputs, output);
            case "NOT" -> new NOTGate(id, inputs, output);
            case "BUFF" -> new BUFFGate(id, inputs, output);
            default -> throw new IllegalArgumentException("Unknown gate type: " + type);
        };
    }

    /**
     * Validates the parsed circuit.
     */
    private void validateParsedCircuit() {
        if (circuitGraph.getPrimaryInputs().isEmpty()) {
            throw new IllegalStateException("No primary inputs found in the circuit.");
        }
        if (circuitGraph.getPrimaryOutputs().isEmpty()) {
            throw new IllegalStateException("No primary outputs found in the circuit.");
        }
        if (circuitGraph.getGates().isEmpty()) {
            throw new IllegalStateException("No gates found in the circuit.");
        }
    }

    public CircuitGraph getCircuitGraph() {
        return circuitGraph;
    }

    /**
     * Inner class to represent a fault.
     */
    public static class Fault {
        private final int nodeId;
        private final boolean stuckValue;

        public Fault(int nodeId, boolean stuckValue) {
            this.nodeId = nodeId;
            this.stuckValue = stuckValue;
        }

        public int getNodeId() {
            return nodeId;
        }

        public boolean getStuckValue() {
            return stuckValue;
        }

        @Override
        public String toString() {
            return "Fault{Node ID=" + nodeId + ", Stuck Value=" + stuckValue + "}";
        }
    }
}
