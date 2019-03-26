/* B50_2945761Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Mar 20 17:19:31 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B50_2945761Test extends WebDriverTestCase {
	@Override
	protected String getFileExtension() {
		return ".zhtml";
	}

	@Test
	public void test() {
		connect();

		click(jq("@label"));
		waitResponse();
		click(jq("body"));
		waitResponse();

		Assert.assertFalse(jq("@popup").isVisible());
	}
}
