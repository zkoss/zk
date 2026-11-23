/* B50_ZK_368Test.java

		Purpose:
		
		Description:
		
		History:
				Tue Apr 09 10:44:56 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.interactions.Actions;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B50_ZK_368Test extends WebDriverTestCase {
	@Test
	public void test() {
		Actions act = new Actions(connect());
		JQuery header = jq(".z-listheader").eq(0);
		act.moveToElement(toElement(header), 10, 10).build().perform();
		Assert.assertEquals("over", jq(".z-div").text());
		act.moveToElement(toElement(jq(".z-label").eq(0))).build().perform();
		Assert.assertEquals("out", jq(".z-div").text());
	}
}
