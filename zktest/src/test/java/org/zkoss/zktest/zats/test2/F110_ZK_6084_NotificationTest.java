/* F110_ZK_6084_NotificationTest.java

		Purpose:

		Description:

		History:
				Mon Sep 07 16:10:00 CST 2026, Created by peggypeng

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * WCAG 4.1.3: Notification and the validation Errorbox announce through the za11y
 * announcer and carry no live role of their own.
 */
@Tag("WcagTestOnly")
public class F110_ZK_6084_NotificationTest extends WebDriverTestCase {
	private static final String POLITE = "#za11y-announcer [role=log][aria-live=polite]";
	private static final String ASSERTIVE = "#za11y-announcer [role=log][aria-live=assertive]";

	@Test
	public void informationalIsPolite() {
		connect();
		waitResponse();

		click(jq("$info"));
		waitResponse(true);

		assertTrue(jq(POLITE).text().contains("Info: Your changes have been saved."),
				"polite log was: " + jq(POLITE).text());
	}

	@Test
	public void warningInterrupts() {
		connect();
		waitResponse();

		click(jq("$warning"));
		waitResponse(true);

		assertTrue(jq(ASSERTIVE).text().contains("Warning: The system is busy."),
				"assertive log was: " + jq(ASSERTIVE).text());
	}

	@Test
	public void errorInterrupts() {
		connect();
		waitResponse();

		click(jq("$error"));
		waitResponse(true);

		assertTrue(jq(ASSERTIVE).text().contains("Error: Could not reach the server."),
				"assertive log was: " + jq(ASSERTIVE).text());
	}

	@Test
	public void validationErrorAnnounces() {
		connect();
		waitResponse();

		// blur onto the heading: a neutral target, so no other constraint fires
		sendKeys(jq("$email"), "not-an-email");
		click(jq("h1"));
		waitResponse(true);

		assertTrue(jq(ASSERTIVE).text().contains("Please enter an e-mail address"),
				"assertive log was: " + jq(ASSERTIVE).text());
	}
}
