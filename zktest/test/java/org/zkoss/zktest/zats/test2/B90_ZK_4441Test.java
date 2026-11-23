/* B90_ZK_4441Test.java

		Purpose:
		
		Description:
		
		History:
				Wed Dec 04 15:53:06 CST 2019, Created by jameschu

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
public class B90_ZK_4441Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		Assert.assertFalse(jq("$c1").isVisible());
	}
}
