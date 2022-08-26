/* B85_ZK_3918Test.java

		Purpose:
		
		Description:
		
		History:
				Thu Jun 13 10:16:59 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B85_ZK_3918Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		testTypeAndPaste();
		click(jq(".z-colorpalette-color").eq(100));
		waitResponse();
		testTypeAndPaste();
	}
	
	private void testTypeAndPaste() {
		click(jq("@colorbox"));
		waitResponse();
		click(jq(".z-colorpalette-input"));
		waitResponse();
		selectAll();
		type(jq(".z-colorpalette-input"), "@GHIJKL");
		waitResponse();
		Assertions.assertEquals("", jq(".z-colorpalette-input").val());
		
		click(jq(".z-colorpalette-input"));
		selectAll();
		type(jq(".z-colorpalette-input"), "#123ABC");
		waitResponse();
		Assertions.assertEquals("#123ABC", jq(".z-colorpalette-input").val());
		
		click(jq(".z-colorpalette-input"));
		selectAll();
		cut();
		waitResponse();
		Assertions.assertEquals("", jq(".z-colorpalette-input").val());
		paste();
		Assertions.assertEquals("#123ABC", jq(".z-colorpalette-input").val());
	}
}
