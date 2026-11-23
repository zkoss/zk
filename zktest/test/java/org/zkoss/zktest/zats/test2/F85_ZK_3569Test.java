/* F85_ZK_3569Test.java

		Purpose:
                
		Description:
                
		History:
				Fri Mar 29 10:25:21 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class F85_ZK_3569Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		verify(jq(".z-combobox-button"), jq(".z-combobox-popup.z-combobox-open"));
		verify(jq(".z-bandbox-button"), jq(".z-bandbox-popup.z-bandbox-open"));
	}

	private void verify(JQuery buttons, JQuery openedPopup) {
		JQuery topButton = buttons.eq(0);
		JQuery bottomButton = buttons.eq(1);

		click(topButton);
		waitResponse();
		Assert.assertTrue(getLocationY(topButton) < getLocationY(openedPopup));

		click(bottomButton);
		waitResponse();
		Assert.assertTrue(getLocationY(bottomButton) > getLocationY(openedPopup));
	}

	private int getLocationY(JQuery widget) {
		return toElement(widget).getLocation().getY();
	}
}
