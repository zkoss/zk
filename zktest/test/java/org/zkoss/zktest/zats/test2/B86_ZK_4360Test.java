/* B86_ZK_4360Test.java

		Purpose:
		
		Description:
		
		History:
				Fri Aug 23 15:50:18 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.interactions.Actions;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B86_ZK_4360Test extends WebDriverTestCase {
	@Test
	public void test() {
		Actions act = new Actions(connect());
		click(jq(".z-tab").eq(1));
		waitResponse();
		click(jq("iframe"));
		waitResponse();
		act.sendKeys("a").perform();
		waitResponse();
		Assert.assertEquals("onChanging", getZKLog());
	}
}
