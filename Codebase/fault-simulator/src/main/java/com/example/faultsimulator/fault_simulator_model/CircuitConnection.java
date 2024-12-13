package com.example.faultsimulator.fault_simulator_model;

public class CircuitConnection {
    private int id;
    private boolean value;
    private boolean stuck;


    public boolean isStuck() {
        return stuck;
    }

    public void setStuck(boolean stuck, boolean value) {
        this.stuck = stuck;
        this.value = value;
    }



    public CircuitConnection(int id) {
        this.id = id;
        this.value = false; // Default value
    }

    public int getId() {
        return id;
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }
}