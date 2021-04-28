/* ChosenboxTest.java

	Purpose:
		
	Description:
		
	History:
		Mon Apr 26 16:17:45 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.comp;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class ChosenboxTest extends WebDriverTestCase {
	@Test
	public void testMultiple() {
		connect();

		click(jq("$cbox1"));
		waitResponse(true);
		click(jq(".z-chosenbox-option:contains(Item 0):visible"));
		waitResponse();
		Assert.assertEquals("[Item 0]", jq("$l1").text());

		click(jq("$cbox1"));
		waitResponse(true);
		click(jq(".z-chosenbox-option:contains(Item 3):visible"));
		waitResponse();
		Assert.assertEquals("[Item 0, Item 3]", jq("$l1").text());

		click(jq(".z-chosenbox-item:contains(Item 0) > .z-chosenbox-delete"));
		waitResponse();
		Assert.assertEquals("[Item 3]", jq("$l1").text());

		click(jq(".z-chosenbox-item:contains(Item 3) > .z-chosenbox-delete"));
		waitResponse();
		Assert.assertEquals("[]", jq("$l1").text());
	}

	@Test
	public void testSingle() {
		connect();

		click(jq("$cbox2"));
		waitResponse(true);
		click(jq(".z-chosenbox-option:contains(Item 0):visible"));
		waitResponse();
		Assert.assertEquals("0", jq("$l2").text());

		click(jq("$cbox2"));
		waitResponse(true);
		click(jq(".z-chosenbox-option:contains(Item 3):visible"));
		waitResponse();
		// A value of Chosenbox.getSelectedIndex is always the index of the first selected item
		Assert.assertEquals("0", jq("$l2").text());

		click(jq(".z-chosenbox-item > .z-chosenbox-delete"));
		waitResponse();
		Assert.assertEquals("-1", jq("$l2").text());
	}
}
