# Fault Simulator API

## Introduction

The FAULT SIMULATOR API is a comprehensive, modular application specifically designed to simulate faults in digital circuits. This tool enables users to model, analyze, and test digital circuits with respect to fault tolerance and error diagnosis. The simulator provides support for multiple fault injection methodologies and circuit evaluation processes. It also incorporates a well-structured approach to user interaction through its API.

This project has been developed using the Model-View-Controller architecture to ensure better maintainability and scalability. Additionally, it adheres to the object-oriented principles of Java, allowing for easy extension of its functionality to meet future requirements.

Key features of the Fault Simulator API include:
- Circuit modeling using directed graphs.
- Fault injection and analysis for gates (AND, OR, NOT, NAND, etc.).
- Configurable circuit structures loaded from `.bench` files.
- API endpoints for user interaction and testing.

---
## Usage 

The API is designed to be accessed using tools like **Postman**, which allows you to easily interact with the endpoints. Through Postman, users can send files (such as `.bench` files) to the API endpoints, specify configurations, and retrieve results. This makes it simple to test circuit faults, analyze outputs, and debug the circuit models.

---
## File Structure

Below is a detailed explanation of the project file structure:

### Root Folder
- **`HELP.md`**  
  A general help file providing basic instructions for setting up and using the application.

- **`build/`**  
  Contains compiled `.class` files, generated JAR files, and other build-related outputs.

### Source Code (`src/`)
#### `main/`
- **`java/com/example/faultsimulator/`**  
  - **`fault_simulator_application/`**  
    Contains the entry point of the application:
    - `FaultSimulatorApplication.java`: Bootstraps the Spring Boot application.
  - **`fault_simulator_config/`**  
    Configuration classes for the circuit fault simulator:
    - `CircuitFaultSimulatorConfig.java`: Handles application configuration like bean definitions and settings.
  - **`fault_simulator_controller/`**  
    Contains the controller layer of the MVC architecture:
    - `CircuitFaultSimulatorController.java`: Defines REST endpoints for interacting with the fault simulator.
  - **`fault_simulator_model/`**  
    Implements the core logic and data models for the simulator:
    - `CircuitGraph.java`: Represents a digital circuit using a graph data structure.
    - `CircuitConnection.java`: Models the connections between circuit components.
    - **`gates/`**  
      Contains classes for logic gates (AND, OR, NOT, etc.).
  - **`fault_simulator_service/`**  
    Implements the business logic of the application:
    - `CircuitFaultSimulatorService.java`: Provides services for fault injection and circuit simulation.
  - **`fault_simulator_util/`**  
    (Optional) Utilities and helper classes, if added later.

#### `resources/`
- **`Netlists/`**  
  Contains `.bench` files, which define the circuit netlists used for simulation.
- **`application.properties`**  
  Contains Spring Boot application configurations.

#### `test/`
- **`java/com/example/faultsimulator/`**  
  - **`fault_simulator_application/`**  
    Contains unit tests for the application entry point.
  - **`fault_simulator_service/`**  
    Contains unit tests for the service layer.


---
## Code
#### **CircuitFaultSimulatorController.java**
Controller class for handling HTTP requests.


##### Constructor: `CircuitFaultSimulatorController`

This constructor initializes the controller with the `CircuitFaultSimulatorService` that is used to run the circuit simulations.

