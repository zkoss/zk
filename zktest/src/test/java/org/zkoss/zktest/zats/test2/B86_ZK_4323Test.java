/* B86_ZK_4323Test.java

		Purpose:
		
		Description:
		
		History:
				Thu Aug 29 16:14:32 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B86_ZK_4323Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		Assert.assertEquals(jq("@listbox .z-listbox-body").eq(0).scrollTop(), jq("@listbox .z-listbox-body").eq(1).scrollTop());
		Assert.assertEquals(jq(".z-scrollbar-vertical-embed").eq(0).positionTop(), jq(".z-scrollbar-vertical-embed").eq(1).positionTop());
	}
}
