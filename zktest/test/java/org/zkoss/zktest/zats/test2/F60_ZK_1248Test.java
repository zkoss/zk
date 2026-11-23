/* F60_ZK_1248Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Apr 15 18:01:30 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.Matchers.lessThanOrEqualTo;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F60_ZK_1248Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		Dimension size = driver.manage().window().getSize();
		driver.manage().window().setSize(new Dimension(500, size.height));
		waitResponse();

		Actions actions = getActions();
		actions.moveToElement(toElement(jq("@window")))
				.moveByOffset(240, 0)
				.contextClick()
				.perform();
		waitResponse();
		actions.moveToElement(toElement(jq("@menu:first")))
				.pause(1000)
				.perform();
		waitResponse();
		Assert.assertThat(
				jq("@menupopup:visible:last").offsetLeft() + jq("@menupopup:visible:last").width(),
				lessThanOrEqualTo(jq("@menupopup:visible:first").offsetLeft())
		);
	}
}
