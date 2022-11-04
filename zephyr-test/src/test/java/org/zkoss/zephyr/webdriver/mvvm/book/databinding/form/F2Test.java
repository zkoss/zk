/** F2Test.java.

	Purpose:
		
	Description:
		
	History:
		2:16:25 PM Dec 31, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.databinding.form;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jumperchen
 */
public class F2Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery quantityABox = jq("$quantityABox");
		JQuery subtotalA = jq("$subtotalA");
		JQuery quantityBBox = jq("$quantityBBox");
		JQuery subtotalB = jq("$subtotalB");
		JQuery computeButton = jq("$computeButton");
		JQuery total = jq("$total");
		JQuery offBox = jq("$offBox");

		type(quantityABox, "5");
		waitResponse();
		type(quantityBBox, "5");
		waitResponse();
		click(computeButton);
		waitResponse();

		assertEquals("50", subtotalA.text());
		assertEquals("100", subtotalB.text());
		assertEquals("150", total.text());

		type(quantityABox, "11");
		waitResponse();
		click(computeButton);
		waitResponse();
		assertEquals("50", subtotalA.text());
		assertEquals("100", subtotalB.text());
		assertEquals("150", total.text());

		type(quantityABox, "5");
		waitResponse();
		click(jq(".z-combobox-button"));
		waitResponse();
		click(offBox.toWidget().lastChild());
		waitResponse();
		click(computeButton);
		waitResponse();
		assertEquals("50", subtotalA.text());
		assertEquals("100", subtotalB.text());
		assertEquals("75", total.text());
	}
}
