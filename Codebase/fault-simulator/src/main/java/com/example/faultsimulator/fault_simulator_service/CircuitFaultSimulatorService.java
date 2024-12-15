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
                    inputs.add(circuitGraph.getCircuitConnections().get(id));
                }
                else {
                    CircuitConnection temp = new CircuitConnection(Integer.parseInt(id.trim()));
                    circuitGraph.addCircuitConnection(temp);
                    inputs.add(temp);
                }
            }

            CircuitConnection output = new CircuitConnection(outputId);
            circuitGraph.addCircuitConnection(output);
            circuitGraph.addGate(createGate(outputId, gateType, inputs, output));
        } catch (Exception e) {
            System.err.println("Error parsing gate line: " + line);
            e.printStackTrace();
        }
    }

    private int extractId(String line) {
        return Integer.parseInt(line.replaceAll("\\D+", "")); // Extract digits
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
     * Evaluates the circuit using the provided input values.
     */
    public void evaluateCircuit(List<Boolean> inputValues) throws Exception {
        circuitGraph.evaluate(inputValues);
    }

    /**
     * Performs additional validation on the parsed circuit.
     */
    private void validateParsedCircuit() {
        System.out.println("Validating parsed circuit...");
        System.out.println("Primary Inputs: " + circuitGraph.getPrimaryInputs().size());
        System.out.println("Primary Outputs: " + circuitGraph.getPrimaryOutputs().size());
        System.out.println("Total Gates: " + circuitGraph.getGates().size());

        // Check if the circuit graph has primary inputs, outputs, and gates
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
}
