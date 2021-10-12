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

/**
 * @author jameschu
 */
public class B90_ZK_4427Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@button"));
		waitResponse();
		Assert.assertEquals("10~100,70~100", getZKLog());
	}
}
