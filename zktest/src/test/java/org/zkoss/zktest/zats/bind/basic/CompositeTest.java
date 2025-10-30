/* CompositeTest.java

		Purpose:
		
		Description:
		
		History:
				Thu Apr 29 10:47:31 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.basic;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

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

		Assertions.assertEquals("Dennis", e1.text().trim());
		Assertions.assertEquals("", e2.text().trim(), "Not work currently");
		Assertions.assertEquals("RD", e3.text().trim());
		Assertions.assertEquals("Dennis", l1.text().trim());
		Assertions.assertEquals("100", l2.text().trim());
		Assertions.assertEquals("RD", l3.text().trim());

		click(jq("@button:contains(reset)"));
		waitResponse();

		Assertions.assertEquals("Lin", e1.text().trim());
		Assertions.assertEquals("", e2.text().trim(), "Not work currently");
		Assertions.assertEquals("MVP", e3.text().trim());
		Assertions.assertEquals("Lin", l1.text().trim());
		Assertions.assertEquals("34", l2.text().trim());
		Assertions.assertEquals("MVP", l3.text().trim());
	}
}
