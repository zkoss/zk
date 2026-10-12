/* F110_ZK_6084_LoadingbarTest.java

		Purpose:

		Description:

		History:
				Mon Sep 07 17:20:00 CST 2026, Created by peggypeng

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * WCAG 4.1.3: a progressbar is not a live region, so Loadingbar announces progress through
 * a sibling polite region, throttled to one announcement per 25% band crossed.
 */
@Tag("WcagTestOnly")
public class F110_ZK_6084_LoadingbarTest extends WebDriverTestCase {
	private static final String STATUS = ".z-loadingbar + [aria-live=polite]";

	@Test
	public void crossingABandAnnounces() {
		connect();
		waitResponse();

		click(jq("$start"));
		waitResponse(true);
		click(jq("$to26"));
		waitResponse(true);

		assertTrue(jq(STATUS).text().contains("26"),
				"status region was: " + jq(STATUS).text());
	}

	@Test
	public void sameBandDoesNotAnnounceAgain() {
		connect();
		waitResponse();

		click(jq("$start"));
		waitResponse(true);
		click(jq("$to26"));
		waitResponse(true);
		click(jq("$to27"));
		waitResponse(true);

		assertFalse(jq(STATUS).text().contains("27"),
				"27 is in the same band as 26, status region was: " + jq(STATUS).text());
	}

	@Test
	public void indeterminateClearsTheRegion() {
		connect();
		waitResponse();

		click(jq("$start"));
		waitResponse(true);
		click(jq("$to26"));
		waitResponse(true);
		click(jq("$indeterminate"));
		waitResponse(true);

		assertEquals("", jq(STATUS).text().trim(),
				"there is no progress to report while indeterminate");
	}
}
