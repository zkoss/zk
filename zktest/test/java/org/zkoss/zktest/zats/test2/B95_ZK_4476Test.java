/* B95_ZK_4476Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Jul 9 16:47:36 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B95_ZK_4476Test extends WebDriverTestCase {
	@Test
	public void test() throws Exception {
		connect();
		waitResponse();
		click(jq("@button"));
		waitResponse();
		Assert.assertEquals("true", getZKLog());
	}
}
