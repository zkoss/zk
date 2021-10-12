/* B86_ZK_4156Test.java

		Purpose:
		
		Description:
		
		History:
				Wed Jun 19 12:03:17 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B86_ZK_4156Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		int scrollHeight = jq("@listbox > .z-listbox-body").scrollHeight();
		jq("@listbox > .z-listbox-body").scrollTop(scrollHeight / 2);
		waitResponse();
		Assert.assertFalse(isZKLogAvailable());
	}
}
