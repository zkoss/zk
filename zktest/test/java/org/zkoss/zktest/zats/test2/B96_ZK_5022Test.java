/* B96_ZK_5022Test.java

	Purpose:
		
	Description:
		
	History:
		5:26 PM 2021/11/11, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B96_ZK_5022Test  extends WebDriverTestCase {
	@Test
	public void test() {
		Actions act = new Actions(connect());

		click(jq("$main").toWidget().lastChild());
		waitResponse();
		click(jq("$btn"));
		waitResponse();
		act.keyDown(Keys.SHIFT).click(toElement(jq("$main").toWidget().firstChild())).perform();
		waitResponse();
		assertFalse(hasError());
		assertTrue(jq("$main").toWidget().firstChild().is("selected"));

	}
}
