package com.example.faultsimulator.fault_simulator_service;

import com.example.faultsimulator.fault_simulator_model.CircuitConnection;
import com.example.faultsimulator.fault_simulator_model.CircuitGraph;
import com.example.faultsimulator.fault_simulator_model.gates.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.util.*;

@Service
public class CircuitFaultSimulatorService {
    private CircuitGraph circuitGraph = new CircuitGraph();

    public void parseFile(MultipartFile file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                parseLine(line);
            }
        }
    }

    private void parseLine(String line) {
        // Remove everything after and including '#' character (handle inline comments)
        int commentIndex = line.indexOf("#");
        if (commentIndex != -1) {
            line = line.substring(0, commentIndex).trim();  // Get everything before the '#', and trim any extra whitespace
        }

        // Skip the line if it's empty after removing the comment
        if (line.isEmpty()) {
            return;
        }

        // Now proceed with splitting the line by whitespace and processing it
        String[] tokens = line.split("\\s+");
        if (tokens.length == 0) return;

        // Handle input lines like "INPUT(1)"
        if (tokens[0].startsWith("INPUT")) {
            int inputId = Integer.parseInt(tokens[0].replaceAll("[^0-9]", ""));  // Remove non-numeric characters to get the pin ID
            CircuitConnection temp = new CircuitConnection(inputId);
            circuitGraph.addConnection(temp); // Add the input to the graph
            circuitGraph.addPrimaryInput(temp);
        }
        // Handle output lines like "OUTPUT(123)"
        else if (tokens[0].startsWith("OUTPUT")) {
            int outputId = Integer.parseInt(tokens[0].replaceAll("[^0-9]", "")); // Get the output ID
            CircuitConnection temp = new CircuitConnection(outputId);
            circuitGraph.addConnection(temp); // Add the output to the graph
            circuitGraph.addPrimaryOutput(temp);
        }
        // Handle gate definitions (AND, OR, etc.)
        else if (tokens.length >= 3) {
            String gateType = tokens[1].toUpperCase();  // Gate type is the second token (AND, OR, etc.)
            int gateId = Integer.parseInt(tokens[0]);   // First token is the gate ID

            // Extract the output ID (e.g., "123" in "123 = AND(1,2)")
            int outputId = gateId; // Assuming the output ID is the same as the gate ID

            List<CircuitConnection> inputs = new ArrayList<>();

            // Parse the inputs from the gate definition (e.g., AND(1, 2))
            String[] inputTokens = tokens[2].substring(tokens[2].indexOf('(') + 1, tokens[2].indexOf(')')).split(",");
            for (String inputToken : inputTokens) {
                int inputId = Integer.parseInt(inputToken.trim());
                inputs.add(new CircuitConnection(inputId));  // Add input connections to the gate
            }

            // Create and add the gate to the circuit graph
            CircuitConnection outputConnection = new CircuitConnection(outputId);
            circuitGraph.addConnection(outputConnection);
            Gate gate = createGate(gateId, gateType, inputs, outputConnection);
            circuitGraph.addGate(gate);
        }
    }

    private Gate createGate(int gateId, String gateType, List<CircuitConnection> inputs,CircuitConnection output) {
        switch (gateType) {
            case "AND":
                return new ANDGate(gateId, inputs, output);
            case "OR":
                return new ORGate(gateId, inputs, output);
            case "NAND":
                return new NANDGate(gateId, inputs, output);
            case "NOR":
                return new NORGate(gateId, inputs, output);
            case "XOR":
                return new XORGate(gateId, inputs, output);
            case "NOT":
                return new NOTGate(gateId, inputs, output);
            case "BUFF":
                return new BUFFGate(gateId, inputs, output);  // Buffer gate if defined
            default:
                throw new IllegalArgumentException("Unsupported gate type: " + gateType);
        }
    }
    // Method to evaluate the circuit (run the gates)
    public void evaluateCircuit(List<Boolean> inputVales) throws Exception {
        circuitGraph.evaluate(inputVales);
    }

    // Getter for the circuit graph (if needed for external usage)
    public CircuitGraph getCircuitGraph() {
        return circuitGraph;
    }
}
