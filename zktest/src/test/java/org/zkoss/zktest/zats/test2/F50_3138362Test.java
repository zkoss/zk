/* F50_3138362Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 09 17:13:48 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class F50_3138362Test extends WebDriverTestCase {
	@Test
	public void testAppearance() {
		connect();

		click(jq("@button:eq(0)"));
		waitResponse();
		Assertions.assertTrue(jq(widget("@doublespinner:eq(0)").$n("btn")).isVisible());
		Assertions.assertTrue(jq(widget("@doublespinner:eq(1)").$n("btn")).isVisible());

		click(jq("@button:eq(1)"));
		waitResponse();
		Assertions.assertTrue(jq("@doublespinner:eq(0) input").is("[readonly]"));
		Assertions.assertTrue(jq("@doublespinner:eq(1) input").is("[readonly]"));

		click(jq("@button:eq(2)"));
		waitResponse();
		Assertions.assertTrue(jq("@doublespinner:eq(0) input").is("[disabled]"));
		Assertions.assertTrue(jq("@doublespinner:eq(1) input").is("[disabled]"));

		click(jq("@button:eq(3)"));
		waitResponse();
		Assertions.assertTrue(jq("@doublespinner:eq(0)").hasClass("z-doublespinner-inplace"));
		Assertions.assertTrue(jq("@doublespinner:eq(1)").hasClass("z-doublespinner-inplace"));
	}

	@Test
	public void testIncreaseDecrease() {
		connect();

		JQuery target = jq("@doublespinner:last");
		double currentVal = Double.parseDouble(target.find("input").val());

		click(widget(target).$n("btn-up"));
		waitResponse();
		Assertions.assertEquals(currentVal + 0.5, Double.parseDouble(target.find("input").val()), 0.1);

		click(widget(target).$n("btn-down"));
		click(widget(target).$n("btn-down"));
		waitResponse();
		Assertions.assertEquals(currentVal - 0.5, Double.parseDouble(target.find("input").val()), 0.1);

		for (int i = 0; i < 12; i++)
			click(widget(target).$n("btn-down"));
		waitResponse();
		Assertions.assertEquals(-2.5, Double.parseDouble(target.find("input").val()), 0.1);

		for (int i = 0; i < 12; i++)
			click(widget(target).$n("btn-up"));
		waitResponse();
		Assertions.assertEquals(2.5, Double.parseDouble(target.find("input").val()), 0.1);
	}
}
