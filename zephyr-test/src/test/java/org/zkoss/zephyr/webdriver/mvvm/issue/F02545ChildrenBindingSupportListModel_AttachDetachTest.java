/* F02545ChildrenBindingSupportListModel_AttachDetachTest.java
	Purpose:

	Description:

	History:
		Fri Apr 30 18:12:24 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class F02545ChildrenBindingSupportListModel_AttachDetachTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery list1 = jq("@window $list1");
		assertEquals(5, list1.toWidget().nChildren());
		assertEquals(5, jq("@window $list2").toWidget().nChildren());

		click(jq("@window $detachBtn"));
		waitResponse();
		assertEquals(0, jq("@window $list2").length());

		click(jq("@window $addBtn"));
		waitResponse();
		assertEquals(7, list1.toWidget().nChildren());

		click(jq("@window $attachBtn"));
		waitResponse();
		assertEquals(7, list1.toWidget().nChildren());
		assertEquals(7, jq("@window $list2").toWidget().nChildren());
	}
}