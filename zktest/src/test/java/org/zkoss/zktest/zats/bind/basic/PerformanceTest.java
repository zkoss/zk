/* PerformanceTest.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 28 15:39:23 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.basic;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author rudyhuang
 */
public class PerformanceTest extends ZATSTestCase {
	@Test
	public void test() throws Exception {
		final DesktopAgent desktop = connect("/bind/basic/performance.zul");

		final long start = System.currentTimeMillis();
		for (int i = 0; i < 10; i++) {
			desktop.query("window #btnSwitch").click();
			Thread.sleep(100);
		}
		final long actualMillis = System.currentTimeMillis() - start;
		MatcherAssert.assertThat(actualMillis, lessThanOrEqualTo(SECONDS.toMillis(2)));
	}
}
