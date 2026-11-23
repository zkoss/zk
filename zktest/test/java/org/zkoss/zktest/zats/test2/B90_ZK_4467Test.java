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
public class B90_ZK_4467Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		click(jq(".z-groupbox-title"));
		waitResponse();
		Assert.assertEquals(jq(".z-groupbox-content").height(), jq("@div").height(), 3);
	}
}
