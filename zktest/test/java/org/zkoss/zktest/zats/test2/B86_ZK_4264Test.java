/* B86_ZK_4264Test.java

		Purpose:
		
		Description:
		
		History:
				Wed May 15 18:24:21 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Dimension;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B86_ZK_4264Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		int bodyHeight = jq(".z-listbox-body").height();
		driver.manage().window().setSize(new Dimension(960, 1080));
		waitResponse();
		Assert.assertEquals(bodyHeight, jq(".z-listbox-body").height());
	}
}
