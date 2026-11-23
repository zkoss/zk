/* F80_ZK_3041Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Apr 18 10:18:30 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class F80_ZK_3041Test extends WebDriverTestCase {
	@Test
	public void test1() {
		connect();

		JQuery cb = jq("@chosenbox:eq(0)");
		click(cb);
		waitResponse(true);
		Assert.assertEquals(4, jq(".z-chosenbox-option:visible").length());

		getActions().sendKeys("a").perform();
		waitResponse();
		Assert.assertEquals(2, jq(".z-chosenbox-option:visible").length());

		click(jq(".z-chosenbox-option:visible:eq(0)"));
		waitResponse();
		Assert.assertEquals(1, cb.find(".z-chosenbox-item").length());

		click(cb);
		waitResponse(true);
		getActions().sendKeys("a").perform();
		waitResponse();
		Assert.assertEquals(1, jq(".z-chosenbox-option:visible").length());
		click(jq(".z-chosenbox-option:visible:eq(0)"));
		waitResponse();
		Assert.assertEquals(2, cb.find(".z-chosenbox-item").length());
	}

	@Test
	public void test2() {
		connect();

		JQuery cb = jq("@chosenbox:eq(1)");
		click(cb);
		waitResponse(true);
		Assert.assertEquals(4, jq(".z-chosenbox-option:visible").length());

		getActions().sendKeys("a").perform();
		waitResponse();
		Assert.assertEquals(1, jq(".z-chosenbox-option:visible").length());
		Assert.assertEquals("Adam adam@company.org", jq(".z-chosenbox-option:visible").text());

		click(jq(".z-chosenbox-option:visible:eq(0)"));
		waitResponse();
		Assert.assertEquals(1, cb.find(".z-chosenbox-item").length());
	}

	@Test
	public void test3() {
		connect();

		JQuery cb = jq("@chosenbox:eq(2)");
		click(cb);
		waitResponse(true);
		Assert.assertEquals(0, jq(".z-chosenbox-option:visible").length());

		getActions().sendKeys("t").perform();
		waitResponse();
		Assert.assertEquals(5, jq(".z-chosenbox-option:visible").length());

		click(jq(".z-chosenbox-option:visible:eq(0)"));
		waitResponse();
		Assert.assertEquals(1, cb.find(".z-chosenbox-item").length());

		click(cb);
		waitResponse(true);
		getActions().sendKeys("t").perform();
		waitResponse();
		Assert.assertEquals(4, jq(".z-chosenbox-option:visible").length());
		click(jq(".z-chosenbox-option:visible:eq(0)"));
		waitResponse();
		Assert.assertEquals(2, cb.find(".z-chosenbox-item").length());
	}

	@Test
	public void test4() {
		connect();

		JQuery cb = jq("@chosenbox:eq(3)");
		click(cb);
		waitResponse(true);
		Assert.assertEquals(0, jq(".z-chosenbox-option:visible").length());

		getActions().sendKeys("t").perform();
		waitResponse();
		Assert.assertEquals(4, jq(".z-chosenbox-option:visible").length());

		click(jq(".z-chosenbox-option:visible:eq(0)"));
		waitResponse();
		Assert.assertEquals(1, cb.find(".z-chosenbox-item").length());
	}
}
