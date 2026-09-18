/* F110_ZK_6084_PagingTest.java

		Purpose:

		Description:

		History:
				Thu Sep 17 18:00:00 CST 2026, Created by peggypeng

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * WCAG 4.1.3: a detailed Paging announces its summary through the za11y announcer, since
 * the sr-only span holding that sentence is rebuilt with its text on every rerender.
 */
@Tag("WcagTestOnly")
public class F110_ZK_6084_PagingTest extends WebDriverTestCase {
	private static final String POLITE = "#za11y-announcer [role=log][aria-live=polite]";

	@Test
	public void pageChangeAnnouncesTheSummary() {
		connect();
		waitResponse();

		click(jq("$detailed .z-paging-next"));
		waitResponse();

		assertTrue(jq(POLITE).text().contains("11-20"),
				"polite log was: " + jq(POLITE).text());
	}

	@Test
	public void pageLoadDoesNotAnnounce() {
		connect();
		waitResponse();

		assertTrue(jq(POLITE).exists(), "no announcer: za11y is not loaded");
		assertEquals(0, jq(POLITE + " > div").length(),
				"arriving on a page is not a status message");
	}

	@Test
	public void plainPagingDoesNotAnnounce() {
		connect();
		waitResponse();

		click(jq("$plain .z-paging-next"));
		waitResponse();

		assertTrue(jq(POLITE).exists(), "no announcer: za11y is not loaded");
		assertEquals(0, jq(POLITE + " > div").length(),
				"nothing is shown to announce when detailed is off");
	}

	@Test
	public void emptyingTheResultSetDoesNotAnnounce() {
		connect();
		waitResponse();

		click(jq("$detailed .z-paging-next"));
		waitResponse();
		click(jq("$empty"));
		waitResponse();

		assertEquals(1, jq(POLITE + " > div").length(),
				"no summary is shown when empty, polite log was: " + jq(POLITE).text());
	}
}
