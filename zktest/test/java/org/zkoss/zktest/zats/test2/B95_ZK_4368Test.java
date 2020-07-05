/* B95_ZK_4368Test.java

	Purpose:
		
	Description:
		
	History:
		Fri, Jul 3, 2020 02:48:18 PM, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B95_ZK_4368Test extends WebDriverTestCase {
	@Test
	public void test() throws Exception {
		connect();
		waitResponse();
		Assert.assertEquals("1", jq(".z-listbox-header-border").css("z-index"));
	}
}
