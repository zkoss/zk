/* B85_ZK_3841Test.java

		Purpose:
		
		Description:
		
		History:
				Wed Jun 12 14:36:47 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B85_ZK_3841Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		int listitemHeight = jq("@listitem").height();
		click(jq("@button:first"));
		waitResponse();
		click(jq("@button:last"));
		waitResponse();
		Assert.assertEquals(listitemHeight, jq("@listitem").height());
	}
}
