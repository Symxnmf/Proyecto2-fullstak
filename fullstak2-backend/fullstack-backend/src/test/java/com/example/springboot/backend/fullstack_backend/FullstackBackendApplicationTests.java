package com.example.springboot.backend.fullstack_backend;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Skip full context during unit tests to avoid external DB dependency")
class FullstackBackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
