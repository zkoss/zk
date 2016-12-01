/** B80_ZK_3196Test.java.

	Purpose:
		
	Description:
		
	History:
 		Thu Dec 1 16:00:12 CST 2016, Created by jameschu

Copyright (C) 2016 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author jameschu
 *
 */
public class B80_ZK_3533Test extends WebDriverTestCase {
	
	@Test
	public void test() {
		connect();
		click(jq(".z-panel-maximize"));
		waitResponse();
		assertEquals(false, jq(".z-panel-maximize").attr("title").contains("2711"));
	}
}
