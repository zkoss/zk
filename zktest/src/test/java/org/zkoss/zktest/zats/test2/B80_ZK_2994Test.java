/* B80_ZK_2994Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Dec 29 16:53:28 CST 2015, Created by Jameschu

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

import static org.junit.Assert.assertTrue;

/**
 * @author jameschu
 */
public class B80_ZK_2994Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@button").get(0));
		waitResponse();
		assertTrue(!jq(".z-messagebox").exists());
	}
}
