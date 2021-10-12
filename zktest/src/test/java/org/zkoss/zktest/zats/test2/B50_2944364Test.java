/* B50_2944364Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Mar 20 17:00:10 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B50_2944364Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		Assert.assertFalse(jq("@ckeditor").isVisible());

		click(jq("@button"));
		waitResponse();
		Assert.assertTrue(jq("@ckeditor").isVisible());
	}
}
