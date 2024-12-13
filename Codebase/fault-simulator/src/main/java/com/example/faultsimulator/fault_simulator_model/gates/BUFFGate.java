package com.example.faultsimulator.fault_simulator_model.gates;



import com.example.faultsimulator.fault_simulator_model.CircuitConnection;

import java.util.List;
import java.util.Map;

public class BUFFGate extends Gate{
    public BUFFGate(int id, List<CircuitConnection> inputs, CircuitConnection output) {
        super(id,inputs,output);
    }

    @Override
    public void evaluateOutput(List<CircuitConnection> inputs, CircuitConnection output) {
        output.setValue(!inputs.get(0).getValue());
    }
}
