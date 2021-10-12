/* B70_ZK_2250Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Mar 27 15:10:07 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B70_ZK_2250Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		Actions actions = new Actions(driver);
		checkItem(actions, 0);
		checkItem(actions, 1);
		checkItem(actions, 2);
	}

	private void checkItem(Actions actions, int i) {
		actions.moveToElement(toElement(jq("@grid @label").eq(i)))
				.clickAndHold()
				.moveToElement(toElement(jq("@grid ~ @div")))
				.perform();
		try {
			Assert.assertTrue(jq("#zk_ddghost").hasClass("z-drop-allow"));
		} finally {
			actions.release().perform();
		}
	}
}
