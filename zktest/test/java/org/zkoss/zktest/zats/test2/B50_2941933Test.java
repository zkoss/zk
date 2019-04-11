/* B50_2941933Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Mar 20 15:37:20 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B50_2941933Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		rightClick(jq("@treerow:eq(0)"));
		waitResponse();
		Assert.assertTrue(jq("@menupopup:visible").exists());
		Assert.assertTrue(jq("@treerow:eq(0)").hasClass("z-treerow-selected"));

		rightClick(jq("@treerow:contains(alert)"));
		waitResponse();
		Assert.assertTrue(jq(".z-messagebox-window").exists());
	}
}
