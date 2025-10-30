/* F95_ZK_4501Test.java

		Purpose:
		
		Description:
		
		History:
				Tue Feb 18 12:03:17 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class F95_ZK_4501Test extends WebDriverTestCase {
	@Test
	public void test() {
		String initName = "guest";
		String initAge = "20";
		String editName = "Jack";
		String editAge = "28";
		String setName = "Peter";
		String setAge = "12";
		
		connect();
		waitResponse();
		Assertions.assertEquals(initName, jq("@textbox").val());
		Assertions.assertEquals(initAge, jq("@intbox").val());
		Assertions.assertEquals(initName, jq("$fn").text());
		Assertions.assertEquals(initAge, jq("$fa").text());
		
		type(jq("@textbox"), editName);
		waitResponse();
		type(jq("@intbox"), editAge);
		waitResponse();
		click(jq("@label"));
		waitResponse();
		Assertions.assertEquals(editName, jq("@textbox").val());
		Assertions.assertEquals(editAge, jq("@intbox").val());
		Assertions.assertEquals(editName, jq("$fn").text());
		Assertions.assertEquals(editAge, jq("$fa").text());
		
		click(jq("@button:contains(save)"));
		waitResponse();
		Assertions.assertEquals(editName, jq("$on").text());
		Assertions.assertEquals(editAge, jq("$oa").text());
		
		click(jq("@button:contains(set data in form)"));
		waitResponse();
		Assertions.assertEquals(setName, jq("@textbox").val());
		Assertions.assertEquals(setAge, jq("@intbox").val());
		Assertions.assertEquals(setName, jq("$fn").text());
		Assertions.assertEquals(setAge, jq("$fa").text());
		
		click(jq("@button:contains(save)"));
		waitResponse();
		Assertions.assertEquals(setName, jq("$on").text());
		Assertions.assertEquals(setAge, jq("$oa").text());
	}
}
