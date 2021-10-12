/* B50_ZK_509Test.java

		Purpose:
		
		Description:
		
		History:
				Fri Apr 19 16:10:06 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B50_ZK_509Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		jq(".z-listbox-body").scrollTop(10000);
		waitResponse();
		Assert.assertTrue(jq("@listcell:contains(199)").exists());
	}
}
