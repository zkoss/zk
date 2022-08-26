/* WcagTestCase.java

	Purpose:
		
	Description:
		
	History:
		Fri May 22 15:45:52 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import java.util.Arrays;

import com.deque.html.axecore.results.Results;
import com.deque.html.axecore.selenium.AxeBuilder;
import com.deque.html.axecore.selenium.AxeReporter;
import org.junit.jupiter.api.Assertions;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A basic test case class for accessibility
 * @author rudyhuang
 */
public abstract class WcagTestCase extends WebDriverTestCase {
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
		// FIXME: Temporary disabled color-contrast
		AxeBuilder builder = new AxeBuilder()
				.withTags(Arrays.asList("wcag2a", "wcag2aa"))
				.disableRules(Arrays.asList("color-contrast"));
		Results results = builder.analyze(driver);
		if (!results.violationFree()) {
			Assertions.fail(AxeReporter.getReadableAxeResults("WCAG", driver, results.getViolations())
					? AxeReporter.getAxeResultString()
					: null);
		}
	}
}
