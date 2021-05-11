/* ComponentAnnotationTest.java

		Purpose:
		
		Description:
		
		History:
				Fri May 07 16:47:04 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.advance;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class ComponentAnnotationTest extends WebDriverTestCase {
	@Test
	public void testWithoutComponentAnnotation() {
		connect();

		JQuery custom0 = jq("$custom0");
		JQuery custom1 = jq("$custom1");
		JQuery custom2 = jq("$custom2");
		JQuery custom3 = jq("$custom3");

		JQuery label =  jq("$l1");

		// test without ComponentAnnotation
		String expected = "init";
		click(custom0);
		waitResponse();
		sendKeys(custom0.find("input"), "red");
		waitResponse();
		click(jq(".z-label")); // blur
		waitResponse();

		Assert.assertEquals(expected, label.text());
		Assert.assertEquals("red", custom0.text());
		Assert.assertEquals(expected, custom1.text());
		Assert.assertEquals(expected, custom2.text());
		Assert.assertEquals(expected, custom3.text());

		// test ComponentAnnotation on class
		expected = "blue";
		click(custom1);
		waitResponse();
		sendKeys(custom1.find("input"), "blue");
		waitResponse();
		click(jq(".z-label")); // blur
		waitResponse();

		Assert.assertEquals(expected, label.text());
		Assert.assertEquals(expected, custom0.text());
		Assert.assertEquals(expected, custom1.text());
		Assert.assertEquals(expected, custom2.text());
		Assert.assertEquals(expected, custom3.text());

		// test ComponentAnnotation on getter
		expected = "green";
		click(custom2);
		waitResponse();
		sendKeys(custom2.find("input"), "green");
		waitResponse();
		click(jq(".z-label")); // blur
		waitResponse();

		Assert.assertEquals(expected, label.text());
		Assert.assertEquals(expected, custom0.text());
		Assert.assertEquals(expected, custom1.text());
		Assert.assertEquals(expected, custom2.text());
		Assert.assertEquals(expected, custom3.text());

		// test ComponentAnnotation on setter
		expected = "purple";
		click(custom3);
		waitResponse();
		sendKeys(custom3.find("input"), "purple");
		waitResponse();
		click(jq(".z-label")); // blur
		waitResponse();

		Assert.assertEquals(expected, label.text());
		Assert.assertEquals(expected, custom0.text());
		Assert.assertEquals(expected, custom1.text());
		Assert.assertEquals(expected, custom2.text());
		Assert.assertEquals(expected, custom3.text());
	}
}
