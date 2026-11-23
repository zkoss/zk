/* B96_ZK_4884Test.java

	Purpose:
		
	Description:
		
	History:
		Tue May 18 09:50:24 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B96_ZK_4884Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("$menu1"));
		waitResponse();
		click(jq("$menuitem1"));
		waitResponse();
		Assert.assertEquals(1, jq(".z-menu-hover").length());

		click(jq("$menu2"));
		waitResponse();
		click(jq("$menuitem2"));
		waitResponse();
		Assert.assertEquals(1, jq(".z-menu-hover").length());
	}
}
