/* B90_ZK_4463Test.java

		Purpose:
		
		Description:
		
		History:
				Thu Dec 19 17:46:06 CST 2019, Created by jameschu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author jameschu
 */
public class B90_ZK_4463Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		JQuery jqMark = jq(".z-multislider-mark").eq(2).find(".z-multislider-mark-label");
		click(jqMark);
		waitResponse();
		Assert.assertTrue(isZKLogAvailable());
		closeZKLog();
		click(jqMark);
		waitResponse();
		Assert.assertFalse(isZKLogAvailable());
	}
}
