package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class F85_ZK_3704Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		String testInput = "123456.789";
		JQuery decimalboxOne = jq(".z-decimalbox:eq(0)");
		JQuery decimalboxTwo = jq(".z-decimalbox:eq(1)");

		sendKeys(decimalboxOne, testInput);
		waitResponse();
		blur(decimalboxOne);
		waitResponse();
		assertEquals("1,23,456.789", decimalboxOne.val());

		sendKeys(decimalboxTwo, testInput);
		waitResponse();
		blur(decimalboxTwo);
		waitResponse();
		assertEquals("123 456,789", decimalboxTwo.val());

		click(jq(".z-button"));
		waitResponse();
		assertEquals("123.456,789", decimalboxOne.val());
	}
}
