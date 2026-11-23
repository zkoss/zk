/* Component_SwitchTest.java

		Purpose:
		
		Description:
		
		History:
				Thu Apr 29 12:38:08 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.basic;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class Component_SwitchTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect("/bind/basic/component-switch.zul");

		JQuery t1 = jq("$t1");
		JQuery t2 = jq("$t2");
		JQuery v1 = jq("$v1");
		JQuery v2 = jq("$v2");
		JQuery l1 = jq("$l1");
		JQuery l2 = jq("$l2");

		Assert.assertEquals("Item 1", l1.text());
		Assert.assertEquals("Item 2", l2.text());

		type(t1, "A");
		waitResponse();
		Assert.assertEquals("A", l1.text());
		Assert.assertEquals("Item 2", l2.text());

		type(t2, "B");
		waitResponse();
		Assert.assertEquals("A", l1.text());
		Assert.assertEquals("B", l2.text());

		getActions().dragAndDrop(toElement(l2), toElement(v1)).perform();
		waitResponse();
		Assert.assertEquals("A", l1.text());
		Assert.assertEquals("A", l2.text());

		getActions().dragAndDrop(toElement(l1), toElement(v2)).perform();
		waitResponse();
		Assert.assertEquals("B", l1.text());
		Assert.assertEquals("A", l2.text());

		type(t1, "C");
		waitResponse();
		Assert.assertEquals("B", l1.text());
		Assert.assertEquals("C", l2.text());

		type(t2, "D");
		waitResponse();
		Assert.assertEquals("D", l1.text());
		Assert.assertEquals("C", l2.text());
	}
}
