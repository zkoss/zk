/* B104_ZK_6101Test.java

		Purpose:

		Description:

		History:
				Wed May 13 17:15:19 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.Matchers.endsWith;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import org.zkoss.test.webdriver.ExternalZkXml;
import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * Verifies that {@code automatic-timeout} still triggers the configured
 * {@code timeout-uri} redirect when the AU request that observes the timeout
 * is ignorable (e.g. onTimer with {@code opt_i}).
 *
 * @author peakerlee
 */
@ForkJVMTestOnly
public class B104_ZK_6101Test extends WebDriverTestCase {
	@RegisterExtension
	public static final ExternalZkXml CONFIG = new ExternalZkXml(B104_ZK_6101Test.class);

	@Test
	public void test() {
		connect();
		waitResponse();
		// Wait past session-timeout (10s) + a margin so the timer's next ignorable
		// onTimer AU request observes the expired desktop and (with the fix)
		// triggers the AuSendRedirect to /timeout.zul.
		sleep(15000);
		MatcherAssert.assertThat(driver.getCurrentUrl(), endsWith("timeout.zul"));
	}
}
