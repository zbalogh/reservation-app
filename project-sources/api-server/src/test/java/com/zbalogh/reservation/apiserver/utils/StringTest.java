package com.zbalogh.reservation.apiserver.utils;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * JUNIT5 test demo with string testing
 */
class StringTest {
	
	@BeforeAll //@BeforeClass
	static void beforeAll() {
		System.out.println("beforeAll(): Initialize connection to database");
	}
	
	@AfterAll //@AfterClass
	static void afterAll() {
		System.out.println("afterAll(): Close connection to database");
	}

	@BeforeEach //@Before
	void beforeEach(TestInfo info) {
		System.out.println("beforeEach(): Initialize Test Data for " + info.getDisplayName());	
	}
	
	@AfterEach //@After
	void afterEach(TestInfo info) {
		System.out.println("afterEach(): Clean up Test Data for " + info.getDisplayName());	
	}
	
	
	@Test
	void length_basic() {
		int actualLength = "ABCD".length();
		int expectedLength = 4;
		
		assertEquals(expectedLength, actualLength);
	}
	
	@Test
	void length_greater_than_zero() {
		assertTrue("ABCD".length()>0);
		assertTrue("ABC".length()>0);
		assertTrue("A".length()>0);
		assertTrue("DEF".length()>0);
	}
	
	
	@ParameterizedTest(name = "length greater than zero: [{index}] -> {arguments}")
	@ValueSource(strings= {"ABCD", "ABC", "A", "DEF"})
	@DisplayName("length greater than zero")
	void length_greater_than_zero_using_parameterized_test(String str) {
		assertTrue(str.length()>0);
	}
	
	@ParameterizedTest(name = "uppercase test: {0} toUpperCase is {1} | [{index}] -> {arguments}")
	@CsvSource(value= {"abcd, ABCD", "abc, ABC", "'', ''", "abcdefg, ABCDEFG"})
	@DisplayName("uppercase test")
	void uppercase_test(String word, String capitalizedWord) {
		assertEquals(capitalizedWord, word.toUpperCase());
	}
	
	@ParameterizedTest(name = "length test: {0} length is {1} | [{index}] -> {arguments}")
	@CsvSource(value= {"abcd, 4", "abc, 3", "'', 0", "abcdefg, 7"})
	@DisplayName("length test")
	void length_test(String word, int expectedLength) {
		assertEquals(expectedLength, word.length());
	}
	
	
	@Test
	@DisplayName("When length is null, throw an exception")
	void length_exception() {
		String str = null;
		// it is success if NullPointerException occurs
		assertThrows(NullPointerException.class, // expect str string variable to be null
				() -> {
					str.length();
				}
		);
	}
	
	@Test
	void toUpperCase_basic() {
		String str = "abcd";
		String result = str.toUpperCase();
		assertEquals("ABCD", result);
	}
	
	@Test
	@RepeatedTest(name=RepeatedTest.LONG_DISPLAY_NAME, value=10)
	@DisplayName("contains basic")
	void contains_basic() {
		String str = "abcdefgh";
		boolean result = str.contains("ijk");
		assertEquals(false, result);
		// assertTrue(result); - optional
	}
	
	@Test 
	void split_basic() {
		String str = "abc def ghi";
		String actualResult[] = str.split(" ");
		String[] expectedResult = new String[] {"abc", "def", "ghi"};
		
		assertArrayEquals(expectedResult, actualResult);
	}
	
	@Test
	public void givenUsingJava8_whenGeneratingRandomAlphabeticString_thenCorrect()
	{
	    int leftLimit = 97; // letter 'a'
	    int rightLimit = 122; // letter 'z'
	    int targetStringLength = 10;
	    Random random = new Random();

	    String generatedString = random.ints(leftLimit, rightLimit + 1)
	      .limit(targetStringLength)
	      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
	      .toString();

	    System.out.println(generatedString);
	}
	
	@Test
	public void givenUsingJava8_whenGeneratingRandomAlphanumericString_thenCorrect()
	{
	    int leftLimit = 48; // numeral '0'
	    int rightLimit = 122; // letter 'z'
	    int targetStringLength = 10;
	    Random random = new Random();

	    String generatedString = random.ints(leftLimit, rightLimit + 1)
	      .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
	      .limit(targetStringLength)
	      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
	      .toString();

	    System.out.println(generatedString);
	}
	
	@Test
	public void RandomStringUtils_Test()
	{
		String generatedString = RandomStringUtils.randomAlphanumeric(10).toUpperCase();
		
		System.out.println(generatedString);
	}
	
	
	@Test
	@Disabled //@Ignored
	void performanceTest() {
		// it is success if the iteration finishes within the given 10 seconds.
		assertTimeout(Duration.ofSeconds(10),
				() -> {
					for(int i = 0; i <= 1000000; i++) {
						int j = i;
						System.out.println(j);
					}
				}
		);
	}
	
	
	
	@Nested
	@DisplayName("Nested Test for an empty string")
	class EmptyStringTests {
		
		private String str;
		
		@BeforeEach
		void init() {
			str = "";
		}
		
		@Test
		@DisplayName("length is zero")
		void lengthIsZero()
		{
			assertEquals(0, str.length());
		}
		
		@Test
		@DisplayName("upper case is empty")
		void upperCaseIsEmpty()
		{
			assertEquals("", str.toUpperCase());
		}
	}
	
}
