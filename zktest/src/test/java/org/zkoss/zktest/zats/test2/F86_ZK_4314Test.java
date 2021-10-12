package org.zkoss.zktest.zats.test2;

import static junit.framework.TestCase.assertEquals;

import org.junit.Test;

import org.openqa.selenium.interactions.Actions;
import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jameschu
 */
public class F86_ZK_4314Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		Actions actions = getActions();
		actions.dragAndDrop(
				toElement(jq("$drag")),
				toElement(jq("$dropTarget")))
				.perform();
		waitResponse();

		String[] zkLogLines = getZKLog().split("\n");

		assertEquals("Start Dragging", zkLogLines[0]);
		assertEquals(true, zkLogLines[1].startsWith("onDrop - null"));
	}
}
