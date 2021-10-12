/* B50_3218078Test.java

		Purpose:
                
		Description:
                
		History:
				Fri Mar 22 10:32:45 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

public class B50_3218078Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		jq(".z-button").forEach(button -> {
			click(button);
			waitResponse();
		});
		Assert.assertEquals("true", getZKLog());
	}
}
