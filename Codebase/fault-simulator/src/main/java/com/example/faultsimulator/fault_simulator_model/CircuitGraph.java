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

    public Map<Integer, Gate> getGates() {
        return nodes;
    }

    public List<CircuitConnection> getPrimaryInputs() {
        return PrimaryInputs;
    }

    public List<CircuitConnection> getPrimaryOutputs() {
        return PrimaryOutputs;
    }

    public void setPrimaryInputsValues(List<Boolean> primaryInputValues) throws Exception {
        if (primaryInputValues.size() != PrimaryInputs.size()) {
            throw new IllegalArgumentException("Mismatch: Number of inputs provided does not match the number of primary inputs in the circuit.");
        }

        for (int i = 0; i < PrimaryInputs.size(); i++) {
            PrimaryInputs.get(i).setValue(primaryInputValues.get(i));
        }
    }

    public void evaluate(List<Boolean> primaryInputValues) throws Exception {
        setPrimaryInputsValues(primaryInputValues);
        for (Gate gate : nodes.values()) {
            gate.evaluateOutput(gate.getInputs(), gate.getOutput());
        }
    }

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
