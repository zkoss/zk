/* F110_ZK_6085_AnchornavTargetSizeTest.java

	Purpose:
		WCAG 2.5.8 Target Size (Minimum) for the links an anchornav tracks.

	Description:
		The tracked links are inline text, one text line apart, so they pass neither
		on size nor on spacing. za11y enlarges them in zkmax/less/_nav-a11y.less.

	History:
		Wed Oct 14 2026, Created by peggypeng

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * WCAG 2.5.8 Target Size (Minimum) - https://www.w3.org/WAI/WCAG22/Understanding/target-size-minimum
 */
@Tag("WcagTestOnly")
public class F110_ZK_6085_AnchornavTargetSizeTest extends WebDriverTestCase {
	/** The minimum width and height of a pointer target, in CSS px. */
	private static final double MIN = 24;
	/** The layouts holding the tracked links, three links each. */
	private static final String[] LAYOUTS = {"list", "column", "row"};

	@Test
	public void trackedLinksMeetTheMinimum() {
		connect();
		waitResponse();

		for (String layout : LAYOUTS) {
			String[] links = getEval("jq('$" + layout + "').find('a[data-anchornav-target]').map(function () {"
					+ " var r = this.getBoundingClientRect(); return this.textContent + ':' + r.width + ':' + r.height;"
					+ " }).get().join('|')").split("\\|");
			Assertions.assertEquals(3, links.length, layout + ": the anchornav must track three links");
			for (String link : links) {
				String[] parts = link.split(":");
				double width = Double.parseDouble(parts[1]), height = Double.parseDouble(parts[2]);
				Assertions.assertTrue(width >= MIN && height >= MIN, layout + ", " + parts[0]
						+ ": a tracked link must be at least " + MIN + "px square, was " + width + "x" + height);
			}
		}
	}
}
