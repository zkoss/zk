/* WcagTestCase.java

	Purpose:
		
	Description:
		
	History:
		Fri May 22 15:45:52 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.deque.html.axecore.args.AxeRuleOptions;
import com.deque.html.axecore.args.AxeRunOnlyOptions;
import com.deque.html.axecore.args.AxeRunOptions;
import com.deque.html.axecore.results.Results;
import com.deque.html.axecore.selenium.AxeBuilder;
import com.deque.html.axecore.selenium.AxeReporter;
import org.junit.jupiter.api.Assertions;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A basic test case class for accessibility.
 * @author rudyhuang
 */
public abstract class WcagTestCase extends WebDriverTestCase {

	/** WCAG A + AA rule tags across 2.0 / 2.1 — the conformance set. */
	private static final List<String> WCAG_AA_TAGS =
			Arrays.asList("wcag2a", "wcag2aa", "wcag21a", "wcag21aa");

	/**
	 * WCAG 2.1 rules that axe ships as {@code experimental}: 1.3.4 Orientation and 2.5.3 Label in Name.
	 */
	private static final List<String> EXPERIMENTAL_WCAG21_RULES =
			Arrays.asList("css-orientation-lock", "label-content-name-mismatch");

	@Override
	protected String getFileLocation() {
		String simple = getClass().getSimpleName();
		String name = getClass().getName().replace("org.zkoss.zktest.zats", "").replace(".","/").replace(simple, "");
		String file = String.valueOf(simple.charAt(0)).toLowerCase() + simple.substring(1).replace("Test", "");
		return name + file + getFileExtension();
	}

	/**
	 * Verify accessibility issues.
	 * If there is any issue, test will fail.
	 */
	protected void verifyA11y() {
		verifyAxe("color-contrast");
	}

	/**
	 * As {@link #verifyA11y()} but gates on every rule, {@code color-contrast} included — for runs
	 * under a WCAG theme.
	 */
	protected void verifyAxe() {
		verifyAxe(new String[0]);
	}

	/**
	 * Runs an axe-core WCAG A/AA scan on the page <b>in its current state</b> and fails on any
	 * violation. Because it scans the live Selenium session, call it <b>after</b> driving the
	 * component into the state under test (open the popup, expand the tree, select a row…), not
	 * just after {@code connect()}.
	 *
	 * @param disabledRules axe rule ids to skip (may be empty)
	 */
	protected void verifyAxe(String... disabledRules) {
		AxeRunOnlyOptions runOnly = new AxeRunOnlyOptions();
		runOnly.setType("tag");
		runOnly.setValues(new ArrayList<>(WCAG_AA_TAGS));

		Map<String, AxeRuleOptions> rules = new HashMap<>();
		for (String rule : EXPERIMENTAL_WCAG21_RULES)
			rules.put(rule, ruleOption(true));
		for (String rule : disabledRules)
			rules.put(rule, ruleOption(false));

		AxeRunOptions options = new AxeRunOptions();
		options.setRunOnly(runOnly);
		options.setRules(rules);

		Results results = new AxeBuilder().withOptions(options).analyze(getWebDriver());
		if (!results.violationFree()) {
			AxeReporter.getReadableAxeResults("WCAG", getWebDriver(), results.getViolations());
			Assertions.fail(AxeReporter.getAxeResultString());
		}
	}

	private static AxeRuleOptions ruleOption(boolean enabled) {
		AxeRuleOptions option = new AxeRuleOptions();
		option.setEnabled(enabled);
		return option;
	}
}
