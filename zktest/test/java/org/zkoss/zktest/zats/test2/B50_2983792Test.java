/* B50_2983792Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Mar 22 11:49:28 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B50_2983792Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		waitResponse();
		Assert.assertEquals("Success!", getZKLog());
	}
}
