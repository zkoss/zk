/* B50_ZK_430Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Jun 12 15:00:21 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.startsWith;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.Widget;

/**
 * @author rudyhuang
 */
public class B50_ZK_430Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		Widget tb1 = widget("$tb1");
		click(tb1);
		waitResponse();
		selectAll();
		sendKeys(tb1.$n("real"), "1212\t");
		Assert.assertEquals("1212", tb1.$n("real").get("value"));

		Widget tb2 = widget("$tb2");
		click(tb2);
		waitResponse();
		selectAll();
		sendKeys(tb2.$n("real"), "121212\t");
		Assert.assertThat(tb2.$n("real").get("value"), endsWith("M 12:12:12"));

		Widget tb3 = widget("$tb3");
		click(tb3);
		waitResponse();

		String apm = tb3.$n("real").get("value").replace("00:00:00", ""); // AM or PM
		setCursorPosition(tb3.$n("real"), 4);

		sendKeys(tb3.$n("real"), Keys.DOWN);
		Assert.assertThat(tb3.$n("real").get("value"), startsWith(apm));
		sendKeys(tb3.$n("real"), Keys.UP, Keys.UP);
		Assert.assertThat(tb3.$n("real").get("value"), startsWith(apm));
	}
}
