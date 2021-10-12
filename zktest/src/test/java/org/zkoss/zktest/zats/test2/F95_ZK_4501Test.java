/* F95_ZK_4501Test.java

		Purpose:
		
		Description:
		
		History:
				Tue Feb 18 12:03:17 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

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
		Assert.assertEquals(initName, jq("@textbox").val());
		Assert.assertEquals(initAge, jq("@intbox").val());
		Assert.assertEquals(initName, jq("$fn").text());
		Assert.assertEquals(initAge, jq("$fa").text());
		
		type(jq("@textbox"), editName);
		waitResponse();
		type(jq("@intbox"), editAge);
		waitResponse();
		click(jq("@label"));
		waitResponse();
		Assert.assertEquals(editName, jq("@textbox").val());
		Assert.assertEquals(editAge, jq("@intbox").val());
		Assert.assertEquals(editName, jq("$fn").text());
		Assert.assertEquals(editAge, jq("$fa").text());
		
		click(jq("@button:contains(save)"));
		waitResponse();
		Assert.assertEquals(editName, jq("$on").text());
		Assert.assertEquals(editAge, jq("$oa").text());
		
		click(jq("@button:contains(set data in form)"));
		waitResponse();
		Assert.assertEquals(setName, jq("@textbox").val());
		Assert.assertEquals(setAge, jq("@intbox").val());
		Assert.assertEquals(setName, jq("$fn").text());
		Assert.assertEquals(setAge, jq("$fa").text());
		
		click(jq("@button:contains(save)"));
		waitResponse();
		Assert.assertEquals(setName, jq("$on").text());
		Assert.assertEquals(setAge, jq("$oa").text());
	}
}
