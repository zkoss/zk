/* B85_ZK_3906Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Jun 11 09:44:11 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B85_ZK_3906Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@colorbox"));
		waitResponse(true);

		JQuery color = jq(".z-colorbox-popup .z-colorpalette-input");
		Assertions.assertEquals("#6677FF", color.val());
	}
}
