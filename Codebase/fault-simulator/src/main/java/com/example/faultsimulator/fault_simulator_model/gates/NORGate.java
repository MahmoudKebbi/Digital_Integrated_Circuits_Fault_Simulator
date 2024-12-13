package com.example.faultsimulator.fault_simulator_model.gates;

import com.example.faultsimulator.fault_simulator_model.CircuitConnection;

import java.util.List;
import java.util.Map;

public class NORGate extends Gate{

    public NORGate(int id, List<CircuitConnection> inputs, CircuitConnection output) {
        super(id,inputs,output);
    }

    @Override
    public void evaluateOutput(List<CircuitConnection> inputs, CircuitConnection output) {
        Boolean result = false;
        for (CircuitConnection input : inputs) { result=result||input.getValue();}
        output.setValue(!result);
    }
}
