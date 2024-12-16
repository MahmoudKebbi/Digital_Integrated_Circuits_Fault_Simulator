package com.example.faultsimulator.fault_simulator_model.gates;

import com.example.faultsimulator.fault_simulator_model.CircuitConnection;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

public class ANDGate extends Gate {

    public ANDGate(int id, List<CircuitConnection> inputs, CircuitConnection output) {
            super(id,inputs,output);
        }

    @Override
    public void evaluateOutput(List<CircuitConnection> inputs, CircuitConnection output) {
        if(!output.isStuck()) {
            Boolean result = true;
            for (CircuitConnection input : inputs) {
                result = result && input.getValue();
            }
            output.setValue(result);
        }
    }

}
