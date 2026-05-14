/* B110_ZK_6101_2Test.java

		Purpose:

		Description:

		History:
				Wed May 13 17:15:19 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.not;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import org.zkoss.test.webdriver.ExternalZkXml;
import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * Verifies that an ignorable AU request (onTimer with {@code opt_i}) observing the timeout does
 * not redirect to the configured {@code timeout-uri} when {@code automatic-timeout} is disabled.
 *
 * @author peakerlee
 */
@ForkJVMTestOnly
public class B110_ZK_6101_2Test extends WebDriverTestCase {
	@RegisterExtension
	public static final ExternalZkXml CONFIG = new ExternalZkXml(B110_ZK_6101_2Test.class);

	@Test
	public void test() {
		connect();
		waitResponse();
		// wait past session-timeout (10s) so the timer's next onTimer observes the expired desktop
		sleep(15000);
		MatcherAssert.assertThat(driver.getCurrentUrl(), not(endsWith("timeout.zul")));
	}
}
