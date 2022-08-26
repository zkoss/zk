/* B86_ZK_4145Test.java

        Purpose:
                
        Description:
                
        History:
                Fri Dec 28 16:27:11 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B86_ZK_4145Test extends WebDriverTestCase {
	private static final String MENU = ".z-menu";

	@Test
	public void test() {
		connect();
		driver.manage().window().setSize(new Dimension(360, 600));

		click(jq(".z-button:eq(0)"));
		waitResponse();

		click(jq(MENU));
		waitResponse();

		JQuery menupopupOne = getMenupopup(0);
		click(menupopupOne.find(MENU));
		waitResponse();

		JQuery menupopupTwo = getMenupopup(1);
		int popupTwoLeft = menupopupTwo.offsetLeft();
		Assertions.assertTrue(popupTwoLeft > menupopupOne.offsetLeft());

		click(menupopupTwo.find(MENU));
		waitResponse();
		Assertions.assertTrue(popupTwoLeft > getMenupopup(2).offsetLeft());

		click(jq(".z-button:eq(1)"));
		waitResponse();

		menupopupOne = getMenupopup(0);
		click(menupopupOne.find(MENU));
		waitResponse();

		menupopupTwo = getMenupopup(1);
		popupTwoLeft = menupopupTwo.offsetLeft();
		Assertions.assertTrue(popupTwoLeft < menupopupOne.offsetLeft());

		click(menupopupTwo.find(MENU));
		waitResponse();
		Assertions.assertTrue(popupTwoLeft < getMenupopup(2).offsetLeft());
	}

	private JQuery getMenupopup(int index) {
		return jq(".z-menupopup-open").eq(index);
	}
}
