/* F86_ZK_2756_2Test.java

	Purpose:

	Description:

	History:
		Mon Sep 03 17:23:52 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F86_ZK_2756_2Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		Assert.assertEquals(0, jq("@select > optgroup[label=\"Group 1\"]").find("option").length());
		Assert.assertEquals(2, jq("@select > optgroup[label=\"Group 2\"]").find("option:disabled").length());
		Assert.assertEquals(1, jq("@select > optgroup[label=\"Group 3\"]").find("option").length());
		Assert.assertTrue(jq("@select").find("option:contains(Item 7)").exists());
		Assert.assertFalse(jq("@select > optgroup[label=\"Group 4\"]").exists());

		click(jq("@button:contains(Toggle Group 1 open)"));
		waitResponse();
		Assert.assertNotEquals(0, jq("@select > optgroup[label=\"Group 1\"]").find("option").length());

		click(jq("@button:contains(Toggle Item 2 visible)"));
		waitResponse();
		Assert.assertEquals(3, jq("@select > optgroup[label=\"Group 1\"]").find("option").length());

		click(jq("@button:contains(Toggle Group 2 disabled)"));
		waitResponse();
		Assert.assertEquals(0, jq("@select > optgroup[label=\"Group 2\"]").find("option:disabled").length());

		click(jq("@button:contains(Toggle Group 4 visible)"));
		waitResponse();
		Assert.assertTrue(jq("@select > optgroup[label=\"Group 4\"]").exists());
		Assert.assertEquals(1, jq("@select > optgroup[label=\"Group 3\"]").find("option").length());
		Assert.assertEquals(1, jq("@select > optgroup[label=\"Group 4\"]").find("option").length());

		click(jq("@button:contains(Detach Group 3)"));
		waitResponse();
		Assert.assertEquals(3, jq("@select > optgroup[label=\"Group 2\"]").find("option").length());

		click(jq("@button:contains(Insert Group 5 before Group 1)"));
		waitResponse();
		Assert.assertEquals("Group 5", jq("@select > optgroup:eq(0)").attr("label"));

		click(jq("@button:contains(Insert Item 1.1 before Item 2)"));
		waitResponse();
		Assert.assertEquals("Item 1.1", jq("@select").find("option:contains(Item 2)").prev().text());

		click(jq("@button:contains(Change Group 4 label)"));
		waitResponse();
		click(jq("@button:contains(Change Item 7 label)"));
		waitResponse();
		Assert.assertEquals("Group 4.", jq("@select").find("optgroup[label=\"Group 4.\"]").attr("label"));
		Assert.assertEquals("Item 7.", jq("@select").find("option:contains(Item 7)").text());

		click(jq("@button:contains(Set maxlength 5)"));
		waitResponse();
		Assert.assertEquals("Group...", jq("@select").find("optgroup:eq(0)").attr("label"));
		Assert.assertEquals("Item...", jq("@select").find("option:eq(0)").text());
	}
}
