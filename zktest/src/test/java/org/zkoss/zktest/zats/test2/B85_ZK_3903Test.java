/* B85_ZK_3903Test.java

        Purpose:
                
        Description:
                
        History:
                Wed Mar 28 2:20 PM:28 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B85_ZK_3903Test extends WebDriverTestCase {
	
	@Test
	public void test() {
		connect();
		
		click(jq("button:eq(0)"));
		waitResponse();
		jq(jq(".z-caption-content:eq(0)").toElement().firstChild()).hasClass("z-icon-globe");
		Assertions.assertTrue(jq(jq(".z-caption-content:eq(0)").toElement().firstChild()).hasClass("z-icon-globe"));
		Assertions.assertEquals("Testlabel1", jq(".z-caption-label:eq(0)").text());
		
		click(jq("button:eq(1)"));
		waitResponse();
		jq(jq(".z-caption-content:eq(1)").toElement().firstChild()).hasClass("z-icon-globe");
		Assertions.assertTrue(jq(jq(".z-caption-content:eq(1)").toElement().firstChild()).hasClass("z-icon-globe"));
		Assertions.assertEquals("asdf", jq(".z-caption-label:eq(1)").text());
		
		
		click(jq("button:eq(3)"));
		waitResponse();
		jq(jq(".z-caption-content:eq(2)").toElement().firstChild()).hasClass("z-icon-globe");
		Assertions.assertTrue(jq(jq(".z-caption-content:eq(2)").toElement().firstChild()).hasClass("z-icon-globe"));
		Assertions.assertEquals("Testlabel3", jq(".z-caption-label:eq(2)").text());
	}
}
