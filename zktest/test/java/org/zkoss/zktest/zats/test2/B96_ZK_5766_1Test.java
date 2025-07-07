/* B96_ZK_5766_1Test.java

	Purpose:

	Description:

	History:
		10:32 AM 2024/10/9, Created by jumperchen

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author jumperchen
 */
public class B96_ZK_5766_1Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		for (JQuery jQuery : jq(".withBadge")) {
			assertEquals("none", jQuery.css("backgroundImage"));
		}
	}
}
