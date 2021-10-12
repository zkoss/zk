/* B86_ZK_4003Test.java

		Purpose:
		
		Description:
		
		History:
				Tue Nov 20 15:47:10 CST 2018, Created by leon

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B86_ZK_4003Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		
		click(jq("$loadButton"));
		waitResponse();
		
		click(jq(".z-combobox-button").eq(0));
		waitResponse();
		
		click(jq(".z-comboitem").eq(0));
		waitResponse();
		
		click(jq(".z-combobox-button").eq(1));
		waitResponse();
		
		Assert.assertEquals("1", jq(".z-comboitem").eq(2).text());
	}
}
