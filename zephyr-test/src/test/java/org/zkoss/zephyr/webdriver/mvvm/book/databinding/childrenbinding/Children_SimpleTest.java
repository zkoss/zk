/* Children_SimpleTest.java

		Purpose:
		
		Description:
		
		History:
				Tue Apr 27 12:12:15 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.databinding.childrenbinding;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.TestStage;
import org.zkoss.zephyr.webdriver.ZephyrClientMVVMTestCase;

public class Children_SimpleTest extends ZephyrClientMVVMTestCase {
	@Test
	public void test() {
		connect("/mvvm/book/databinding/childrenbinding/children-simple.zul");

		checkAllChildren(3, 3, 0, 3);
		checkDependencyDynamicTemplate();

		click(jq("@button:contains(reload 1)"));
		waitResponse();

		checkAllChildren(3, 4, 0, 4);
		checkDependencyDynamicTemplate();

		click(jq("@button:contains(reload 2)"));
		waitResponse();

		checkAllChildren(3, 4, 4, 4);
		checkDependencyDynamicTemplate();

		click(jq("@button:contains(reload 3)"));
		waitResponse();

		checkAllChildren(3, 4, 4, 4);
		checkFirstTwoChildren();
		checkDependencyDynamicTemplate();

		click(jq("@button:contains(clear)"));
		waitResponse();
		checkAllChildren(3, 0, 4, 0);
	}

	private void checkAllChildren(int expectedInit, int expectedLoad, int expectedAftercmd, int expectedDependency) {
		assertEquals(expectedInit, jq("$init").children().length(), "init should have " + expectedInit + " children");
		assertEquals(expectedLoad, jq("$load").children().length(), "load should have " + expectedLoad + " children");
		assertEquals(expectedAftercmd, jq("$aftercmd").children().length(),
				"aftercmd should have " + expectedAftercmd + " children");
		assertEquals(expectedDependency, jq("$dependency").children().length(),
				"dependency should have " + expectedDependency + " children");
	}

	private void checkDependencyDynamicTemplate() {
		assertEquals(1, jq("$dependency .c1").length(), "should only have 1 className c1");
		assertEquals(jq("$dependency .z-label").length() - 1, jq("$dependency .c2").length(), "others should be c2");
		assertEquals("Item A", jq("$dependency .c1").text(), "the text of c1 should be Item A");
	}

	private void checkFirstTwoChildren() {
		String message0 = "index0 should be Item X";
		String message1 = "index1 should be Item A";
		assertEquals("Item X", jq("$load .z-label:eq(0)").text(), message0);
		assertEquals("Item A", jq("$load .z-label:eq(1)").text(), message1);
		assertEquals("Item X", jq("$dependency .z-label:eq(0)").text(), message0);
		assertEquals("Item A", jq("$dependency .z-label:eq(1)").text(), message1);
	}
}
