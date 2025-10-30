/* F90_ZK_4380Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Sep 20 15:40:44 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.startsWith;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F90_ZK_4380Test extends WebDriverTestCase {
	@Test
	public void testSingleSelection() {
		connect();

		click(jq("@searchbox"));
		waitResponse(true);
		click(jq(".z-searchbox-item:eq(0)"));
		waitResponse();
		Assertions.assertEquals("Selected 1 items: [Item 1]", jq(widget("@searchbox").$n("label")).text());

		click(jq("@searchbox"));
		waitResponse(true);
		Assertions.assertTrue(jq(".z-searchbox-item:eq(0)").hasClass("z-searchbox-selected"));
		click(jq(".z-searchbox-item:eq(0)"));
		waitResponse();
		Assertions.assertEquals("Selected 0 items: []", jq(widget("@searchbox").$n("label")).text());
	}

	@Test
	public void testMultipleSelection() {
		connect();

		click(jq("@button:contains(multiple)"));
		waitResponse();
		click(jq("@button:contains(autoclose)"));
		waitResponse();

		click(jq("@searchbox"));
		waitResponse(true);
		click(jq(".z-searchbox-item:eq(0)"));
		click(jq(".z-searchbox-item:eq(1)"));
		click(jq(".z-searchbox-item:eq(2)"));
		click(jq(".z-searchbox-item:eq(3)"));
		waitResponse();
		Assertions.assertTrue(jq(".z-searchbox-item:eq(0)").hasClass("z-searchbox-selected"));
		Assertions.assertTrue(jq(".z-searchbox-item:eq(1)").hasClass("z-searchbox-selected"));
		Assertions.assertTrue(jq(".z-searchbox-item:eq(2)").hasClass("z-searchbox-selected"));
		Assertions.assertTrue(jq(".z-searchbox-item:eq(3)").hasClass("z-searchbox-selected"));
		click(jq("@searchbox"));
		waitResponse();

		click(jq("@searchbox"));
		waitResponse(true);
		click(jq(".z-searchbox-item:eq(0)"));
		Assertions.assertFalse(jq(".z-searchbox-item:eq(0)").hasClass("z-searchbox-selected"));
	}

	@Test
	public void testSearch() {
		connect();

		click(jq("@searchbox"));
		waitResponse(true);
		int count = jq(".z-searchbox-item:visible").length();

		getActions().sendKeys("9").perform();
		waitResponse();
		Assertions.assertNotEquals(0, jq(".z-searchbox-item:visible").length());

		getActions().sendKeys("99").perform();
		waitResponse();
		Assertions.assertEquals(0, jq(".z-searchbox-item:visible").length());

		getActions().sendKeys(Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.BACK_SPACE).perform();
		waitResponse();
		Assertions.assertEquals(count, jq(".z-searchbox-item:visible").length());
	}

	@Test
	public void testKeyboardNavigation() {
		connect();

		click(jq("@button:contains(multiple)"));
		waitResponse();
		click(jq("@button:contains(autoclose)"));
		waitResponse();

		click(jq("@textbox"));
		waitResponse();
		sendKeys(jq("@textbox"), Keys.TAB);
		waitResponse();
		getActions().sendKeys(Keys.DOWN).perform();
		waitResponse(true);
		Assertions.assertTrue(jq(widget("@searchbox").$n("pp")).isVisible());

		getActions().sendKeys(Keys.DOWN, Keys.DOWN).perform();
		assertThat(jq(".z-searchbox-active").text().trim(), startsWith("Item 2"));
		getActions().sendKeys(Keys.END).perform();
		assertThat(jq(".z-searchbox-active").text().trim(), startsWith("Item 100"));
		getActions().sendKeys(Keys.UP).perform();
		assertThat(jq(".z-searchbox-active").text().trim(), startsWith("Item 99"));
		getActions().sendKeys(Keys.HOME).perform();
		assertThat(jq(".z-searchbox-active").text().trim(), startsWith("Item 1"));

		getActions().sendKeys(Keys.ENTER).perform();
		waitResponse();
		Assertions.assertTrue(jq(".z-searchbox-item:eq(0)").hasClass("z-searchbox-selected"));
		getActions().sendKeys(Keys.ENTER).perform();
		waitResponse();
		Assertions.assertFalse(jq(".z-searchbox-item:eq(0)").hasClass("z-searchbox-selected"));
		getActions().sendKeys(Keys.ENTER)
				.keyDown(Keys.SHIFT)
				.sendKeys(Keys.DOWN, Keys.DOWN, Keys.DOWN, Keys.DOWN, Keys.ENTER)
				.keyUp(Keys.SHIFT)
				.perform();
		waitResponse();
		Assertions.assertEquals(6, jq(".z-searchbox-selected").length());

		getActions().sendKeys(Keys.UP, Keys.ENTER)
				.keyDown(Keys.SHIFT)
				.sendKeys(Keys.UP, Keys.UP, Keys.ENTER)
				.keyUp(Keys.SHIFT)
				.perform();
		waitResponse();
		Assertions.assertEquals(3, jq(".z-searchbox-selected").length());

		getActions().sendKeys(Keys.ESCAPE).perform();
		waitResponse();
		Assertions.assertFalse(jq(widget("@searchbox").$n("pp")).isVisible());
	}

	@Test
	public void testDisabled() {
		connect();

		click(jq("@button:contains(disabled)"));
		waitResponse();

		click(jq("@searchbox"));
		waitResponse(true);
		Assertions.assertFalse(jq(widget("@searchbox").$n("pp")).isVisible());

		click(jq("@button:contains(disabled)"));
		waitResponse();

		click(jq("@searchbox"));
		waitResponse(true);
		Assertions.assertTrue(jq(widget("@searchbox").$n("pp")).isVisible());
	}

	@Test
	public void testOpen() {
		connect();

		click(jq("@button:contains(open)"));
		waitResponse(true);
		Assertions.assertTrue(jq(widget("@searchbox").$n("pp")).isVisible());

		click(jq("@searchbox"));
		waitResponse();
		Assertions.assertFalse(jq(widget("@searchbox").$n("pp")).isVisible());

		click(jq("@button:contains(disabled)"));
		waitResponse();
		click(jq("@button:contains(open)"));
		waitResponse(true);
		Assertions.assertTrue(jq(widget("@searchbox").$n("pp")).isVisible());
	}

	@Test
	public void testAutoclose() {
		connect();

		click(jq("@button:contains(autoclose)"));
		waitResponse();
		click(jq("@searchbox"));
		waitResponse(true);
		click(jq(".z-searchbox-item:eq(0)"));
		waitResponse();
		Assertions.assertTrue(jq(widget("@searchbox").$n("pp")).isVisible());

		click(jq("@searchbox"));
		waitResponse();
		click(jq("@button:contains(autoclose)"));
		waitResponse();

		click(jq("@searchbox"));
		waitResponse(true);
		click(jq(".z-searchbox-item:eq(0)"));
		waitResponse();
		Assertions.assertFalse(jq(widget("@searchbox").$n("pp")).isVisible());
	}
}
