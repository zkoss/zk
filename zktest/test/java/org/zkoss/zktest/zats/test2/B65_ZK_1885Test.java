/* B65_ZK_1885Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Dec 11 09:38:41 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Dimension;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B65_ZK_1885Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery div = jq("@div");
		div.scrollTop(100);
		waitResponse();
		driver.manage().window().setSize(new Dimension(640, 480));
		waitResponse();

		Assert.assertNotEquals(0, div.scrollTop());
	}
}
