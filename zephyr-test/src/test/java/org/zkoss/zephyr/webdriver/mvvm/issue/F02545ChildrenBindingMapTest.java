/* F02545ChildrenBindingMapTest.java
	Purpose:

	Description:

	History:
		Fri Apr 30 18:12:24 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.issue;


import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class F02545ChildrenBindingMapTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery cbm1 = jq("$w $cbm1");
		assertEquals(5, cbm1.toWidget().nChildren());

		click(jq("$serialization"));
		waitResponse();
		cbm1 = jq("$w $cbm1");
		assertEquals(5, cbm1.toWidget().nChildren());

		assertTrue(jq("$msg").text().contains("done deserialize:"), "Should be able to serialized");
	}
}