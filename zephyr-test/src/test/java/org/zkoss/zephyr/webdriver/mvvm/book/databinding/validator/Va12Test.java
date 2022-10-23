/* Va12Test.java

		Purpose:
		
		Description:
		
		History:
				Tue May 11 11:03:59 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.databinding.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.TestStage;
import org.zkoss.zephyr.webdriver.ZephyrClientMVVMTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class Va12Test extends ZephyrClientMVVMTestCase {
	@Test
	public void test() {
		connect();

		JQuery quantityABoxAgent = jq("$quantityABox");
		JQuery quantityBBoxAgent = jq("$quantityBBox");
		JQuery offBoxAgent = jq("$offBox input");
		JQuery computeButton = jq("$computeButton");
		JQuery total = jq("$total");
		JQuery subtotalA = jq("$subtotalA");
		JQuery subtotalB = jq("$subtotalB");

		click(computeButton);
		waitResponse();
		assertEquals("30", total.text());

		type(quantityABoxAgent, "10");
		waitResponse();
		assertEquals("100", subtotalA.text());

		type(quantityBBoxAgent, "11");
		waitResponse();
		assertEquals("20", subtotalB.text());

		type(quantityBBoxAgent, "10");
		waitResponse();
		assertEquals("200", subtotalB.text());

		click(computeButton);
		waitResponse();
		assertEquals("300", total.text());

		type(offBoxAgent, "90");
		waitResponse();
		click(computeButton);
		waitResponse();
		assertEquals("270", total.text());
	}
}
