/* Children_SimpleTest.java

		Purpose:
		
		Description:
		
		History:
				Tue Apr 27 12:12:15 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.databinding.childrenbinding;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class Children_SimpleTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect("/bind/databinding/childrenbinding/children-simple.zul");

		checkAllChildren(3, 3, 0, 3);
		checkDependencyDynamicTemplate();

		click(jq("@button:contains(reload 1)"));
		waitResponse();

		checkAllChildren(3, 4, 0, 4);
		checkDependencyDynamicTemplate();

		click(jq("@button:contains(reload 2)"));
		waitResponse();

		checkAllChildren(3, 4, 5, 4);
		checkDependencyDynamicTemplate();

		click(jq("@button:contains(reload 3)"));
		waitResponse();

		checkAllChildren(3, 4, 5, 5);
		checkFirstTwoChildren();
		checkDependencyDynamicTemplate();

		click(jq("@button:contains(clear)"));
		waitResponse();
		checkAllChildren(3, 0, 5, 0);
	}

	private void checkAllChildren(int expectedInit, int expectedLoad, int expectedAftercmd, int expectedDependency) {
		Assert.assertEquals("init should have " + expectedInit + " children", expectedInit, jq("$init").children().length());
		Assert.assertEquals("load should have " + expectedLoad + " children", expectedLoad, jq("$load").children().length());
		Assert.assertEquals("aftercmd should have " + expectedAftercmd + " children", expectedAftercmd, jq("$aftercmd").children().length());
		Assert.assertEquals("dependency should have " + expectedDependency + " children", expectedDependency, jq("$dependency").children().length());
	}

	private void checkDependencyDynamicTemplate() {
		Assert.assertEquals("should only have 1 className c1", 1, jq("$dependency .c1").length());
		Assert.assertEquals("others should be c2", jq("$dependency .z-label").length() - 1, jq("$dependency .c2").length());
		Assert.assertEquals("the text of c1 should be Item A", "Item A", jq("$dependency .c1").text());
	}

	private void checkFirstTwoChildren() {
		String message0 = "index0 should be Item X";
		String message1 = "index1 should be Item A";
		Assert.assertEquals(message0, "Item X", jq("$init .z-label:eq(0)").text());
		Assert.assertEquals(message1, "Item A", jq("$init .z-label:eq(1)").text());
		Assert.assertEquals(message0, "Item X", jq("$load .z-label:eq(0)").text());
		Assert.assertEquals(message1, "Item A", jq("$load .z-label:eq(1)").text());
		Assert.assertEquals(message0, "Item X", jq("$aftercmd .z-label:eq(0)").text());
		Assert.assertEquals(message1, "Item A", jq("$aftercmd .z-label:eq(1)").text());
		Assert.assertEquals(message0, "Item X", jq("$dependency .z-label:eq(0)").text());
		Assert.assertEquals(message1, "Item A", jq("$dependency .z-label:eq(1)").text());
	}
}
