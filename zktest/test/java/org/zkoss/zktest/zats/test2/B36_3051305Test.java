/* B36_3051305Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Mar 15 12:25:07 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.Matchers.endsWith;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B36_3051305Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		Actions actions = new Actions(driver);
		actions.dragAndDropBy(driver.findElement(jq("@listitem:contains(ZK JSP)")), 0, 50)
				.perform();
		waitResponse();
		Assert.assertEquals(5, jq("@listitem").length());
		Assert.assertThat(jq("@listitem:last").text(), endsWith("ZK Spring"));

		actions.dragAndDropBy(driver.findElement(jq("@row:contains(ZK JSP)")), 0, 50)
				.perform();
		waitResponse();
		Assert.assertEquals(5, jq("@row").length());
		Assert.assertThat(jq("@row:last").text(), endsWith("ZK Spring"));

		actions.dragAndDropBy(driver.findElement(jq("@treerow:contains(ZK JSP)")), 0, 50)
				.perform();
		waitResponse();
		Assert.assertEquals(5, jq("@treerow").length());
		Assert.assertThat(jq("@treerow:last").text(), endsWith("ZK Spring"));

		click(jq(".z-paging-next:eq(0)"));
		waitResponse();
		Assert.assertThat(jq("@listitem:last").text(), endsWith("ZK JSP"));
		click(jq(".z-paging-next:eq(1)"));
		waitResponse();
		Assert.assertThat(jq("@row:last").text(), endsWith("ZK JSP"));
		click(jq(".z-paging-next:eq(2)"));
		waitResponse();
		Assert.assertThat(jq("@treerow:last").text(), endsWith("ZK JSP"));
	}
}
