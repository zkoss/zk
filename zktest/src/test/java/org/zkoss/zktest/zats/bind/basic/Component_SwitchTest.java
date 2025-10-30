/* Component_SwitchTest.java

		Purpose:
		
		Description:
		
		History:
				Thu Apr 29 12:38:08 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.basic;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

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

		Assertions.assertEquals("Item 1", l1.text());
		Assertions.assertEquals("Item 2", l2.text());

		type(t1, "A");
		waitResponse();
		Assertions.assertEquals("A", l1.text());
		Assertions.assertEquals("Item 2", l2.text());

		type(t2, "B");
		waitResponse();
		Assertions.assertEquals("A", l1.text());
		Assertions.assertEquals("B", l2.text());

		getActions().dragAndDrop(toElement(l2), toElement(v1)).perform();
		waitResponse();
		Assertions.assertEquals("A", l1.text());
		Assertions.assertEquals("A", l2.text());

		getActions().dragAndDrop(toElement(l1), toElement(v2)).perform();
		waitResponse();
		Assertions.assertEquals("B", l1.text());
		Assertions.assertEquals("A", l2.text());

		type(t1, "C");
		waitResponse();
		Assertions.assertEquals("B", l1.text());
		Assertions.assertEquals("C", l2.text());

		type(t2, "D");
		waitResponse();
		Assertions.assertEquals("D", l1.text());
		Assertions.assertEquals("C", l2.text());
	}
}
