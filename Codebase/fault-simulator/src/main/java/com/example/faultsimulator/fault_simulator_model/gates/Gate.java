package com.example.faultsimulator.fault_simulator_model.gates;

import com.example.faultsimulator.fault_simulator_model.CircuitConnection;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Map;

public abstract class Gate {
    protected int id;
    protected List<CircuitConnection> inputs;
    protected CircuitConnection output;

    public Gate(int id, List<CircuitConnection> inputs, CircuitConnection output) {
        this.id = id;
        this.inputs = inputs;
        this.output = output;
    }

    public int getId() {
        return id;
    }

    public List<CircuitConnection> getInputs() {
        return inputs;
    }

    public CircuitConnection getOutput() {
        return output;
    }


    public abstract void evaluateOutput(List<CircuitConnection> inputs, CircuitConnection outputValue);
}

