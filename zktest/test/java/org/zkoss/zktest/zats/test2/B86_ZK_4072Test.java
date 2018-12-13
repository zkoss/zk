/* B86_ZK_4072Test.java

		Purpose:
		
		Description:
		
		History:
				Tue Dec 11 15:22:18 CST 2018, Created by leon

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B86_ZK_4072Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("$breeze"));
		waitResponse();
		check();
		click(jq("$sapphire"));
		waitResponse();
		check();
		click(jq("$silvertail"));
		waitResponse();
		check();
		click(jq("$atlantic"));
		waitResponse();
		check();
		click(jq("$default"));
		waitResponse();
		check();
	}
	
	private void check() {
		connect();
		for (int i = 0; i < 4; i++) {
			click(jq("$add"));
			waitResponse();
		}
		click(jq("$test"));
		waitResponse();
		Assert.assertEquals("true", getZKLog());
	}
}
