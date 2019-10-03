/* F90_ZK_4380Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Sep 20 15:40:44 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.Matchers.startsWith;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;

import org.zkoss.zktest.zats.WebDriverTestCase;

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
		Assert.assertEquals("Selected 1 items: [Item 1]", jq(widget("@searchbox").$n("label")).text());

		click(jq("@searchbox"));
		waitResponse(true);
		Assert.assertTrue(jq(".z-searchbox-item:eq(0)").hasClass("z-searchbox-selected"));
		click(jq(".z-searchbox-item:eq(0)"));
		waitResponse();
		Assert.assertEquals("Selected 0 items: []", jq(widget("@searchbox").$n("label")).text());
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
		Assert.assertTrue(jq(".z-searchbox-item:eq(0)").hasClass("z-searchbox-selected"));
		Assert.assertTrue(jq(".z-searchbox-item:eq(1)").hasClass("z-searchbox-selected"));
		Assert.assertTrue(jq(".z-searchbox-item:eq(2)").hasClass("z-searchbox-selected"));
		Assert.assertTrue(jq(".z-searchbox-item:eq(3)").hasClass("z-searchbox-selected"));
		click(jq("@searchbox"));
		waitResponse();

		click(jq("@searchbox"));
		waitResponse(true);
		click(jq(".z-searchbox-item:eq(0)"));
		Assert.assertFalse(jq(".z-searchbox-item:eq(0)").hasClass("z-searchbox-selected"));
	}

	@Test
	public void testSearch() {
		connect();

		click(jq("@searchbox"));
		waitResponse(true);
		int count = jq(".z-searchbox-item:visible").length();

		getActions().sendKeys("9").perform();
		waitResponse();
		Assert.assertNotEquals(0, jq(".z-searchbox-item:visible").length());

		getActions().sendKeys("99").perform();
		waitResponse();
		Assert.assertEquals(0, jq(".z-searchbox-item:visible").length());

		getActions().sendKeys(Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.BACK_SPACE).perform();
		waitResponse();
		Assert.assertEquals(count, jq(".z-searchbox-item:visible").length());
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
		Assert.assertTrue(jq(widget("@searchbox").$n("pp")).isVisible());

		getActions().sendKeys(Keys.DOWN, Keys.DOWN).perform();
		Assert.assertThat(jq(".z-searchbox-active").text().trim(), startsWith("Item 2"));
		getActions().sendKeys(Keys.END).perform();
		Assert.assertThat(jq(".z-searchbox-active").text().trim(), startsWith("Item 100"));
		getActions().sendKeys(Keys.UP).perform();
		Assert.assertThat(jq(".z-searchbox-active").text().trim(), startsWith("Item 99"));
		getActions().sendKeys(Keys.HOME).perform();
		Assert.assertThat(jq(".z-searchbox-active").text().trim(), startsWith("Item 1"));

		getActions().sendKeys(Keys.ENTER).perform();
		waitResponse();
		Assert.assertTrue(jq(".z-searchbox-item:eq(0)").hasClass("z-searchbox-selected"));
		getActions().sendKeys(Keys.ENTER).perform();
		waitResponse();
		Assert.assertFalse(jq(".z-searchbox-item:eq(0)").hasClass("z-searchbox-selected"));
		getActions().sendKeys(Keys.ENTER)
				.keyDown(Keys.SHIFT)
				.sendKeys(Keys.DOWN, Keys.DOWN, Keys.DOWN, Keys.DOWN, Keys.ENTER)
				.keyUp(Keys.SHIFT)
				.perform();
		waitResponse();
		Assert.assertEquals(6, jq(".z-searchbox-selected").length());

		getActions().sendKeys(Keys.UP, Keys.ENTER)
				.keyDown(Keys.SHIFT)
				.sendKeys(Keys.UP, Keys.UP, Keys.ENTER)
				.keyUp(Keys.SHIFT)
				.perform();
		waitResponse();
		Assert.assertEquals(3, jq(".z-searchbox-selected").length());

		getActions().sendKeys(Keys.ESCAPE).perform();
		waitResponse();
		Assert.assertFalse(jq(widget("@searchbox").$n("pp")).isVisible());
	}

	@Test
	public void testDisabled() {
		connect();

		click(jq("@button:contains(disabled)"));
		waitResponse();

		click(jq("@searchbox"));
		waitResponse(true);
		Assert.assertFalse(jq(widget("@searchbox").$n("pp")).isVisible());

		click(jq("@button:contains(disabled)"));
		waitResponse();

		click(jq("@searchbox"));
		waitResponse(true);
		Assert.assertTrue(jq(widget("@searchbox").$n("pp")).isVisible());
	}

	@Test
	public void testOpen() {
		connect();

		click(jq("@button:contains(open)"));
		waitResponse(true);
		Assert.assertTrue(jq(widget("@searchbox").$n("pp")).isVisible());

		click(jq("@searchbox"));
		waitResponse();
		Assert.assertFalse(jq(widget("@searchbox").$n("pp")).isVisible());

		click(jq("@button:contains(disabled)"));
		waitResponse();
		click(jq("@button:contains(open)"));
		waitResponse(true);
		Assert.assertTrue(jq(widget("@searchbox").$n("pp")).isVisible());
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
		Assert.assertTrue(jq(widget("@searchbox").$n("pp")).isVisible());

		click(jq("@searchbox"));
		waitResponse();
		click(jq("@button:contains(autoclose)"));
		waitResponse();

		click(jq("@searchbox"));
		waitResponse(true);
		click(jq(".z-searchbox-item:eq(0)"));
		waitResponse();
		Assert.assertFalse(jq(widget("@searchbox").$n("pp")).isVisible());
	}
}
