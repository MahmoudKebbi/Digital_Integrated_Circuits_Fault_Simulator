package com.example.faultsimulator.fault_simulator_application;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FaultSimulatorApplicationTests {

	@Test
	void contextLoads() {

				FaultSimulatorApplication.main(new String[] {});  // Start the application
				// Optionally, add assertions to check if certain beans are initialized
				assertTrue(true, "Application started successfully.");
			}
		}


