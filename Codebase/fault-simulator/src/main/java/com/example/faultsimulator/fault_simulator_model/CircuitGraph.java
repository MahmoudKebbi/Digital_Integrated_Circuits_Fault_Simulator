package com.example.faultsimulator.fault_simulator_model;

import com.example.faultsimulator.fault_simulator_model.gates.*;

import java.util.*;

public class CircuitGraph {
    private Map<Integer, Gate> nodes = new HashMap<>();
    private List<CircuitConnection> connections = new ArrayList<>();
    private List<CircuitConnection> PrimaryInputs = new ArrayList<>();
    private List<CircuitConnection> PrimaryOutputs = new ArrayList<>();

    public void addConnection(CircuitConnection connection) {
        connections.add(connection);
    }

    public void addGate(Gate gate) {
        nodes.put(gate.getId(), gate);
    }

    public void addPrimaryInput(CircuitConnection input) {
        PrimaryInputs.add(input);
    }


    public void addPrimaryOutput(CircuitConnection output) {
        PrimaryOutputs.add(output);
    }

    /**
     * Sets the values of the primary inputs based on a provided list of booleans.
     *
     * @param primaryInputValues The list of boolean values representing the primary inputs.
     * @throws Exception If the input size does not match the number of primary inputs.
     */
    public void setPrimaryInputsValues(List<Boolean> primaryInputValues) throws Exception {
        if (primaryInputValues.size() != PrimaryInputs.size()) {
            throw new IllegalArgumentException("Mismatch: Number of inputs provided does not match the number of primary inputs in the circuit.");
        }

        for (int i = 0; i < PrimaryInputs.size(); i++) {
            PrimaryInputs.get(i).setValue(primaryInputValues.get(i));
        }
    }

    public Map<Integer, Gate> getNodes() {
        return nodes;
    }

    public void setNodes(Map<Integer, Gate> nodes) {
        this.nodes = nodes;
    }

    public List<CircuitConnection> getConnections() {
        return connections;
    }

    public void setConnections(List<CircuitConnection> connections) {
        this.connections = connections;
    }

    public List<CircuitConnection> getPrimaryInputs() {
        return PrimaryInputs;
    }

    public void setPrimaryInputs(List<CircuitConnection> primaryInputs) {
        PrimaryInputs = primaryInputs;
    }

    public List<CircuitConnection> getPrimaryOutputs() {
        return PrimaryOutputs;
    }

    public void setPrimaryOutputs(List<CircuitConnection> primaryOutputs) {
        PrimaryOutputs = primaryOutputs;
    }

    public void evaluate(List<Boolean> primaryInputValues) throws Exception {
        // Step 1: Set the primary input values
        setPrimaryInputsValues(primaryInputValues);

        // Step 2: Evaluate each gate in the circuit
        for (Gate gate : nodes.values()) {
            gate.evaluateOutput(gate.getInputs(), gate.getOutput());
        }
    }

    /**
     * Retrieves the values of the primary outputs after circuit evaluation.
     *
     * @return A list of boolean values representing the primary outputs.
     */
    public List<Boolean> getPrimaryOutputsValues() {
        List<Boolean> outputValues = new ArrayList<>();
        for (CircuitConnection output : PrimaryOutputs) {
            outputValues.add(output.getValue());
        }
        return outputValues;
    }

    public void printPrimaryOutputs() {
        System.out.println("Primary Outputs:");
        for (CircuitConnection output : PrimaryOutputs) {
            System.out.println("Output " + output.getId() + ": " + output.getValue());
        }
    }
}
