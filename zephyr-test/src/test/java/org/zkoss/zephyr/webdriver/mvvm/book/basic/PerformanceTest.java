/* PerformanceTest.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 28 15:39:23 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.basic;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class PerformanceTest extends WebDriverTestCase {
	@Test
	public void test() throws Exception {
		connect("/mvvm/book/basic/performance.zul");
		waitResponse();
		assertEquals(5, jq(".z-include .z-hbox").length());
		assertEquals("0", jq(".z-hbox").eq(0).find("@label").eq(0).text());
		final long start = System.currentTimeMillis();
		for (int i = 0; i < 10; i++) {
			click(jq("@window $btnSwitch"));
			Thread.sleep(100);
		}
		assertEquals("10", jq(".z-hbox").eq(0).find("@label").eq(0).text());
		final long actualMillis = System.currentTimeMillis() - start;
		MatcherAssert.assertThat(actualMillis, lessThanOrEqualTo(SECONDS.toMillis(2)));
	}
}
