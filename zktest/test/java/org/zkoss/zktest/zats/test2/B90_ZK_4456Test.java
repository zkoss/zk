/* B90_ZK_4456Test.java

		Purpose:
		
		Description:
		
		History:
				Tue Dec 17 12:34:41 CST 2019, Created by jameschu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B90_ZK_4456Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@button"));
		waitResponse();
		Assert.assertTrue(jq(".z-cascader-icon").hasClass("z-icon-times"));
	}
}