```java
@Autowired
public CircuitFaultSimulatorController(CircuitFaultSimulatorService circuitService) {
    this.circuitService = circuitService;
}
````

**Explanation**:

- The `@Autowired` annotation automatically injects the `CircuitFaultSimulatorService` into the constructor. This ensures that the controller has access to the service needed for running the simulations.

##### Method: `simulateCircuit`

This method handles the file upload and calls the `CircuitFaultSimulatorService` to process the uploaded circuit file.

```java
public ResponseEntity<String> simulateCircuit(@RequestParam("file") MultipartFile file)
```

**Explanation**:

- This method is the main entry point for the `/api/circuits` endpoint. It receives a `.bench` file via a `POST` request and runs a simulation on the circuit described in the file.
- **Input**: A `MultipartFile` (representing a circuit model in `.bench` format).
- **Output**: A `ResponseEntity` containing a message indicating the success or failure of the simulation.

##### Handling Empty Files

If the uploaded file is empty, the method returns a `400 Bad Request` status with an appropriate error message.

```java
if (file.isEmpty()) {
    return ResponseEntity.badRequest().body("File is empty. Please upload a valid file.");
}
```

**Explanation**:

- This check ensures that the uploaded file is not empty. If it is, a `400 Bad Request` response is returned with a message prompting the user to upload a valid file.

##### Running the Simulation

Once the file is validated, the simulation is executed using the `CircuitFaultSimulatorService`.

```java
String simulationResults = circuitService.runSimulation(file);
```

**Explanation**:

- This line calls the `runSimulation()` method of the `CircuitFaultSimulatorService`, passing the uploaded file as a parameter. The service handles the logic for simulating faults and generating results.

##### Successful Simulation Response

If the simulation completes successfully, the controller responds with a `200 OK` status and the simulation results.

```java
return ResponseEntity.ok("Simulation completed successfully. Results:\n" + simulationResults);
```

**Explanation**:

- Upon successful completion of the simulation, the controller returns a `200 OK` response with the results of the simulation.

##### Error Handling

If an error occurs during the simulation (for example, an unexpected failure), the method returns a `500 Internal Server Error` response.

```java
return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("An unexpected error occurred while running the simulation.");
```

**Explanation**:

- This catch block handles any unexpected exceptions that may occur during the simulation process. If an error is encountered, the controller returns a `500 Internal Server Error` status with a generic error message.

---
#### FaultSimulatorApplication.java

The `FaultSimulatorApplication` class is the entry point for the Fault Simulator API. It starts the Spring Boot application and sets up the application context. The API allows users to upload circuit files and simulate faults in the provided circuits.

##### File Structure
- **FaultSimulatorApplication.java**: Main class that starts the Spring Boot application.



##### Class: `FaultSimulatorApplication`

This class contains the `main` method, which is responsible for running the Spring Boot application.

```java
@SpringBootApplication
public class FaultSimulatorApplication {
````

**Explanation**:

- The `@SpringBootApplication` annotation marks this class as the main entry point for the Spring Boot application. This annotation enables automatic configuration, component scanning, and other essential Spring Boot features.

##### Method: `main`

The `main` method serves as the entry point for running the Spring Boot application.

```java
public static void main(String[] args) {
    SpringApplication.run(FaultSimulatorApplication.class, args);
}
```

**Explanation**:

- The `main` method invokes the `SpringApplication.run()` method, which bootstraps the application. It sets up the application context and runs the Spring Boot application, making the API ready to handle incoming requests.

---

### Fault Simulator Model
#### Gates

##### Gate.java

The `Gate` class is an abstract representation of a circuit gate. It defines the common properties and methods for gates in the fault simulator application. The class includes an identifier for the gate, input connections, and an output connection. It also provides an abstract method for evaluating the gate's output, which will be implemented by its subclasses.

###### File Structure
- **Gate.java**: Abstract class that represents a generic gate in the circuit.

###### Class: `Gate`

This class defines the structure for a gate object, with its inputs, output, and methods to interact with the connections.

```java
public abstract class Gate {
````

**Explanation**:

- The `Gate` class is abstract, meaning it cannot be instantiated directly. It serves as a blueprint for other specific gate types, such as AND, OR, NOT, etc.

###### Fields

```java
protected int id;
```

**Explanation**:

- The `id` field holds the unique identifier for the gate. This is used to distinguish the gate from others in the circuit.

```java
protected List<CircuitConnection> inputs;
```

**Explanation**:

- The `inputs` field is a list of `CircuitConnection` objects. It represents the inputs to the gate, which are used to evaluate the gate's output.

```java
protected CircuitConnection output;
```

**Explanation**:

- The `output` field holds the `CircuitConnection` representing the gate's output. This connection will be affected by the gate's evaluation logic.

###### Constructor

```java
public Gate(int id, List<CircuitConnection> inputs, CircuitConnection output) {
    this.id = id;
    this.inputs = inputs;
    this.output = output;
}
```

**Explanation**:

- The constructor initializes the gate's `id`, `inputs`, and `output`. It is called when a new gate object is created to set up the gate's connections.

###### Methods

```java
public int getId() {
    return id;
}
```

**Explanation**:

- This method returns the `id` of the gate.


```java
public List<CircuitConnection> getInputs() {
    return inputs;
}
```

**Explanation**:

- This method returns the list of `CircuitConnection` objects representing the inputs to the gate.


```java
public CircuitConnection getOutput() {
    return output;
}
```

**Explanation**:

- This method returns the `CircuitConnection` object representing the output of the gate.


```java
public abstract void evaluateOutput(List<CircuitConnection> inputs, CircuitConnection output);
```

**Explanation**:

- The `evaluateOutput` method is an abstract method that must be implemented by subclasses of the `Gate` class. It defines the logic for evaluating the gate's output based on its inputs. Subclasses will provide specific implementations for different types of gates (e.g., AND, OR, NOT).

---
##### ANDGate.java-NANDGate.java-ORGate.java-NORGate.java-BUFFGate.java XORGate.java-NOTGate.java

The `XGate` classes are a subclass of the `Gate` class that implement the logic for an the respective gate. It evaluates the output based on the values of its inputs.

The `XGate` class extends the abstract `Gate` class 

```java
public class XGate extends Gate {
````

**Explanation**:

- The `XGate` class inherits from the `Gate` class and implements the logic for evaluating the output of an AND gate.

###### Constructor

```java
public XGate(int id, List<CircuitConnection> inputs, CircuitConnection output) {
    super(id, inputs, output);
}
```

**Explanation**:

- The constructor of the `ANDGate` class calls the parent `Gate` constructor to initialize the gate's `id`, `inputs`, and `output`. It allows the creation of an AND gate with a unique identifier and specific input and output connections.

###### Methods

```java
public void evaluateOutput(List<CircuitConnection> inputs, CircuitConnection output) {
    Boolean result = $initial value$;
    for (CircuitConnection input : inputs) {
        result = $logic for each gate$;
    }
    output.setValue(result);
}
```

**Explanation**:

- This method evaluates the output of the AND gate. It iterates through all the input connections and checks if all their values are `true`. If any input is `false`, the result will be `false`. If all inputs are `true`, the result will be `true`. The final result is then set to the `output` connection.

---

#### CircuitConnection.java

- **CircuitConnection.java**: A class that models a connection between gates in a circuit, including the ability to handle stuck-at faults.

The `CircuitConnection` class models a connection in a digital circuit, storing its value and handling faults (specifically stuck-at faults).

```java
public class CircuitConnection {
````

**Explanation**:

- This class is used to represent connections between various components (e.g., gates) in the fault simulation process. It can simulate faulty behavior by allowing a connection to be stuck at a specific logic value.

##### Fields

```java
private final int id;
```

**Explanation**:

- The `id` field uniquely identifies the connection. This is used to differentiate between multiple connections in the circuit.

```java
private boolean value;
```

**Explanation**:

- The `value` field holds the logical value of the connection (either `true` or `false`). This is the default value of the connection unless a stuck-at fault is injected.

```java
private boolean stuck;
```

**Explanation**:

- The `stuck` field indicates whether the connection is currently in a stuck-at fault state. If `true`, the connection is forced to a specific value and does not change.


```java
private boolean stuckValue;
```

**Explanation**:

- The `stuckValue` field holds the logical value (`true` or `false`) that the connection is stuck to if a stuck-at fault is applied.
##### Constructor

```java
public CircuitConnection(int id) {
    this.id = id;
    this.value = false; // Default value
    this.stuck = false;
    this.stuckValue = false;
}
```

**Explanation**:

- The constructor initializes the connection with a unique `id`, a default `value` of `false`, and sets the `stuck` and `stuckValue` flags to `false`, meaning there is no stuck-at fault initially.

##### Methods

```java
public int getId() {
    return id;
}
```

**Explanation**:

- This method returns the unique identifier for the connection.

```java
public boolean getValue() {
    return stuck ? stuckValue : value;
}
```

**Explanation**:

- This method returns the current logical value of the connection. If the connection is stuck (i.e., the `stuck` flag is `true`), it returns the `stuckValue`; otherwise, it returns the current value.


```java
public void setValue(boolean value) {
    if (!stuck) {
        this.value = value;
    }
}
```

**Explanation**:

- This method sets the logical value of the connection. If the connection is not stuck (i.e., `stuck` is `false`), it updates the `value`. If the connection is stuck, the value cannot be changed.


```java
public boolean isStuck() {
    return stuck;
}
```

**Explanation**:

- This method checks whether the connection is stuck in a fixed logical value. It returns `true` if the connection is stuck and `false` otherwise.


```java
public void setStuck(boolean stuck, boolean stuckValue) {
    this.stuck = stuck;
    this.stuckValue = stuckValue;
    if (stuck) {
        this.value = stuckValue; // Force the value to the stuck-at fault
    }
    System.out.println("Connection " + id + " stuck=" + stuck + ", value=" + stuckValue);
}
```

**Explanation**:

- This method injects or removes a stuck-at fault on the connection. If `stuck` is `true`, the connection is forced to the `stuckValue` (either `true` or `false`). If `stuck` is `false`, the connection can take normal values again. The method also prints the state of the connection for debugging purposes.

---

#### CircuitGraph.java

- **CircuitGraph.java**: A class that models a digital circuit as a graph with gates, connections, and primary inputs/outputs. The class provides methods to evaluate the circuit and manage the connections and gates.


```java
public class CircuitGraph {
````

**Explanation**:

- This class models a circuit's components and handles its evaluation by setting input values, processing gates, and retrieving output values.

##### Fields

```java
private final Map<Integer, Gate> gates = new HashMap<>();
```

**Explanation**:

- This field stores the gates in the circuit, with each gate identified by a unique `id`. The `Map` ensures that no duplicate gate IDs are added.

```java
private final Map<Integer, CircuitConnection> circuitConnections = new HashMap<>();
```

**Explanation**:

- This field stores the connections in the circuit, again keyed by unique connection IDs. Each connection represents the path between gates and/or inputs/outputs.


```java
private final List<CircuitConnection> primaryInputs = new ArrayList<>();
```

**Explanation**:

- This list holds the primary input connections for the circuit. These are the starting points for the circuit's evaluation.

```java
private final List<CircuitConnection> primaryOutputs = new ArrayList<>();
```

**Explanation**:

- This list holds the primary output connections for the circuit. These are the ending points for the circuit's evaluation.

##### Methods


```java
public void addPrimaryInput(CircuitConnection input) {
    if (!primaryInputs.contains(input)) {
        primaryInputs.add(input);
    } else {
        System.err.println("Duplicate primary input detected: " + input.getId());
    }
}
```

**Explanation**:

- This method adds a primary input connection to the circuit, ensuring that no duplicates are added.


```java
public void addPrimaryOutput(CircuitConnection output) {
    if (!primaryOutputs.contains(output)) {
        primaryOutputs.add(output);
    } else {
        System.err.println("Duplicate primary output detected: " + output.getId());
    }
}
```

**Explanation**:

- This method adds a primary output connection to the circuit, ensuring that no duplicates are added.


```java
public void addGate(Gate gate) {
    if (gates.containsKey(gate.getId())) {
        System.err.println("Duplicate Gate ID Detected: " + gate.getId());
        return;
    }
    gates.put(gate.getId(), gate);
}
```

**Explanation**:

- This method adds a gate to the circuit. It checks if a gate with the same ID already exists to prevent duplicates.


```java
public void addCircuitConnection(CircuitConnection connection) {
    if (!circuitConnections.containsKey(connection.getId())) {
        circuitConnections.put(connection.getId(), connection);
    }
}
```

**Explanation**:

- This method adds a connection to the circuit, ensuring no duplicate connections are added.


```java
public List<CircuitConnection> getPrimaryInputs() {
    return primaryInputs;
}
```

**Explanation**:

- This method returns the list of primary input connections for the circuit.


```java
public List<CircuitConnection> getPrimaryOutputs() {
    return primaryOutputs;
}
```

**Explanation**:

- This method returns the list of primary output connections for the circuit.


```java
public Map<Integer, Gate> getGates() {
    return gates;
}
```

**Explanation**:

- This method returns the map of gates in the circuit, keyed by their unique IDs.


```java
public Map<Integer, CircuitConnection> getCircuitConnections() {
    return circuitConnections;
}
```

**Explanation**:

- This method returns the map of connections in the circuit, keyed by their unique IDs.

```java
public void evaluate(List<Boolean> primaryInputValues) throws Exception {
    if (primaryInputValues.size() != primaryInputs.size()) {
        throw new IllegalArgumentException("Input values count does not match primary inputs.");
    }

    for (int i = 0; i < primaryInputs.size(); i++) {
        primaryInputs.get(i).setValue(primaryInputValues.get(i));
        System.out.println("Input " + primaryInputs.get(i).getId() + " = " + primaryInputs.get(i).getValue());
    }

    for (Gate gate : gates.values()) {
        gate.evaluateOutput(gate.getInputs(), gate.getOutput());
    }
}
```

**Explanation**:

- This method evaluates the circuit by setting the primary input values and then processing the gates. Each gate is evaluated in sequence, updating the output based on the inputs.


```java
public List<Boolean> getPrimaryOutputsValues() {
    List<Boolean> outputValues = new ArrayList<>();
    for (CircuitConnection output : primaryOutputs) {
        outputValues.add(output.getValue());
    }
    return outputValues;
}
```

**Explanation**:

- This method retrieves the logical values of the primary outputs after the circuit has been evaluated.


```java
public void printSummary() {
    System.out.println("Primary Inputs: " + primaryInputs.size());
    System.out.println("Primary Outputs: " + primaryOutputs.size());
    System.out.println("Total Gates: " + gates.size());
}
```

**Explanation**:

- This method prints a summary of the circuit, including the number of primary inputs, primary outputs, and gates in the circuit.

---
### Circuit Fault Simulator Service Documentation

This service simulates faults in a digital circuit by evaluating fault-free and faulty circuits, generating fault lists, and simulating the fault effects on the outputs. It works by parsing a circuit description file and then running fault simulations on the parsed circuit.

#### Functions & Definitions

This function is the main entry point for running the fault simulation. It will parse the uploaded file, then run the serial and parallel fault simulations, and return the results. Currently, this function is not implemented.

```java
public String runSimulation(MultipartFile file) {
    return "";
}
````

Generates a list of stuck-at faults for every connection in the circuit. It adds two faults for each connection: stuck-at-0 and stuck-at-1.

```java
public List<Fault> generateFaultList() {
    List<Fault> faultList = new ArrayList<>();

    for (CircuitConnection connection : circuitGraph.getCircuitConnections().values()) {
        faultList.add(new Fault(connection.getId(), false)); // Stuck-at-0
        faultList.add(new Fault(connection.getId(), true));  // Stuck-at-1
    }

    return faultList;
}
```

This method evaluates the circuit without any faults by resetting all faults and then running the circuit evaluation with the provided test vector. It returns the output values of the circuit.

```java
public List<Boolean> evaluateFaultFreeCircuit(List<Boolean> inputValues) throws Exception {
    // Reset all stuck faults
    for (CircuitConnection connection : circuitGraph.getCircuitConnections().values()) {
        connection.setStuck(false, false);
    }

    circuitGraph.evaluate(inputValues);
    List<Boolean> test = circuitGraph.getPrimaryOutputsValues();
    return test;
}
```

This method runs the fault simulation by first evaluating the fault-free circuit and then injecting faults (stuck-at-0 or stuck-at-1) into each connection. It compares the faulty outputs to the fault-free outputs and stores the results.

```java
public Map<String, List<Boolean>> runFaultSimulation(List<Boolean> testVector) throws Exception {
    Map<String, List<Boolean>> faultResults = new HashMap<>();

    // Step 1: Evaluate fault-free circuit
    List<Boolean> goldenOutputs = evaluateFaultFreeCircuit(testVector);

    // Step 2: Inject faults and evaluate for each connection
    for (CircuitConnection connection : circuitGraph.getCircuitConnections().values()) {
        for (boolean stuckAtValue : Arrays.asList(true, false)) {
            // Inject fault
            connection.setStuck(true, stuckAtValue);
            System.out.println("Injecting fault at Node: " + connection.getId() + ", Fault Value: " + stuckAtValue);

            // Evaluate circuit with fault
            circuitGraph.evaluate(testVector);
            List<Boolean> faultyOutputs = circuitGraph.getPrimaryOutputsValues();

            // Store results
            String faultKey = "Node " + connection.getId() + "_StuckAt" + (stuckAtValue ? "1" : "0");
            faultResults.put(faultKey, new ArrayList<>(faultyOutputs));

            // Clear fault
            connection.setStuck(false, false);
        }
    }

    // Return fault results
    return faultResults;
}
```

This function reads an uploaded file line by line, processes each line by calling `parseLine()` to identify inputs, outputs, or gates, and adds the corresponding objects to the circuit graph. It then validates the circuit structure.

```java
public void parseFile(MultipartFile file) throws IOException {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
        String line;
        while ((line = reader.readLine()) != null) {
            parseLine(line.trim());
        }
    }
    validateParsedCircuit(); // Additional circuit validation
    System.out.println("Parsing completed successfully.");
}
```

This method processes each line of the circuit file. It checks the type of the line (either an input, output, or gate description) and calls the appropriate processing method.

```java
private void parseLine(String line) {
    line = line.split("#")[0].trim(); // Remove comments
    if (line.isEmpty()) return;

    if (line.startsWith("INPUT")) {
        int inputId = extractId(line);
        CircuitConnection temp = new CircuitConnection(inputId);
        circuitGraph.addPrimaryInput(temp);
        circuitGraph.addCircuitConnection(temp);
    } else if (line.startsWith("OUTPUT")) {
        int outputId = extractId(line);
        CircuitConnection temp = new CircuitConnection(outputId);
        circuitGraph.addPrimaryOutput(temp);
        circuitGraph.addCircuitConnection(temp);
    } else if (line.contains("=")) {
        parseGateLine(line);
    } else {
        throw new IllegalArgumentException("Invalid line format: " + line);
    }
}
```

This function processes lines that describe gates, extracting output IDs, gate types, and input IDs, and creates the corresponding gate object. The gate is then added to the circuit graph.

```java
private void parseGateLine(String line) {
    try {
        String[] parts = line.split("=");
        int outputId = Integer.parseInt(parts[0].trim());

        String gateInfo = parts[1].trim();
        String gateType = gateInfo.substring(0, gateInfo.indexOf('(')).trim();
        String[] inputIds = gateInfo.substring(gateInfo.indexOf('(') + 1, gateInfo.indexOf(')'))
                .split("\\s*,\\s*");

        List<CircuitConnection> inputs = new ArrayList<>();

        for (String id : inputIds) {
            if (circuitGraph.getCircuitConnections().containsKey(Integer.parseInt(id.trim()))) {
                CircuitConnection temp2 = circuitGraph.getCircuitConnections().get(Integer.parseInt(id.trim()));
                inputs.add(temp2);
            } else {
                CircuitConnection temp = new CircuitConnection(Integer.parseInt(id.trim()));
                circuitGraph.addCircuitConnection(temp);
                inputs.add(temp);
            }
        }

        CircuitConnection output;

        if (circuitGraph.getCircuitConnections().containsKey(outputId)) {
            CircuitConnection temp2 = circuitGraph.getCircuitConnections().get(outputId);
            output = temp2;
        } else {
            output = new CircuitConnection(outputId);
            circuitGraph.addCircuitConnection(output);
        }
        circuitGraph.addGate(createGate(outputId, gateType, inputs, output));
    } catch (Exception e) {
        System.err.println("Error parsing gate line: " + line);
        e.printStackTrace();
    }
}
```

This method creates a gate of the specified type (e.g., AND, OR, NOT) using the given input and output connections.

```java
private Gate createGate(int id, String type, List<CircuitConnection> inputs, CircuitConnection output) {
    return switch (type.toUpperCase()) {
        case "AND" -> new ANDGate(id, inputs, output);
        case "NAND" -> new NANDGate(id, inputs, output);
        case "OR" -> new ORGate(id, inputs, output);
        case "NOR" -> new NORGate(id, inputs, output);
        case "XOR" -> new XORGate(id, inputs, output);
        case "NOT" -> new NOTGate(id, inputs, output);
        case "BUFF" -> new BUFFGate(id, inputs, output);
        default -> throw new IllegalArgumentException("Unknown gate type: " + type);
    };
}
```

This function ensures that the parsed circuit has primary inputs, primary outputs, and gates. If any of these are missing, it throws an exception.

```java
private void validateParsedCircuit() {
    if (circuitGraph.getPrimaryInputs().isEmpty()) {
        throw new IllegalStateException("No primary inputs found in the circuit.");
    }
    if (circuitGraph.getPrimaryOutputs().isEmpty()) {
        throw new IllegalStateException("No primary outputs found in the circuit.");
    }
    if (circuitGraph.getGates().isEmpty()) {
        throw new IllegalStateException("No gates found in the circuit.");
    }
}
```

This method returns the current `CircuitGraph` object, which represents the entire circuit, including all connections, gates, inputs, and outputs.

```java
public CircuitGraph getCircuitGraph() {
    return circuitGraph;
}
```
#### Inner Class: `Fault`

This inner class represents a fault in the circuit. Each fault is associated with a node ID (the connection where the fault occurs) and a stuck value (either `0` or `1`).

```java
public static class Fault {
    private final int nodeId;
    private final boolean stuckValue;

