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

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ClientBindTestCase;

/**
 * @author rudyhuang
 */
public class PerformanceTest extends ClientBindTestCase {
	@Test
	public void test() throws Exception {
		connect("/mvvm/book/basic/performance.zul");

		final long start = System.currentTimeMillis();
		for (int i = 0; i < 10; i++) {
			click(jq("@window $btnSwitch"));
			Thread.sleep(100);
		}
		final long actualMillis = System.currentTimeMillis() - start;
		MatcherAssert.assertThat(actualMillis, lessThanOrEqualTo(SECONDS.toMillis(2)));
	}
}
