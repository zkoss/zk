/* CompositeTest.java

		Purpose:
		
		Description:
		
		History:
				Thu Apr 29 10:47:31 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.basic;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class CompositeTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect("/bind/basic/composite.zul");

		JQuery e1 = jq("$e1");
		JQuery e2 = jq("$e2");
		JQuery e3 = jq("$e3");
		JQuery l1 = jq("$l1");
		JQuery l2 = jq("$l2");
		JQuery l3 = jq("$l3");

		Assert.assertEquals("Dennis", e1.text().trim());
		Assert.assertEquals("Not work currently", "", e2.text().trim());
		Assert.assertEquals("RD", e3.text().trim());
		Assert.assertEquals("Dennis", l1.text().trim());
		Assert.assertEquals("100", l2.text().trim());
		Assert.assertEquals("RD", l3.text().trim());

		click(jq("@button:contains(reset)"));
		waitResponse();

		Assert.assertEquals("Lin", e1.text().trim());
		Assert.assertEquals("Not work currently", "", e2.text().trim());
		Assert.assertEquals("MVP", e3.text().trim());
		Assert.assertEquals("Lin", l1.text().trim());
		Assert.assertEquals("34", l2.text().trim());
		Assert.assertEquals("MVP", l3.text().trim());
	}
}
