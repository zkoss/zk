/* B101_ZK_5756Test.java

	Purpose:

	Description:

	History:
		3:07 PM 2024/9/20, Created by jumperchen

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.Random;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B101_ZK_5756Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		Random random = new Random();
		for (int i = 0; i < 30; i++) {
			check(jq("$r1"));
			sleep(random.nextInt(100));
			check(jq("$r2"));
			assertNoZKError();
		}
	}
}
