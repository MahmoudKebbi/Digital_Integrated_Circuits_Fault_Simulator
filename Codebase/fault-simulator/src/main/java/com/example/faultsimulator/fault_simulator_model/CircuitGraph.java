package com.example.faultsimulator.fault_simulator_model;

import com.example.faultsimulator.fault_simulator_model.gates.Gate;

import java.util.*;

public class CircuitGraph {
    private final Map<Integer, Gate> gates = new HashMap<>();
    private final Map<Integer, CircuitConnection> circuitConnections = new HashMap<>();
    private final List<CircuitConnection> primaryInputs = new ArrayList<>();
    private final List<CircuitConnection> primaryOutputs = new ArrayList<>();

    /**
     * Add a primary input connection if it doesn't already exist.
     */
    public void addPrimaryInput(CircuitConnection input) {
        if (!primaryInputs.contains(input)) {
            primaryInputs.add(input);
        } else {
            System.err.println("Duplicate primary input detected: " + input.getId());
        }
    }

    /**
     * Add a primary output connection if it doesn't already exist.
     */
    public void addPrimaryOutput(CircuitConnection output) {
        if (!primaryOutputs.contains(output)) {
            primaryOutputs.add(output);
        } else {
            System.err.println("Duplicate primary output detected: " + output.getId());
        }
    }

    /**
     * Add a gate to the circuit graph, ensuring no duplicate gate IDs.
     */
    public void addGate(Gate gate) {
        if (gates.containsKey(gate.getId())) {
            System.err.println("Duplicate Gate ID Detected: " + gate.getId());
            return;
        }
        gates.put(gate.getId(), gate);
    }

    /**
     * Add a connection to the circuit graph, ensuring no duplicate gate IDs.
     */
    public void addCircuitConnection(CircuitConnection connection) {
        if (!circuitConnections.containsKey(connection.getId())) {
            circuitConnections.put(connection.getId(), connection);
        }
    }

    /**
     * Get the list of primary input connections.
     */
    public List<CircuitConnection> getPrimaryInputs() {
        return primaryInputs;
    }

    /**
     * Get the list of primary output connections.
     */
    public List<CircuitConnection> getPrimaryOutputs() {
        return primaryOutputs;
    }

    /**
     * Get the map of gates in the circuit graph.
     */
    public Map<Integer, Gate> getGates() {
        return gates;
    }

    /**
     * Get the map of the connections in the circuit graph.
     */
    public Map<Integer, CircuitConnection> getCircuitConnections() {
        return circuitConnections;
    }

    /**
     * Evaluate the circuit using the provided input values.
     */
    public void evaluate(List<Boolean> primaryInputValues) throws Exception {
        if (primaryInputValues.size() != primaryInputs.size()) {
            throw new IllegalArgumentException("Input values count does not match primary inputs.");
        }

        // Set values for primary inputs
        for (int i = 0; i < primaryInputs.size(); i++) {

            if(!primaryInputs.get(i).isStuck()) {
                primaryInputs.get(i).setValue(primaryInputValues.get(i));
            }

            System.out.println("Input " + primaryInputs.get(i).getId() + " = " + primaryInputs.get(i).getValue());
        }

        // Evaluate each gate
        for (Gate gate : gates.values()) {
            gate.evaluateOutput(gate.getInputs(), gate.getOutput());
        }
    }

    /**
     * Get the list of primary output values.
     */
    public List<Boolean> getPrimaryOutputsValues() {
        List<Boolean> outputValues = new ArrayList<>();
        for (CircuitConnection output : primaryOutputs) {
            outputValues.add(output.getValue());
        }
        return outputValues;
    }

    /**
     * Print a summary of the circuit graph.
     */
    public void printSummary() {
        System.out.println("Primary Inputs: " + primaryInputs.size());
        System.out.println("Primary Outputs: " + primaryOutputs.size());
        System.out.println("Total Gates: " + gates.size());
    }
}
