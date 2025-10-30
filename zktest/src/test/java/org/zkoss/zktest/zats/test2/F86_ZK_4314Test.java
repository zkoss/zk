package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;

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
