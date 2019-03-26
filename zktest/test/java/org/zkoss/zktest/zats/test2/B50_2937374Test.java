/* B50_2937374Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Mar 20 15:33:17 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B50_2937374Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse(true);
		Assert.assertFalse(jq(".z-combobox-popup").exists());
	}
}
