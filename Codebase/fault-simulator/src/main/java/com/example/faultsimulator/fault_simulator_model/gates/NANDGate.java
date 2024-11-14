package com.example.faultsimulator.fault_simulator_model.gates;

import com.example.faultsimulator.fault_simulator_model.CircuitConnection;

import java.util.List;
import java.util.Map;

public class NANDGate extends Gate{

    public NANDGate(int id, List<CircuitConnection> inputs, CircuitConnection output) {
        super(id,inputs,output);
    }

    @Override
    public void evaluateOutput(List<CircuitConnection> inputs, CircuitConnection outputValue) {
//  TODO Write Method
    }
}
