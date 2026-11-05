/* B110_ZK_6112_TonalTextContrastTest.java

	Purpose:
		Marble: small text on a tonal container, or faded with opacity, reaches 4.5:1 (WCAG 1.4.3).

	Description:
		ZK-6112 drew the Portallayout counter, the selected Paging and Navbar items and the Chip
		severities in the role colour on its own tint, and faded the Calendar's previous/next-month
		days and week numbers with the disabled opacity, all below 4.5:1. They now use the on-container
		colours. WcagTestCase.verifyA11y() skips color-contrast, so this runs that one rule on just
		these components.

		Marble only: IceBlue's default colours are below 4.5:1 here too (its WCAG themes are not),
		so the test is skipped under -Dzktest.theme=iceblue.

	History:
		Thu Nov 05 2026, Created by hawk

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.deque.html.axecore.results.Results;
import com.deque.html.axecore.results.Rule;
import com.deque.html.axecore.selenium.AxeBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B110_ZK_6112_TonalTextContrastTest extends WebDriverTestCase {
	private static final List<String> COMPONENTS = Arrays.asList(
			"$portal", "$cal", "$paging", "$navbar", "$chips");

	@Test
	public void textReachesTheContrastMinimum() {
		Assumptions.assumeFalse("iceblue".equals(System.getProperty("zktest.theme")),
				"IceBlue's default colours are below 4.5:1 here; this asserts Marble's");
		connect();
		// axe leaves one-character text undecided, so the page gives these two digits
		Assertions.assertEquals("10", jq(".z-portalchildren-counter").text(), "precondition: a two-digit counter");
		Assertions.assertEquals("10", jq(".z-paging-selected").text(), "precondition: a two-digit selected page");
		Assertions.assertEquals("29", jq(".z-calendar-outside:first").text(), "precondition: two-digit days of the previous month");

		AxeBuilder axe = new AxeBuilder().withOnlyRules(Arrays.asList("color-contrast"));
		for (String root : componentRoots())
			axe.include(root);
		Results results = axe.analyze(getWebDriver());
		Assertions.assertTrue(results.getViolations().isEmpty(), describe(results.getViolations()));
	}

	/** axe takes CSS selectors, so each ZK id becomes its root element's uuid. */
	private List<String> componentRoots() {
		return COMPONENTS.stream()
				.map(id -> "#" + jq(id).attr("id"))
				.collect(Collectors.toList());
	}

	private static String describe(List<Rule> violations) {
		return violations.stream()
				.flatMap(rule -> rule.getNodes().stream())
				.map(node -> node.getTarget() + ": " + node.getFailureSummary())
				.collect(Collectors.joining("\n", "text below 4.5:1:\n", ""));
	}
}