    public Fault(int nodeId, boolean stuckValue) {
        this.nodeId = nodeId;
        this.stuckValue = stuckValue;
    }

    public int getNodeId() {
        return nodeId;
    }

    public boolean getStuckValue() {
	    return stuckValue; 
     }
    }
```


---

### Application Properties

This configuration file is used to set up the essential properties for running the Fault Simulator application. It defines the application name, server port, and configuration for the H2 database that was initially planned to be used. However, the H2 database was not implemented later due to time constraints.

### Configuration Details

```properties
# Set the name of the Spring Boot application
spring.application.name=fault-simulator

# Configure the server port to 8095
server.port=8095
````

- **spring.application.name**: This property sets the name of the application to "fault-simulator", which can be used in logs, monitoring, and other contexts.
- **server.port**: Configures the server to run on port `8095`.

#### H2 Database Configuration

```properties
# H2 Database Configuration
spring.datasource.url=jdbc:h2:~/test
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=sa
spring.sql.init.platform=h2
spring.sql.init.mode=always
# Enable H2 console
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

- **spring.datasource.url**: Specifies the URL for connecting to the H2 database. In this case, it uses an in-memory database stored at `~/test`.
- **spring.datasource.driverClassName**: The class name of the H2 JDBC driver used to connect to the database.
- **spring.datasource.username** and **spring.datasource.password**: These properties set the username and password for connecting to the H2 database. Here, both are set to `sa`.
- **spring.sql.init.platform**: Specifies the platform (H2 in this case) used for initializing the database.
- **spring.sql.init.mode**: Ensures that the SQL scripts will be executed automatically at startup.
- **spring.h2.console.enabled**: Enables the H2 console for web-based access to the database.
- **spring.h2.console.path**: Defines the path to access the H2 console (`/h2-console`).

#### Note on H2 Database

Initially, the H2 database was intended to be used for local testing and development. However, due to time constraints, the H2 database configuration was not fully implemented and was later removed from the project. Therefore, the H2 database was not utilized in the final version of the Fault Simulator application.

---
## Testing
### CircuitFaultSimulatorServiceTest.java

This class contains unit tests for the **`CircuitFaultSimulatorService`** in the Fault Simulator application. The tests validate the core functionalities of the fault simulator, including circuit parsing, fault-free evaluation, fault simulation, and fault list generation. Each test method ensures that the service behaves as expected under various conditions.

### Test Methods and Explanations

This helper method initializes the `CircuitFaultSimulatorService` and parses a benchmark file for testing.

```java
private CircuitFaultSimulatorService initializeService(String fileName) throws Exception {
    CircuitFaultSimulatorService service = new CircuitFaultSimulatorService();
    InputStream inputStream = getClass().getClassLoader().getResourceAsStream("Netlists/" + fileName);
    assertNotNull(inputStream, fileName + " not found in Netlists directory!");

    MockMultipartFile mockFile = new MockMultipartFile(
            "file", fileName, "text/plain", inputStream.readAllBytes()
    );

    service.parseFile(mockFile);
    return service;
}
````

- **Explanation**: This method loads the benchmark file from the resources folder, mocks the file upload, and parses the content using the `parseFile` method of the service.

This method sets up the initial conditions for the tests by parsing a sample benchmark content into the service.

```java
@BeforeEach
void setUp() throws Exception {
    service = new CircuitFaultSimulatorService();
    // Assuming a valid benchmark file is parsed for testing
    String testBenchContent = """
            INPUT(1)
            INPUT(2)
            INPUT(3)
            OUTPUT(4)
            OUTPUT(5)
            4 = AND(1,2)
            5 = OR(2,3)
            """;
    parseTestFile(testBenchContent);
}
```

- **Explanation**: This method prepares the environment by parsing a simple benchmark file for testing, ensuring that the service is ready for further test executions.


This method parses a string content into the service for testing purposes.

```java
private void parseTestFile(String content) throws Exception {
    service.parseFile(new MockMultipartFile("test.bench", content.getBytes()));
}
```

- **Explanation**: Parses the provided string content into a `MockMultipartFile` and processes it using the `parseFile` method. This is typically used for quick, in-memory test setups.


This test method evaluates a simple circuit consisting of two inputs and an AND gate, verifying the output.

```java
@Test
void evaluateCircuit() throws Exception {
    CircuitFaultSimulatorService service = new CircuitFaultSimulatorService();
    CircuitGraph circuitGraph = service.getCircuitGraph();

    // Define inputs and outputs
    CircuitConnection input1 = new CircuitConnection(1);
    CircuitConnection input2 = new CircuitConnection(2);
    CircuitConnection output = new CircuitConnection(3);

    circuitGraph.addPrimaryInput(input1);
    circuitGraph.addPrimaryInput(input2);
    circuitGraph.addPrimaryOutput(output);
    circuitGraph.addGate(new ANDGate(1, Arrays.asList(input1, input2), output));

    // Set input values and evaluate
    List<Boolean> inputValues = Arrays.asList(true, false);
    service.evaluateFaultFreeCircuit(inputValues);

    // Verify outputs
    List<Boolean> outputs = circuitGraph.getPrimaryOutputsValues();
    assertNotNull(outputs, "Outputs should not be null.");
    assertEquals(1, outputs.size(), "Expected 1 output.");
    assertFalse(outputs.get(0), "AND gate output should be false.");
}
```

- **Explanation**: This test defines inputs, outputs, and an AND gate. It then evaluates the circuit with specific input values and verifies the output.


This method tests parsing a benchmark file for the C432 circuit and checks the number of primary inputs, outputs, and gates.

```java
@Test
void parseC432File() throws Exception {
    CircuitFaultSimulatorService service = initializeService("c432.bench.txt");

    CircuitGraph circuitGraph = service.getCircuitGraph();
    System.out.println("Primary Inputs: " + circuitGraph.getPrimaryInputs().size());
    System.out.println("Primary Outputs: " + circuitGraph.getPrimaryOutputs().size());
    System.out.println("Total Gates: " + circuitGraph.getGates().size());

    assertEquals(36, circuitGraph.getPrimaryInputs().size(), "Expected 36 primary inputs.");
    assertEquals(7, circuitGraph.getPrimaryOutputs().size(), "Expected 7 primary outputs.");
    assertTrue(circuitGraph.getGates().size() >= 120, "Expected at least 120 gates.");
}
```

- **Explanation**: This method parses the **C432** benchmark file, then checks if the number of primary inputs, outputs, and gates matches the expected values.


This test evaluates the **C432** circuit with a sample input vector and verifies the output.

```java
@Test
void evaluateC432Circuit() throws Exception {
    CircuitFaultSimulatorService service = initializeService("c432.bench.txt");

    // Define a sample input vector for c432
    List<Boolean> inputValues = Arrays.asList(
            true, false, true, false, true, false, true, false,
            true, false, true, false, true, false, true, false,
            true, false, true, false, true, false, true, false,
            true, false, true, false, true, false, true, false,
            true, false, true, false
    );

    // Evaluate circuit
    service.evaluateFaultFreeCircuit(inputValues);

    // Verify outputs
    List<Boolean> outputs = service.getCircuitGraph().getPrimaryOutputsValues();
    assertNotNull(outputs, "Outputs should not be null.");
    assertEquals(7, outputs.size(), "Expected 7 outputs.");

    System.out.println("C432 Circuit Outputs: " + outputs);
}
```

- **Explanation**: This method runs the fault-free evaluation of the **C432** circuit with a predefined input vector and verifies the expected number of outputs.


This method tests the fault-free evaluation for a simple circuit with a defined input vector and expected output.

```java
@Test
void testEvaluateFaultFreeCircuit() throws Exception {
    // Define test vector: Inputs for the circuit
    List<Boolean> testVector = Arrays.asList(true, false, true);

    // Run fault-free evaluation
    List<Boolean> outputs = service.evaluateFaultFreeCircuit(testVector);

    // Expected outputs for the given test vector
    assertEquals(Arrays.asList(false, true), outputs, "Fault-free evaluation failed");
}
```

- **Explanation**: This test method ensures that the fault-free evaluation produces the correct output for a given test vector.


This method tests running a fault simulation with a test vector and compares the outputs with the fault-free (golden) outputs.

```java
@Test
void testRunFaultSimulation() throws Exception {
    // Define a test vector
    List<Boolean> testVector = Arrays.asList(true, false, true);

    // Run fault simulation
    Map<String, List<Boolean>> faultResults = service.runFaultSimulation(testVector);

    // Golden outputs (fault-free)
    List<Boolean> goldenOutputs = service.evaluateFaultFreeCircuit(testVector);
    System.out.println("Golden Outputs: " + goldenOutputs);

    // Verify that fault results differ from the golden outputs for injected faults
    faultResults.forEach((faultKey, faultyOutputs) -> {
        System.out.println("Fault: " + faultKey + ", Outputs: " + faultyOutputs);
        assertNotEquals(goldenOutputs, faultyOutputs, "Fault at " + faultKey + " did not affect outputs!");
    });
}
```

- **Explanation**: This method tests the fault simulation and compares each result with the golden (fault-free) output to verify if the injected faults affect the circuit as expected.


This method tests the generation of the fault list, ensuring that stuck-at-0 and stuck-at-1 faults are included.

```java
@Test
void testGenerateFaultList() {
    List<CircuitFaultSimulatorService.Fault> faultList = service.generateFaultList();

    // Verify that the fault list contains expected stuck-at-0 and stuck-at-1 faults
    assertFalse(faultList.isEmpty(), "Fault list is empty!");
    faultList.forEach(fault -> 
            System.out.println("Fault: Node " + fault.getNodeId() + ", Stuck Value: " + fault.getStuckValue()));
}
```

- **Explanation**: This test verifies that the fault list is populated with faults, including stuck-at-0 and stuck-at-1 faults, ensuring that fault generation works correctly.


This method tests parsing another benchmark file and checks the number of primary inputs, outputs, and gates.

```java
@Test
void parseFile() throws Exception {
    CircuitFaultSimulatorService service = initializeService("c17.bench.txt");

    CircuitGraph circuitGraph = service.getCircuitGraph();
    assertNotNull(circuitGraph, "CircuitGraph should not be null.");
    assertEquals(5, circuitGraph.getPrimaryInputs().size(), "Expected 5 primary inputs.");
    assertEquals(2, circuitGraph.getPrimaryOutputs().size(), "Expected 2 primary outputs.");
    assertEquals(6, circuitGraph.getGates().size(), "Expected 6 gates.");
}
```

- **Explanation

**This method tests the parsing of a **c17** benchmark file, checking the circuit's basic characteristics like the number of inputs, outputs, and gates.

---

### CircuitFaultSimulatorApplicationTest.java

This is a basic test class for a Spring Boot application, intended to verify that the Spring application context loads successfully. Below is an explanation of the `FaultSimulatorApplicationTests` class:

```java
package com.example.faultsimulator.fault_simulator_application;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FaultSimulatorApplicationTests {

    @Test
    void contextLoads() {
        // Verify that the Spring context loads without issues
        assertTrue(true, "Spring context loaded successfully.");
    }
}
```


---