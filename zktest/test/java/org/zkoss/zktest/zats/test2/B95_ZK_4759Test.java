/* B95_ZK_4759Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Jan 8 14:10:21 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;

import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B95_ZK_4759Test extends WebDriverTestCase {
	@Test
	public void test() throws Exception {
		connect();
		waitResponse();
		WebDriver.Window window = driver.manage().window();
		int originalWindowHeight = window.getSize().height;
		int originalWindowWidth = window.getSize().width;
		int wd1 = jq("$lh1").width();
		int wd2 = jq("$lh2").width();
		int wd3 = jq("$lh3").width();
		window.setSize(new Dimension(originalWindowWidth / 2, originalWindowHeight));
		waitResponse();
		int totalWd = jq("$block3").width();
		assertFalse(isZKLogAvailable());
		int restHalfWd = (totalWd - wd3) / 2;
		int newWd1 = jq("$lh1").width();
		int newWd2 = jq("$lh2").width();
		MatcherAssert.assertThat(newWd1, lessThan(wd1));
		MatcherAssert.assertThat(newWd2, lessThan(wd2));
		assertEquals(restHalfWd, jq("$lh1").width(), 2);
		assertEquals(restHalfWd, jq("$lh2").width(), 2);
		assertEquals(wd3, jq("$lh3").width());

		int hgh1 = jq("$block1").outerHeight();
		int hgh2 = jq("$block2").outerHeight();
		int hgh3 = jq("$block3").outerHeight();
		window.setSize(new Dimension(originalWindowWidth, originalWindowHeight / 2));
		waitResponse();
		int totalHgh = jq("@vlayout").height();
		assertFalse(isZKLogAvailable());
		int restHalfHgh = (totalHgh - hgh2) / 2;
		int newHgh1 = jq("$block1").outerHeight();
		int newHgh3 = jq("$block3").outerHeight();
		MatcherAssert.assertThat(newHgh1, lessThan(hgh1));
		MatcherAssert.assertThat(newHgh3, lessThan(hgh3));
		assertEquals(restHalfHgh, newHgh1, 5); // consider vlayout padding
		assertEquals(hgh2, jq("$block2").outerHeight());
		assertEquals(restHalfHgh, newHgh3, 5); // consider vlayout padding
	}
}
