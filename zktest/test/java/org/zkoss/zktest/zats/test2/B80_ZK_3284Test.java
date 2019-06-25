/* B80_ZK_3284Test.java

		Purpose:
                
		Description:
                
		History:
				Mon Mar 25 15:40:02 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

public class B80_ZK_3284Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		click(jq(".z-bandbox-button"));
		waitResponse();
		Assert.assertEquals("bandbox focused", jq("$lb").text().trim());
		click(jq(".z-listitem"));
		waitResponse();
		Assert.assertEquals("bandbox focused", jq("$lb").text().trim());
	}
}
