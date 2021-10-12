/* B50_3285148Test.java

		Purpose:
                
		Description:
                
		History:
				Fri Mar 22 11:03:38 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

public class B50_3285148Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		click(jq(".z-button"));
		waitResponse();
		Assert.assertEquals("false", getZKLog());
	}
}
