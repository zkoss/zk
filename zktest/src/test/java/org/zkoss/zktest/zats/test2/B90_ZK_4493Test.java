/* B90_ZK_4493Test.java

		Purpose:
		
		Description:
		
		History:
			Tue Feb 04 15:26:36 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B90_ZK_4493Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		Assert.assertTrue(jq("@columnlayout").hasClass("z-flex-row"));
	}
}
