package com.example.faultsimulator.fault_simulator_service;

import com.example.faultsimulator.fault_simulator_model.CircuitConnection;
import com.example.faultsimulator.fault_simulator_model.CircuitGraph;
import com.example.faultsimulator.fault_simulator_model.gates.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class CircuitFaultSimulatorService {
    private final CircuitGraph circuitGraph = new CircuitGraph();

    public void parseFile(MultipartFile file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                parseLine(line);
            }
        }
    }

    private void parseLine(String line) {
        System.out.println("Parsing line: " + line);
        line = line.split("#")[0].trim(); // Remove comments and trim spaces
        if (line.isEmpty()) return;

        if (line.startsWith("INPUT")) {
            int inputId = Integer.parseInt(line.replaceAll("\\D+", ""));
            CircuitConnection input = new CircuitConnection(inputId);
            circuitGraph.addPrimaryInput(input);
        } else if (line.startsWith("OUTPUT")) {
            int outputId = Integer.parseInt(line.replaceAll("\\D+", ""));
            CircuitConnection output = new CircuitConnection(outputId);
            circuitGraph.addPrimaryOutput(output);
        } else if (line.contains("=")) {
            String[] parts = line.split("=");
            int outputId = Integer.parseInt(parts[0].trim());
            String[] gateParts = parts[1].trim().split("\\(");
            String gateType = gateParts[0];
            String[] inputIds = gateParts[1].replace(")", "").split(",");

            List<CircuitConnection> inputs = new ArrayList<>();
            for (String id : inputIds) {
                inputs.add(new CircuitConnection(Integer.parseInt(id.trim())));
            }

            CircuitConnection output = new CircuitConnection(outputId);
            circuitGraph.addGate(createGate(outputId, gateType, inputs, output));
        }
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

    public void evaluateCircuit(List<Boolean> inputValues) throws Exception {
        circuitGraph.evaluate(inputValues);
    }

    public CircuitGraph getCircuitGraph() {
        return circuitGraph;
    }
}
