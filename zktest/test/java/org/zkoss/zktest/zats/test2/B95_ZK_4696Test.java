/* B95_ZK_4694Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Oct 07 15:14:15 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.Matchers.containsString;

import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Dimension;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B95_ZK_4696Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button:eq(0)"));
		waitResponse();
		Assert.assertEquals("Received from inc1", getZKLog());

		closeZKLog();
		click(jq("@button:eq(1)"));
		waitResponse();
		Assert.assertFalse(isZKLogAvailable());

		driver.manage().window().setSize(new Dimension(800, 600));
		waitResponse();
		final String zkLog = getZKLog();
		MatcherAssert.assertThat(zkLog, containsString("Received from inc1"));
		MatcherAssert.assertThat(zkLog, containsString("Received from inc2"));
	}
}
