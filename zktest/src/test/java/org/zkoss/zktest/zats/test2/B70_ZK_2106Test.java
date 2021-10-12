/* B70_ZK_2106Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Mar 26 17:51:25 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Dimension;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B70_ZK_2106Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		driver.manage().window().setSize(new Dimension(960, 1080));
		waitResponse();
		Assert.assertEquals(
				jq("@columnchildren:eq(0)").positionTop(),
				jq("@columnchildren:eq(1)").positionTop());

		driver.manage().window().setSize(new Dimension(720, 1080));
		waitResponse();
		Assert.assertEquals(
				jq("@columnchildren:eq(0)").positionTop(),
				jq("@columnchildren:eq(1)").positionTop());
	}
}
