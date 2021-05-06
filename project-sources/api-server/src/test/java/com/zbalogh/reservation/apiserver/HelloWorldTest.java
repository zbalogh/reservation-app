package com.zbalogh.reservation.apiserver;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Very simple JUNIT test
 */
public class HelloWorldTest {

	@Test
	public void helloWorldTest()
	{
		String testString = "Hello World";
		
		int actualLength = testString.length();
		
		int expectedLength = 11;
		
		assertEquals(expectedLength, actualLength);
	}

}
