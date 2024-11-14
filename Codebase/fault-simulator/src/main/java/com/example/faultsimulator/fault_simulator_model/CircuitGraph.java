package com.example.faultsimulator.fault_simulator_model;

import com.example.faultsimulator.fault_simulator_model.gates.*;

import java.util.*;


public class CircuitGraph {
    private Map<Integer, Gate> nodes = new HashMap<>();
    private Map<Integer, CircuitConnection> connections = new HashMap<>();

    public void addConnection(CircuitConnection connection) {
        connections.put(connection.getId(), connection);
    }

    public void addGate(Gate gate) {
        nodes.put(gate.getId(), gate);
    }

    public void evaluate() {
        for (Gate gate : nodes.values()) {
            gate.evaluateOutput(gate.getInputs(), gate.getOutput());;
        }

        nodes.values().forEach(gate ->
                System.out.println("Gate " + gate.getId() + " Output: " + gate.getOutput().getValue())
        );
    }

    public void addInput(CircuitConnection input) {
        connections.put(input.getId(), input);
    }
    public void addOutput(CircuitConnection output) {
        connections.put(output.getId(), output);
    }
}
