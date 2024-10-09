/* B101_ZK_5766_1Test.java

	Purpose:

	Description:

	History:
		10:32 AM 2024/10/9, Created by jumperchen

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jumperchen
 */
public class B101_ZK_5766_1Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		for (JQuery jQuery : jq(".withBadge")) {
			assertEquals("none", jQuery.css("backgroundImage"));
		}
	}
}
