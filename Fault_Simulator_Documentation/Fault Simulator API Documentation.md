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

