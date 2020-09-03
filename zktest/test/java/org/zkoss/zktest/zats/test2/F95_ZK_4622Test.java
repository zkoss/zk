/* F95_ZK_4622Test.java

	Purpose:
		
	Description:
		
	History:
		Thu, Sep 3, 2020 11:59:18 PM, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jameschu
 */
public class F95_ZK_4622Test extends WebDriverTestCase {
	@Test
	public void test() throws Exception {
		connect();
		Assert.assertEquals(0, jq(".z-listbox-header-border").length());
		Assert.assertEquals(0, jq(".z-grid-header-border").length());
		Assert.assertEquals(0, jq(".z-tree-header-border").length());
	}
}
