package com.finance.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class BackendApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void testBCrypt() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		boolean matches = encoder.matches("password", "$2a$10$OmbZsvpencmrTNgDDTycxe5JY4TMWTvlYj4yu.zkZ6mqGwnlUwNWe");
		org.junit.jupiter.api.Assertions.assertTrue(matches, "Password hash should match 'password'");
	}

}
