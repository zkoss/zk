/* B86_ZK_3197Test.java

		Purpose:
		
		Description:
		
		History:
				Mon Jun 17 15:33:13 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B86_ZK_3197Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@listitem"));
		waitResponse();
		jq(".z-listbox-body").scrollTop(1000000);
		waitResponse();
		sleep(1000);
		Assert.assertEquals(1000000, jq(".z-listbox-body").scrollTop());
	}
}
