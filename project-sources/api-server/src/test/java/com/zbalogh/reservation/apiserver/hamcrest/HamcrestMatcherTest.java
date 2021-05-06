package com.zbalogh.reservation.apiserver.hamcrest;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.core.Every.everyItem;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

public class HamcrestMatcherTest {

	@Test
	public void basicHamcrestMatchers()
	{
		// List
		List<Integer> scores = Arrays.asList(99, 100, 101, 105);
		
		assertThat(scores, hasSize(4));
		assertThat(scores, hasItems(100, 101));
		assertThat(scores, everyItem(greaterThan(90)));
		assertThat(scores, everyItem(lessThan(200)));

		// String
		assertThat("", is(emptyString()) );
		assertThat(null, is(emptyOrNullString()) );
		assertThat("ABCDE", containsString("BCD") );
		assertThat("ABCDE", startsWith("AB") );
		assertThat("ABCDE", endsWith("DE") );

		// Array
		Integer[] marks = { 1, 2, 3 };

		assertThat(marks, arrayWithSize(3));
		assertThat(marks, arrayContainingInAnyOrder(2, 3, 1));
	}

}
