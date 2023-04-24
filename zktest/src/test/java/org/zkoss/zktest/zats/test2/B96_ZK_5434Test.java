package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.OffsetDateTime;
import java.time.ZoneId;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B96_ZK_5434Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();

		final String tzIdentifier = "America/Mexico_City";
		final float momentjsUtcOffsetInHours = Float.parseFloat(getEval("getOffset('" + tzIdentifier + "')"));
		final int javaUtcOffsetInSeconds = OffsetDateTime.now(ZoneId.of(tzIdentifier)).getOffset().getTotalSeconds();
		// Don't use `assertEquals` to compare floating-point numbers.
		assertTrue(javaUtcOffsetInSeconds == momentjsUtcOffsetInHours * 3600);
		assertEquals(javaUtcOffsetInSeconds, -6 * 3600); // The UTC offset should be -6 as of Apr 24, 2023.
	}
}
