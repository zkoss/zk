/* B50_ZK_507Test.java

		Purpose:
		
		Description:
		
		History:
				Tue Apr 09 16:56:53 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B50_ZK_507Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq(".z-column"));
		waitResponse();
		click(jq(".z-paging-next"));
		waitResponse();
		Assert.assertEquals("name05", jq(".z-row").eq(0).text());
	}
}
