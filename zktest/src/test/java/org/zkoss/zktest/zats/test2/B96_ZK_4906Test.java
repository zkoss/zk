/* B96_ZK_4906Test.java

	Purpose:
		
	Description:
		
	History:
		Tue May 25 11:33:44 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B96_ZK_4906Test extends WebDriverTestCase {
	@Test
	public void testWay1() {
		connect();

		mouseOver(jq("$menu1"));
		mouseOver(jq("$menuitem1"));
		mouseOver(jq("$menu3"));
		Assertions.assertTrue(jq("$menu3popup").isVisible(),
				"the menupopup should be visible");
	}

	@Test
	public void testWay2() {
		connect();

		mouseOver(jq("$menu1"));
		mouseOver(jq("$menu2"));
		mouseOver(jq("$menuitem4"));
		mouseOver(jq("$menuitem5"));
		mouseOver(jq("$menuitem7"));
		Assertions.assertTrue(jq("$menu1popup").isVisible(),
				"the menupopup should be visible");
	}
}
