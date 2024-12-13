package com.example.faultsimulator.fault_simulator_model;

import com.example.faultsimulator.fault_simulator_model.gates.*;

import java.util.*;


public class CircuitGraph {
    private Map<Integer, Gate> nodes = new HashMap<>();
    private Map<Integer, CircuitConnection> connections = new HashMap<>();
    private List<CircuitConnection> PrimaryInputs;
    private List<CircuitConnection> PrimaryOutputs;

    public void addConnection(CircuitConnection connection) {
        connections.put(connection.getId(), connection);
    }

    public void addGate(Gate gate) {
        nodes.put(gate.getId(), gate);
    }

    public void evaluate(List<Boolean> primaryInputValues) throws Exception {
        setPrimaryInputsValues(primaryInputValues);
        for (Gate gate : nodes.values()) {
            gate.evaluateOutput(gate.getInputs(), gate.getOutput());;
        }
//        TODO we don't need prints this is just a place holder
        nodes.values().forEach(gate ->
                System.out.println("Gate " + gate.getId() + " Output: " + gate.getOutput().getValue())
        );
    }

    public void addPrimaryInput(CircuitConnection input) {
        PrimaryInputs.add(input);
    }
    public void addPrimaryOutput(CircuitConnection output) {
        PrimaryOutputs.add(output);
    }
    public void setPrimaryInputsValues(List<Boolean> primaryInputs) throws Exception {
        int index = 0;
        for(CircuitConnection input : PrimaryInputs) {
            input.setValue(primaryInputs.get(index));
            index++;
        }
        //TODO will continue tomorrow and think about it a little
    }
}
