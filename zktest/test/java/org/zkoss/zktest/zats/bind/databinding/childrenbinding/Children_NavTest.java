/* Children_NavTest.java

		Purpose:
		
		Description:
		
		History:
				Tue Apr 27 16:05:03 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.databinding.childrenbinding;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class Children_NavTest extends WebDriverTestCase {
	@Test
	public void structureTest() {
		connect("/bind/databinding/childrenbinding/children-nav.zul");

		Assert.assertEquals(0, jq(".z-nav:contains(Item A)").length());
		Assert.assertEquals(1, jq(".z-navitem:contains(Item A)").length());

		Assert.assertEquals(1, jq(".z-nav:contains(Item B)").length());
		Assert.assertEquals(1, jq(".z-navitem:contains(Item B)").length());

		Assert.assertEquals(2, jq(".z-nav:contains(Item C)").length());
		Assert.assertEquals(3, jq(".z-navitem:contains(Item C)").length());

		Assert.assertEquals(7, jq(".z-nav:contains(Item D)").length());
		Assert.assertEquals(15, jq(".z-navitem:contains(Item D)").length());

		click(jq(".z-nav:contains(Item C)"));
		waitResponse();
		click(jq(".z-nav:contains(Item C_0)"));
		waitResponse();
		click(jq(".z-navitem:contains(Item C_0_1)"));
		waitResponse();
		Assert.assertEquals("clicked " + jq(".z-navitem:contains(Item C_0_1)").text().trim(), jq("$msg").text());
	}
}
