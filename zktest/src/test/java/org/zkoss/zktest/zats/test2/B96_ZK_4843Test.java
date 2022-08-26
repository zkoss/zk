/* B96_ZK_4843Test.java

		Purpose:
		
		Description:
		
		History:
				Wed Apr 07 17:49:14 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B96_ZK_4843Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button:eq(0)"));
		waitResponse();
		double period = getPeriod();

		MatcherAssert.assertThat("Response was too slow!", period, Matchers
			.lessThanOrEqualTo(1000.0));
	}

	private double getPeriod() {
		double period = Double.parseDouble(getZKLog().replaceAll("[^\\d.]", ""));
		closeZKLog();
		return period;
	}
}
