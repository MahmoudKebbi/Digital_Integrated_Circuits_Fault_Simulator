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
    public Map<String, List<Boolean>> runFaultSimulation(List<Boolean> testVector) throws Exception {
        Map<String, List<Boolean>> faultResults = new HashMap<>();

        // Step 1: Evaluate fault-free circuit
        List<Boolean> goldenOutputs = evaluateFaultFreeCircuit(testVector);

        // Step 2: Inject faults and evaluate for each connection
        for (CircuitConnection connection : circuitGraph.getCircuitConnections().values()) {
            for (boolean stuckAtValue : Arrays.asList(true, false)) {
                // Inject fault
                connection.setStuck(true, stuckAtValue);
                System.out.println("Injecting fault at Node: " + connection.getId() + ", Fault Value: " + stuckAtValue);

                // Evaluate circuit with fault
                circuitGraph.evaluate(testVector);
                List<Boolean> faultyOutputs = circuitGraph.getPrimaryOutputsValues();

                // Store results
                String faultKey = "Node " + connection.getId() + "_StuckAt" + (stuckAtValue ? "1" : "0");
                faultResults.put(faultKey, new ArrayList<>(faultyOutputs));

                // Clear fault
                connection.setStuck(false, false);
            }
        }

        // Return fault results
        return faultResults;
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
