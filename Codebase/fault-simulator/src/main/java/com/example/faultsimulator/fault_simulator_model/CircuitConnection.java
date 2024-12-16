package com.example.faultsimulator.fault_simulator_model;

public class CircuitConnection {
    private final int id;
    private boolean value;  // Holds the current logic value (true/false)
    private boolean stuck;  // Indicates if this connection is stuck-at fault
    private boolean stuckValue; // Holds the stuck-at fault value (true/false)

    public CircuitConnection(int id) {
        this.id = id;
        this.value = false; // Default value
        this.stuck = false;
        this.stuckValue = false;
    }

    /**
     * Gets the ID of this connection.
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the current value of the connection.
     */
    public boolean getValue() {
        // If stuck, return the stuck value
        return stuck ? stuckValue : value;
    }

    /**
     * Sets the value of the connection.
     */
    public void setValue(boolean value) {
        // Only set if the connection is not stuck
        if (!stuck) {
            this.value = value;
        }
    }

    /**
     * Checks if the connection is stuck.
     */
    public boolean isStuck() {
        return stuck;
    }

    /**
     * Injects or removes a stuck-at fault.
     *
     * @param stuck      - True if the connection is to be stuck.
     * @param stuckValue - The stuck value (true/false).
     */
    public void setStuck(boolean stuck, boolean stuckValue) {
        this.stuck = stuck;
        this.stuckValue = stuckValue;
        if (stuck) {
            this.value = stuckValue; // Force the value to the stuck-at fault
        }
        System.out.println("Connection " + id + " stuck=" + stuck + ", value=" + stuckValue);
    }
}
