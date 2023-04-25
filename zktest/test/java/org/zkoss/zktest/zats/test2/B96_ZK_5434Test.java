package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

public class B96_ZK_5434Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();

		final String tzIdentifier = "America/Mexico_City";
		final int javaUtcOffsetInSeconds = OffsetDateTime.now(ZoneId.of(tzIdentifier)).getOffset().getTotalSeconds();
		System.out.println("javaUtcOffsetInSeconds:");
		System.out.println(javaUtcOffsetInSeconds);
		assertEquals(-6 * 3600, javaUtcOffsetInSeconds); // The UTC offset should be -6 as of Apr 24, 2023.

		final String offsetString = getEval("getOffset('" + tzIdentifier + "')");
		System.out.println("offsetString:");
		System.out.println(offsetString);
		assertEquals("-6", offsetString);

		final float momentjsUtcOffsetInHours = Float.parseFloat(offsetString);
		System.out.println("momentjsUtcOffsetInHours:");
		System.out.println(momentjsUtcOffsetInHours);
		// Don't use `assertEquals` to compare floating-point numbers.
		assertTrue(javaUtcOffsetInSeconds == momentjsUtcOffsetInHours * 3600);
	}
}
