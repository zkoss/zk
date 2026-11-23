/* B95_ZK_4592Test.java

		Purpose:
		
		Description:
		
		History:
				Tue Oct 13 11:24:43 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B95_ZK_4592Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		// detach the caochmark
		click(jq("$btn"));
		waitResponse();

		click(jq("$btn2"));
		waitResponse();
		Assert.assertTrue("btn2 should be clickable after the caochmark is detached", isZKLogAvailable());
	}
}
